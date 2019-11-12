package edu.usc.infolab.kien.blockchaingeospatial.app;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import edu.usc.infolab.kien.blockchaingeospatial.config.Constants;
import edu.usc.infolab.kien.blockchaingeospatial.contract.generated.PrivGeoMarktCommitmentStorage;
import edu.usc.infolab.kien.blockchaingeospatial.contract.generated.PrivGeoMarktSearchableStorage;
import edu.usc.infolab.kien.blockchaingeospatial.eth.*;
import edu.usc.infolab.kien.blockchaingeospatial.monitor.TransactionMonitor;
import edu.usc.infolab.kien.blockchaingeospatial.monitor.TransactionMonitorMessage;
import edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem.DataItemsMetadataContainer;
import edu.usc.infolab.kien.blockchaingeospatial.storage.offer.OfferStatus;
import edu.usc.infolab.kien.blockchaingeospatial.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import edu.usc.infolab.kien.blockchaingeospatial.contract.generated.PrivGeoMarkt;


public class PrivGeoMarktApp {

    private static final Logger logger = LoggerFactory.getLogger(PrivGeoMarktApp.class);

    private Web3j web3j;

    private ContractGasProvider contractGasProvider;

    private ContractHelper privGeoMarktHelper;
    private ContractHelper privGeoMarktCommitmentStorageHelper;
    private ContractHelper privGeoMarktSearchableStorageHelper;

    public PrivGeoMarktApp() {
        try {
            web3j = EthHelper.getWeb3jConnection();

            contractGasProvider = new DefaultGasProvider();

            privGeoMarktHelper = new PrivGeoMarktHelper(
                web3j,
                PrivGeoMarktHelper.DEFAULT_CONTRACT_ADDRESS,
                contractGasProvider);

            privGeoMarktCommitmentStorageHelper = new PrivGeoMarktCommitmentStorageHelper(
                web3j,
                PrivGeoMarktCommitmentStorageHelper.DEFAULT_CONTRACT_ADDRESS,
                contractGasProvider);

            privGeoMarktSearchableStorageHelper = new PrivGeoMarktSearchableStorageHelper(
                web3j,
                PrivGeoMarktSearchableStorageHelper.DEFAULT_CONTRACT_ADDRESS,
                contractGasProvider);

            if (!privGeoMarktHelper.isValid()
                || !privGeoMarktCommitmentStorageHelper.isValid()
                || !privGeoMarktSearchableStorageHelper.isValid())
            {
                throw new IOException("Contract binary invalid");
            }

        } catch (IOException e) {
            logger.error("Error creating helpers: ", e);
        }
    }

    public CompletableFuture<BigDecimal> runCreatePurchasePolicyAsync(int ownerIndex, double minPrice) {
        try {
            String ownerAddress = EthHelper.getOwnerAccounts().get(ownerIndex).getAddress();
            logger.info("ownerAddress: " + ownerAddress);

            PrivGeoMarkt ownerPrivGeoMarkt = (PrivGeoMarkt) privGeoMarktHelper
                .getOwnersContracts()
                .get(ownerIndex);

            logger.info("Creating purchase policy of " + ownerAddress + " to "  + minPrice + " ether");
            CompletableFuture<BigDecimal> receiptCompletableFuture = ownerPrivGeoMarkt
                .createPurchasePolicy(Convert.toWei(BigDecimal.valueOf(minPrice), Convert.Unit.ETHER).toBigInteger())
                .sendAsync().thenApply(receipt -> {
                    BigDecimal purchasePolicy = null;
                    try {
                        logger.info("Set my purchase policy: " + receipt.toString());
                        TransactionMonitor.addMessage(
                            new TransactionMonitorMessage(
                                PrivGeoMarkt.FUNC_CREATEPURCHASEPOLICY,
                                contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_CREATEPURCHASEPOLICY),
                                receipt));

                        BigInteger purchasePolicyInt = ownerPrivGeoMarkt.getPurchasePolicyOfOwner(ownerAddress).send();
                        purchasePolicy = Convert.fromWei(BigDecimal.valueOf(purchasePolicyInt.longValue()), Convert.Unit.ETHER);
                        logger.info("Purchase policy of " + ownerAddress + " is "  + purchasePolicy + " ether");
                    } catch (Exception ex) {
                        logger.error("Error creating purchase policy for owner index=" +  ownerIndex, ex);
                    }
                    return purchasePolicy;
                }).exceptionally(transactionReceipt -> null);
            return receiptCompletableFuture;
        } catch (Exception ex) {
            logger.error("Error creating purchase policy for owner index=" +  ownerIndex, ex);
            return null;
        }
    }

    public CompletableFuture<String> runSubmitCommitmentAsync(int ownerIndex, String dataAddress, String commitment) {
        try {
            String ownerAddress = EthHelper.getOwnerAccounts().get(ownerIndex).getAddress();
            PrivGeoMarktCommitmentStorage ownerPrivGeoMarktCommitmentStorage = (PrivGeoMarktCommitmentStorage)
                privGeoMarktCommitmentStorageHelper
                    .getOwnersContracts()
                    .get(ownerIndex);

            CompletableFuture<String> commitmentCompletableFuture = ownerPrivGeoMarktCommitmentStorage.submitCommitment(
                dataAddress.getBytes(), commitment.getBytes())
                .sendAsync().thenApply(receipt -> {
                    String submitedCommitment = null;
                    try {
                        logger.info("submitCommReceipt: " + receipt);
                        TransactionMonitor.addMessage(
                            new TransactionMonitorMessage(
                                PrivGeoMarktCommitmentStorage.FUNC_SUBMITCOMMITMENT,
                                contractGasProvider.getGasPrice(PrivGeoMarktCommitmentStorage.FUNC_SUBMITCOMMITMENT),
                                receipt));
                        Tuple2<byte[], BigInteger> commitmentTuple2 = ownerPrivGeoMarktCommitmentStorage.getCommitment(ownerAddress, dataAddress.getBytes()).send();
                        submitedCommitment = new String(commitmentTuple2.getValue1());
                        logger.info("Commitments: commitment=" + submitedCommitment + " , modified= " + commitmentTuple2.getValue2());
                    } catch (Exception ex) {
                        logger.error("Error submitting commitment for owner index=" +  ownerIndex, ex);
                    }
                    return submitedCommitment;
                }).exceptionally(transactionReceipt -> null);

            return commitmentCompletableFuture;
        } catch (Exception e) {
            logger.error("Error submitting commitment for owner index=" +  ownerIndex, e);
            return null;
        }
    }

    public CompletableFuture<String> runTradingAsync(
        int buyerIndex,
        int ownerIndex,
        long offerExpirationPeriod,
        long refundExpirationPeriod,
        double price,
        ArrayList<byte[]> dataAddressesToPurchase,
        byte[] encryptedKeys,
        BigInteger numKeys) {
        try {
            String ownerAddress = EthHelper.getOwnerAccounts().get(ownerIndex).getAddress();
            String buyerAddress = EthHelper.getBuyerAccounts().get(buyerIndex).getAddress();

            PrivGeoMarkt ownerPrivGeoMarkt = (PrivGeoMarkt) privGeoMarktHelper
                .getOwnersContracts()
                .get(ownerIndex);

            logger.info("buyerAddress: "+ buyerAddress);
            PrivGeoMarkt buyerPrivGeoMarkt = (PrivGeoMarkt) privGeoMarktHelper
                .getBuyersContracts()
                .get(buyerIndex);

            logger.info("Make offer");
            long currentTime = Utils.currentTimeInSeconds(); //time in seconds
            long offerExpiration = currentTime + offerExpirationPeriod;
            long refundExpiration = currentTime + refundExpirationPeriod;

            CompletableFuture<String> tradingCompletableFuture = buyerPrivGeoMarkt
                .makeOffer(ownerAddress,
                    dataAddressesToPurchase,
                    BigInteger.valueOf(offerExpiration),
                    BigInteger.valueOf(refundExpiration),
                    Convert.toWei(BigDecimal.valueOf(price), Convert.Unit.ETHER).toBigInteger())
                .sendAsync()
                .thenApply(receipt -> {
                    String result = null;
                    try {
                        TransactionMonitor.addMessage(
                            new TransactionMonitorMessage(
                                PrivGeoMarkt.FUNC_MAKEOFFER,
                                contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_MAKEOFFER),
                                receipt));
                        BigInteger numOffers = buyerPrivGeoMarkt.getNumOffers().send();
                        BigInteger indexInOfferList = numOffers.subtract(BigInteger.valueOf(1));

                        //check content
                        Tuple6<String, String, BigInteger, BigInteger, BigInteger, List<byte[]>> offerContributors = buyerPrivGeoMarkt.getOfferContributors(indexInOfferList).send();
                        String offerBuyerAddress = offerContributors.getValue1();
                        logger.info("offerBuyerAddress=" + offerBuyerAddress);

                        String offerOwnerAddress = offerContributors.getValue2();
                        logger.info("offerOwnerAddress=" + offerOwnerAddress);

                        BigInteger offerValue = offerContributors.getValue3();
                        logger.info("offerValue=" + offerValue.toString());

                        BigInteger inOwnerIndex = offerContributors.getValue4();
                        logger.info("inOwnerIndex=" + inOwnerIndex.toString());

                        BigInteger inBuyerIndex = offerContributors.getValue5();
                        logger.info("inBuyerIndex=" + inBuyerIndex.toString());

                        List<byte[]> offerDataAddresses = offerContributors.getValue6();
                        for (int i = 0; i < dataAddressesToPurchase.size(); i++) {
                            logger.info("offerDataAddresses.get(i)=" + new String(offerDataAddresses.get(i)));
                        }

                        Tuple2<BigInteger, BigInteger> offerTimes = ownerPrivGeoMarkt.getOfferExpirationTimes(indexInOfferList).send();
                        BigInteger offerExpirationStored = offerTimes.getValue1();
                        BigInteger refundExpirationStored = offerTimes.getValue2();

                        Tuple2<byte[], BigInteger> offerKeysTuple = ownerPrivGeoMarkt.getOfferKeys(indexInOfferList).send();
                        logger.info("offerKeys=" + new String(offerKeysTuple.getValue1()));
                        logger.info("offerNumKey=" + offerKeysTuple.getValue2().toString());


                        logger.info("Num offers = " + numOffers.toString());
                        logger.info(buyerPrivGeoMarkt.isValidOfferMapping(indexInOfferList).send().toString());

                        BigInteger offerStatus = buyerPrivGeoMarkt.getOfferStatus(indexInOfferList).send();
                        logger.info("offerStatus=" + OfferStatus.fromValue(offerStatus.intValue()));

                        logger.info("send keys");
                        ownerPrivGeoMarkt
                            .sendKeys(indexInOfferList, encryptedKeys, numKeys)
                            .sendAsync().handle((receipt1, throwable1) -> {
                            if (throwable1 == null) {
                                try {
                                    logger.info("send key receipt: " + receipt.toString());
                                    TransactionMonitor.addMessage(
                                        new TransactionMonitorMessage(
                                            PrivGeoMarkt.FUNC_SENDKEYS,
                                            contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_SENDKEYS),
                                            receipt));

                                    logger.info("offerStatus=" + OfferStatus.fromValue(buyerPrivGeoMarkt.getOfferStatus(indexInOfferList).send().intValue()));

                                    logger.info("offerKeys=" + new String(ownerPrivGeoMarkt.getOfferKeys(indexInOfferList).send().getValue1()));
                                    logger.info("offerNumKey=" + ownerPrivGeoMarkt.getOfferKeys(indexInOfferList).send().getValue2().toString());

                                    //try refund after offerExpirationPeriod
                                    logger.info("refundExpirationStored=" + refundExpirationStored.toString());
                                    logger.info("current time=" + Utils.currentTimeInSeconds());

                                    long extraWaitTime = 60;
                                    logger.info("wait until passing refundExpirationStored=" + refundExpirationStored.toString()
                                        + " extra " + extraWaitTime + " seconds "
                                        + " to withdraw");
                                    while (Utils.currentTimeInSeconds() < offerTimes.getValue2().longValue() + extraWaitTime) {
                                        Thread.sleep(5000);
                                    }

                                    logger.info("current time=" + Utils.currentTimeInSeconds());

                                    ownerPrivGeoMarkt.withdrawPayment(indexInOfferList).sendAsync().handle((receipt2, throwable2) -> {
                                        if (throwable2 == null) {
                                            logger.info("withdraw receipt:" + receipt.toString());
                                            TransactionMonitor.addMessage(
                                                new TransactionMonitorMessage(
                                                    PrivGeoMarkt.FUNC_WITHDRAWPAYMENT,
                                                    contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_WITHDRAWPAYMENT),
                                                    receipt));
                                        }

                                        return receipt;
                                    });

                                } catch (Exception e) {
                                    logger.error("Error sending key for buyer index=" + buyerIndex, e);
                                }
                            }
                            return receipt1;
                        });

                    } catch (Exception ex) {
                        logger.error("Error trading for buyer index=" +  buyerIndex, ex);
                    }

                    return result;
                })
                .exceptionally(transactionReceipt -> null);

            return tradingCompletableFuture;
        } catch (Exception e) {
            logger.error("Error running trading async for buyer" + buyerIndex, e);
            return null;
        }
    }



    public void runAsync() {
        if (!privGeoMarktHelper.isValid()
            || !privGeoMarktCommitmentStorageHelper.isValid()
            || !privGeoMarktSearchableStorageHelper.isValid()) {
            logger.error("Contract binary invalid");
            return;
        }



        int buyerIndex = 1;
        int ownerIndex = 0;
        int curatorIndex = 0;
        double minPrice = Constants.OWNER_MIN_PRICE;
        double lowerPrice = Constants.OWNER_MIN_PRICE - 0.1;
        double higherPrice = Constants.OWNER_MIN_PRICE + 0.1;
        long offerExpirationPeriod = 120; //should be enough time to send key
//        long refundExpirationPeriod = REFUND_EXPIRATION;
        long refundExpirationPeriod = 132;
        String dataAddress = Constants.DATA_ADDRESS;
        String commitment = Constants.COMMITMENT;
        byte[] encryptedKeys = Constants.ENCRYPTED_KEYS.getBytes();
        BigInteger numKeys = BigInteger.valueOf(Constants.NUM_ENCRYPTED_KEYS);
        String indexAddress = Constants.INDEX_ADDRESS;
        long startTime = Constants.START_TIME;
        long endTime = Constants.END_TIME;
        ArrayList<byte[]> dataAddressesToPurchase = new ArrayList<>();
        dataAddressesToPurchase.add(dataAddress.getBytes());

        List<CompletableFuture> completableFutures = new ArrayList<>();

        for (int i = 0; i < privGeoMarktHelper.getOwnersContracts().size(); i++) {
//            CompletableFuture<BigDecimal> policyCompletableFuture = runCreatePurchasePolicyAsync(i, minPrice);
//            if (policyCompletableFuture != null) {
//                completableFutures.add(policyCompletableFuture);
//            }

            CompletableFuture<String> commitmentCompletableFuture = runSubmitCommitmentAsync(i, dataAddress, commitment);
            if (commitmentCompletableFuture != null) {
                completableFutures.add(commitmentCompletableFuture);
            }
        }

//        for (int i = 0; i < privGeoMarktHelper.getBuyersContracts().size(); i++)  {
//            CompletableFuture<String> tradingCompletableFuture = runTradingAsync(
//                i,
//                0, offerExpirationPeriod, refundExpirationPeriod, higherPrice,
//                dataAddressesToPurchase,
//                encryptedKeys,
//                numKeys);
//        }

        CompletableFuture<Void> allCompleted = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]));
        try {
            allCompleted.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        logger.info("Transaction summary:");
        logger.info(TransactionMonitor.getSummary());
    }

    public void runTestContracts() throws Exception {
        if (!privGeoMarktHelper.isValid()
            || !privGeoMarktCommitmentStorageHelper.isValid()
            || !privGeoMarktSearchableStorageHelper.isValid())
        {
            logger.error("Contract binary invalid");
            return;
        }

        logger.info("---------- Starting demo ---------");

        int buyerIndex = 1;
        int ownerIndex = 0;
        int curatorIndex = 0;
        double minPrice = Constants.OWNER_MIN_PRICE;
        double lowerPrice = Constants.OWNER_MIN_PRICE - 0.1;
        double higherPrice = Constants.OWNER_MIN_PRICE + 0.1;
        long offerExpirationPeriod = 120; //should be enough time to send key
//        long refundExpirationPeriod = REFUND_EXPIRATION;
        long refundExpirationPeriod = 132;
        String dataAddress = Constants.DATA_ADDRESS;
        String commitment = Constants.COMMITMENT;
        byte[] encryptedKeys = Constants.ENCRYPTED_KEYS.getBytes();
        BigInteger numKeys = BigInteger.valueOf(Constants.NUM_ENCRYPTED_KEYS);
        String indexAddress = Constants.INDEX_ADDRESS;
        long startTime = Constants.START_TIME;
        long endTime = Constants.END_TIME;
        ArrayList<byte[]> dataAddressesToPurchase = new ArrayList<>();
        dataAddressesToPurchase.add(dataAddress.getBytes());


        String ownerAddress = EthHelper.getOwnerAccounts().get(ownerIndex).getAddress();

        logger.info("ownerAddress: " + ownerAddress);
        PrivGeoMarktCommitmentStorage ownerPrivGeoMarktCommitmentStorage = (PrivGeoMarktCommitmentStorage)
            privGeoMarktCommitmentStorageHelper
            .getOwnersContracts()
            .get(ownerIndex);

        PrivGeoMarkt ownerPrivGeoMarkt = (PrivGeoMarkt) privGeoMarktHelper
            .getOwnersContracts()
            .get(ownerIndex);

        String buyerAddress = EthHelper.getBuyerAccounts().get(buyerIndex).getAddress();

        logger.info("buyerAddress: "+ buyerAddress);
        PrivGeoMarkt buyerPrivGeoMarkt = (PrivGeoMarkt) privGeoMarktHelper
            .getBuyersContracts()
            .get(buyerIndex);

        TransactionReceipt receipt;

        logger.info("Creating purchase policy of " + ownerAddress + " to "  + minPrice + " ether");
        receipt = ownerPrivGeoMarkt
            .createPurchasePolicy(Convert.toWei(BigDecimal.valueOf(minPrice), Convert.Unit.ETHER).toBigInteger())
            .send();
        logger.info("Set my purchase policy: " + receipt.toString());
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarkt.FUNC_CREATEPURCHASEPOLICY,
                contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_CREATEPURCHASEPOLICY),
                receipt));
        BigInteger purchasePolicyInt = ownerPrivGeoMarkt.getPurchasePolicyOfOwner(ownerAddress).send();
        BigDecimal purchasePolicy = Convert.fromWei(BigDecimal.valueOf(purchasePolicyInt.longValue()), Convert.Unit.ETHER);
        logger.info("Purchase policy of " + ownerAddress + " is "  + purchasePolicy + " ether");


        logger.info("Creating purchase policy of " + ownerAddress + " to "  + minPrice + " ether");
        receipt = ownerPrivGeoMarkt
            .createPurchasePolicy(Convert.toWei(BigDecimal.valueOf(minPrice), Convert.Unit.ETHER).toBigInteger())
            .send();
        logger.info("Set my purchase policy: " + receipt);
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarkt.FUNC_CREATEPURCHASEPOLICY,
                contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_CREATEPURCHASEPOLICY),
                receipt));
        purchasePolicyInt = ownerPrivGeoMarkt.getPurchasePolicyOfOwner(ownerAddress).send();
        purchasePolicy = Convert.fromWei(BigDecimal.valueOf(purchasePolicyInt.longValue()), Convert.Unit.ETHER);
        logger.info("Purchase policy of " + ownerAddress + " is "  + purchasePolicy + " ether");


        logger.info("Delete purchase policy of " + ownerAddress + " to "  + minPrice + " ether");
        receipt = ownerPrivGeoMarkt
            .deletePurchasePolicy()
            .send();
        logger.info("Set my purchase policy: " + receipt.toString());
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarkt.FUNC_DELETEPURCHASEPOLICY,
                contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_DELETEPURCHASEPOLICY),
                receipt));
        purchasePolicyInt = ownerPrivGeoMarkt.getPurchasePolicyOfOwner(ownerAddress).send();
        purchasePolicy = Convert.fromWei(BigDecimal.valueOf(purchasePolicyInt.longValue()), Convert.Unit.ETHER);
        logger.info("Purchase policy of " + ownerAddress + " is "  + purchasePolicy + " ether");


        logger.info("Creating purchase policy of " + ownerAddress + " to "  + minPrice + " ether");
        receipt = ownerPrivGeoMarkt
            .createPurchasePolicy(Convert.toWei(BigDecimal.valueOf(minPrice), Convert.Unit.ETHER).toBigInteger())
            .send();
        logger.info("Set my purchase policy: " + receipt);
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarkt.FUNC_CREATEPURCHASEPOLICY,
                contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_CREATEPURCHASEPOLICY),
                receipt));
        purchasePolicyInt = ownerPrivGeoMarkt.getPurchasePolicyOfOwner(ownerAddress).send();
        purchasePolicy = Convert.fromWei(BigDecimal.valueOf(purchasePolicyInt.longValue()), Convert.Unit.ETHER);
        logger.info("Purchase policy of " + ownerAddress + " is "  + purchasePolicy + " ether");

        logger.info("-----------Owner submitting commitment-----------");

        receipt = ownerPrivGeoMarktCommitmentStorage.submitCommitment(
            dataAddress.getBytes(), commitment.getBytes()).send();
        logger.info("submitCommReceipt: " + receipt);
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarktCommitmentStorage.FUNC_SUBMITCOMMITMENT,
                contractGasProvider.getGasPrice(PrivGeoMarktCommitmentStorage.FUNC_SUBMITCOMMITMENT),
                receipt));

        Tuple2<byte[], BigInteger> commitmentTuple2 = ownerPrivGeoMarktCommitmentStorage.getCommitment(ownerAddress, dataAddress.getBytes()).send();
        logger.info("Commitments: commitment=" + new String(commitmentTuple2.getValue1()) + " , modified= " + commitmentTuple2.getValue2());

        logger.info("Resubmit commitment");
        receipt = ownerPrivGeoMarktCommitmentStorage.submitCommitment(
            dataAddress.getBytes(), commitment.getBytes()).send();
        logger.info("submitCommReceipt: " + receipt);
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarktCommitmentStorage.FUNC_SUBMITCOMMITMENT,
                contractGasProvider.getGasPrice(PrivGeoMarktCommitmentStorage.FUNC_SUBMITCOMMITMENT),
                receipt));

        commitmentTuple2 = ownerPrivGeoMarktCommitmentStorage.getCommitment(ownerAddress, dataAddress.getBytes()).send();
        logger.info("Commitments: commitment=" + new String(commitmentTuple2.getValue1()) + " , modified= " + commitmentTuple2.getValue2());


        logger.info("Delete commitment");
        receipt = ownerPrivGeoMarktCommitmentStorage.deleteCommitment(dataAddress.getBytes()).send();
        logger.info("submitCommReceipt: " + receipt);
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarktCommitmentStorage.FUNC_DELETECOMMITMENT,
                contractGasProvider.getGasPrice(PrivGeoMarktCommitmentStorage.FUNC_DELETECOMMITMENT),
                receipt));

        commitmentTuple2 = ownerPrivGeoMarktCommitmentStorage.getCommitment(ownerAddress, dataAddress.getBytes()).send();
        logger.info("Commitments: commitment=" + new String(commitmentTuple2.getValue1()) + " , modified= " + commitmentTuple2.getValue2());


        logger.info("Resubmit commitment");
        receipt = ownerPrivGeoMarktCommitmentStorage.submitCommitment(
            dataAddress.getBytes(), commitment.getBytes()).send();
        logger.info("submitCommReceipt: " + receipt);
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarktCommitmentStorage.FUNC_SUBMITCOMMITMENT,
                contractGasProvider.getGasPrice(PrivGeoMarktCommitmentStorage.FUNC_SUBMITCOMMITMENT),
                receipt));

        commitmentTuple2 = ownerPrivGeoMarktCommitmentStorage.getCommitment(ownerAddress, dataAddress.getBytes()).send();
        logger.info("Commitments: commitment=" + new String(commitmentTuple2.getValue1()) + " , modified= " + commitmentTuple2.getValue2());


        // Curator submitting searchable index
        logger.info("-----------Curator submitting searchable index-----------");


        String curatorAddress = EthHelper.getCuratorAccount().getAddress();
        logger.info("curatorAddress: " + curatorAddress);
        PrivGeoMarktSearchableStorage curatorPrivGeoMarktSearchableStorage = (PrivGeoMarktSearchableStorage)
            privGeoMarktSearchableStorageHelper
            .getCuratorsContracts()
            .get(curatorIndex);


        receipt = curatorPrivGeoMarktSearchableStorage
            .submitSearchableIndex(
                indexAddress.getBytes(),
                BigInteger.valueOf(startTime),
                BigInteger.valueOf(endTime))
            .send();
        logger.info("submitIndexAddressReceipt: " + receipt);
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarktSearchableStorage.FUNC_SUBMITSEARCHABLEINDEX,
                contractGasProvider.getGasPrice(PrivGeoMarktSearchableStorage.FUNC_SUBMITSEARCHABLEINDEX),
                receipt));

        Tuple3<BigInteger, BigInteger, BigInteger> searchableIndexInfoTimes = curatorPrivGeoMarktSearchableStorage
            .getSearchableIndexInfo(curatorAddress, indexAddress.getBytes())
            .send();
        logger.info("Times of searchable index info: startTime=" + searchableIndexInfoTimes.getValue1()
            + ", endTime=" + searchableIndexInfoTimes.getValue2()
            + ", modifiedTime=" + searchableIndexInfoTimes.getValue3());


        logger.info("Resubmit searchable index");
        receipt = curatorPrivGeoMarktSearchableStorage
            .submitSearchableIndex(
                indexAddress.getBytes(),
                BigInteger.valueOf(startTime),
                BigInteger.valueOf(endTime))
            .send();
        logger.info("submitIndexAddressReceipt: " + receipt);
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarktSearchableStorage.FUNC_SUBMITSEARCHABLEINDEX,
                contractGasProvider.getGasPrice(PrivGeoMarktSearchableStorage.FUNC_SUBMITSEARCHABLEINDEX),
                receipt));

        searchableIndexInfoTimes = curatorPrivGeoMarktSearchableStorage
            .getSearchableIndexInfo(curatorAddress, indexAddress.getBytes())
            .send();
        logger.info("Times of searchable index info: startTime=" + searchableIndexInfoTimes.getValue1()
            + ", endTime=" + searchableIndexInfoTimes.getValue2()
            + ", modifiedTime=" + searchableIndexInfoTimes.getValue3());

        logger.info("Delete searchable index");
        receipt = curatorPrivGeoMarktSearchableStorage
            .deleteSearchableIndex(indexAddress.getBytes())
            .send();
        logger.info("submitIndexAddressReceipt: " + receipt);
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarktSearchableStorage.FUNC_DELETESEARCHABLEINDEX,
                contractGasProvider.getGasPrice(PrivGeoMarktSearchableStorage.FUNC_DELETESEARCHABLEINDEX),
                receipt));

        searchableIndexInfoTimes = curatorPrivGeoMarktSearchableStorage
            .getSearchableIndexInfo(curatorAddress, indexAddress.getBytes())
            .send();
        logger.info("Times of searchable index info: startTime=" + searchableIndexInfoTimes.getValue1()
            + ", endTime=" + searchableIndexInfoTimes.getValue2()
            + ", modifiedTime=" + searchableIndexInfoTimes.getValue3());

        logger.info("Resubmit searchable index");
        receipt = curatorPrivGeoMarktSearchableStorage
            .submitSearchableIndex(
                indexAddress.getBytes(),
                BigInteger.valueOf(startTime),
                BigInteger.valueOf(endTime))
            .send();
        logger.info("submitIndexAddressReceipt: " + receipt);
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarktSearchableStorage.FUNC_SUBMITSEARCHABLEINDEX,
                contractGasProvider.getGasPrice(PrivGeoMarktSearchableStorage.FUNC_SUBMITSEARCHABLEINDEX),
                receipt));

        searchableIndexInfoTimes = curatorPrivGeoMarktSearchableStorage
            .getSearchableIndexInfo(curatorAddress, indexAddress.getBytes())
            .send();
        logger.info("Times of searchable index info: startTime=" + searchableIndexInfoTimes.getValue1()
            + ", endTime=" + searchableIndexInfoTimes.getValue2()
            + ", modifiedTime=" + searchableIndexInfoTimes.getValue3());


        logger.info("-----------Buyer purchase-----------");

        logger.info("Make offer");
        long currentTime = Utils.currentTimeInSeconds(); //time in seconds
        long offerExpiration = currentTime + offerExpirationPeriod;
        long refundExpiration = currentTime + refundExpirationPeriod;

        receipt = buyerPrivGeoMarkt
            .makeOffer(ownerAddress,
                dataAddressesToPurchase,
                BigInteger.valueOf(offerExpiration),
                BigInteger.valueOf(refundExpiration),
                Convert.toWei(BigDecimal.valueOf(higherPrice), Convert.Unit.ETHER).toBigInteger())
            .send();
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarkt.FUNC_MAKEOFFER,
                contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_MAKEOFFER),
                receipt));

        logger.info(receipt.toString());

        BigInteger numOffers = buyerPrivGeoMarkt.getNumOffers().send();
        BigInteger indexInOfferList = numOffers.subtract(BigInteger.valueOf(1));

        //check content
        Tuple6<String, String, BigInteger, BigInteger, BigInteger, List<byte[]>> offerContributors = buyerPrivGeoMarkt.getOfferContributors(indexInOfferList).send();
        String offerBuyerAddress = offerContributors.getValue1();
        logger.info("offerBuyerAddress=" + offerBuyerAddress);

        String offerOwnerAddress = offerContributors.getValue2();
        logger.info("offerOwnerAddress=" + offerOwnerAddress);

        BigInteger offerValue = offerContributors.getValue3();
        logger.info("offerValue=" + offerValue.toString());

        BigInteger inOwnerIndex = offerContributors.getValue4();
        logger.info("inOwnerIndex=" + inOwnerIndex.toString());

        BigInteger inBuyerIndex = offerContributors.getValue5();
        logger.info("inBuyerIndex=" + inBuyerIndex.toString());

        List<byte[]> offerDataAddresses = offerContributors.getValue6();
        for (int i = 0; i < dataAddressesToPurchase.size(); i++) {
            logger.info("offerDataAddresses.get(i)=" + new String(offerDataAddresses.get(i)));
        }

        Tuple2<BigInteger, BigInteger> offerTimes = ownerPrivGeoMarkt.getOfferExpirationTimes(indexInOfferList).send();
        BigInteger offerExpirationStored = offerTimes.getValue1();
        BigInteger refundExpirationStored = offerTimes.getValue2();

        Tuple2<byte[], BigInteger> offerKeysTuple = ownerPrivGeoMarkt.getOfferKeys(indexInOfferList).send();
        logger.info("offerKeys=" + new String(offerKeysTuple.getValue1()));
        logger.info("offerNumKey=" + offerKeysTuple.getValue2().toString());


        logger.info("Num offers = " + numOffers.toString());
        logger.info(buyerPrivGeoMarkt.isValidOfferMapping(indexInOfferList).send().toString());

        BigInteger offerStatus = buyerPrivGeoMarkt.getOfferStatus(indexInOfferList).send();
        logger.info("offerStatus=" + OfferStatus.fromValue(offerStatus.intValue()));



        logger.info("send keys");
        receipt = ownerPrivGeoMarkt
            .sendKeys(indexInOfferList, encryptedKeys, numKeys)
            .send();
        logger.info("send key receipt: " + receipt.toString());
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarkt.FUNC_SENDKEYS,
                contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_SENDKEYS),
                receipt));

        offerStatus = buyerPrivGeoMarkt.getOfferStatus(indexInOfferList).send();
        logger.info("offerStatus=" + OfferStatus.fromValue(offerStatus.intValue()));

        offerKeysTuple = ownerPrivGeoMarkt.getOfferKeys(indexInOfferList).send();
        logger.info("offerKeys=" + new String(offerKeysTuple.getValue1()));
        logger.info("offerNumKey=" + offerKeysTuple.getValue2().toString());

        logger.info("Cancel after sending keys");

        receipt = buyerPrivGeoMarkt
            .cancelOffer(indexInOfferList)
            .send();
        logger.info("fail cancel receipt:" + receipt.toString());
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarkt.FUNC_CANCELOFFER,
                contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_CANCELOFFER),
                receipt));

        offerStatus = buyerPrivGeoMarkt.getOfferStatus(indexInOfferList).send();
        logger.info("offerStatus=" + OfferStatus.fromValue(offerStatus.intValue()));

        //try refund after offerExpirationPeriod
        logger.info("refundExpirationStored=" + refundExpirationStored.toString());
        logger.info("current time=" + Utils.currentTimeInSeconds());

        long extraWaitTime = 60;
        logger.info("wait until passing refundExpirationStored=" + refundExpirationStored.toString()
            + " extra " + extraWaitTime + " seconds "
            + " to withdraw");
        while (Utils.currentTimeInSeconds() < offerTimes.getValue2().longValue() + extraWaitTime) {
            Thread.sleep(5000);
        }

        logger.info("current time=" + Utils.currentTimeInSeconds());

        receipt = ownerPrivGeoMarkt.withdrawPayment(indexInOfferList).send();
        logger.info("withdraw receipt:" + receipt.toString());
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarkt.FUNC_WITHDRAWPAYMENT,
                contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_WITHDRAWPAYMENT),
                receipt));

        offerStatus = buyerPrivGeoMarkt.getOfferStatus(indexInOfferList).send();
        logger.info("offerStatus=" + OfferStatus.fromValue(offerStatus.intValue()));

        receipt = buyerPrivGeoMarkt.refundOffer(indexInOfferList).send();
        logger.info("fail refund receipt:" + receipt.toString());
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarkt.FUNC_REFUNDOFFER,
                contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_REFUNDOFFER),
                receipt));

        offerStatus = buyerPrivGeoMarkt.getOfferStatus(indexInOfferList).send();
        logger.info("offerStatus=" + OfferStatus.fromValue(offerStatus.intValue()));

        logger.info("Transaction summary:");
        logger.info(TransactionMonitor.getSummary());
    }


    /**
     * Send funds from funding accounts to all others accounts
     *
     * @param etherValue
     * @throws Exception
     * @throws TransactionException
     * @throws InterruptedException
     */
    public void sendFundsToAllAccounts(int etherValue) throws InterruptedException, TransactionException, Exception {
        logger.info("Starting sendFundsToAllAccounts with " + etherValue + " each");

        // We start by creating a new web3j instance to connect to remote nodes on the network.
        // Note: if using web3j Android, use Web3jFactory.build(...
        Web3j web3j = EthHelper.getWeb3jConnection();

        // We then need to load our Ethereum wallet file
        Credentials fundingAccount = EthHelper.getFundingAccount();

        logger.info("Sending to curator account at: " + EthHelper.getCuratorAccount().getAddress());
        TransactionReceipt transferReceipt = Transfer.sendFunds(
                web3j,
                fundingAccount,
                EthHelper.getCuratorAccount().getAddress(),
                BigDecimal.valueOf(etherValue),
                Convert.Unit.ETHER)  //
                .send();

        logger.info("Transaction complete: " + transferReceipt.toString());
        logger.info("Current balance of this account: " + EthHelper.getBalance(web3j, EthHelper.getCuratorAccount().getAddress()));


        for (Credentials ownerCredentials: EthHelper.getOwnerAccounts()) {
            logger.info("Sending to owner account at: " + ownerCredentials.getAddress());
            transferReceipt = Transfer.sendFunds(
                    web3j,
                    fundingAccount,
                    ownerCredentials.getAddress(),
                    BigDecimal.valueOf(etherValue),
                    Convert.Unit.ETHER)  //
                    .send();

            logger.info("Transaction complete: " + transferReceipt.toString());
            logger.info("Current balance of this account: " + EthHelper.getBalance(web3j, ownerCredentials.getAddress()));
        }

        for (Credentials buyerCredentials: EthHelper.getBuyerAccounts()) {
            logger.info("Sending to buyer account at: " + buyerCredentials.getAddress());
            transferReceipt = Transfer.sendFunds(
                    web3j,
                    fundingAccount,
                    buyerCredentials.getAddress(),
                    BigDecimal.valueOf(etherValue),
                    Convert.Unit.ETHER)  //
                    .send();

            logger.info("Transaction complete: " + transferReceipt.toString());
            logger.info("Current balance of this account: " + EthHelper.getBalance(web3j, buyerCredentials.getAddress()));
        }

        logger.info("Current balance of this account: " + EthHelper.getBalance(web3j, fundingAccount.getAddress()));
    }

    public void runPlainMarkt() throws Exception {
        if (!privGeoMarktHelper.isValid()
            || !privGeoMarktCommitmentStorageHelper.isValid()
            || !privGeoMarktSearchableStorageHelper.isValid())
        {
            logger.error("Contract binary invalid");
            return;
        }

        logger.info("---------- Starting market with plain index ---------");

        OwnerApp ownerApp = new OwnerApp(
            web3j,
            contractGasProvider,
            privGeoMarktHelper,
            privGeoMarktCommitmentStorageHelper,
            privGeoMarktSearchableStorageHelper);

        CuratorApp curatorApp = new CuratorApp(
            web3j,
            contractGasProvider,
            privGeoMarktHelper,
            privGeoMarktCommitmentStorageHelper,
            privGeoMarktSearchableStorageHelper);

        BuyerApp buyerApp = new BuyerApp(
            web3j,
            contractGasProvider,
            privGeoMarktHelper,
            privGeoMarktCommitmentStorageHelper,
            privGeoMarktSearchableStorageHelper);

        DataItemsMetadataContainer dataItemsMetadataContainer = ownerApp.generateDataAndSubmitCommitment();

        ArrayList<DataItemsMetadataContainer> dataItemsMetadataContainerList = new ArrayList<>();
        dataItemsMetadataContainerList.add(dataItemsMetadataContainer);

        curatorApp.generateSearchableIndexAndSubmit(dataItemsMetadataContainerList);

        buyerApp.setOwnerApp(ownerApp);
        buyerApp.run();
    }
}
