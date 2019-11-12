package edu.usc.infolab.kien.blockchaingeospatial.app;


import com.google.gson.Gson;
import edu.usc.infolab.kien.blockchaingeospatial.config.Config;
import edu.usc.infolab.kien.blockchaingeospatial.contract.generated.PrivGeoMarkt;
import edu.usc.infolab.kien.blockchaingeospatial.contract.generated.PrivGeoMarktSearchableStorage;
import edu.usc.infolab.kien.blockchaingeospatial.crypto.AESUtils;
import edu.usc.infolab.kien.blockchaingeospatial.crypto.RSAKeyPairGenerator;
import edu.usc.infolab.kien.blockchaingeospatial.crypto.RSAUtils;
import edu.usc.infolab.kien.blockchaingeospatial.eth.*;
import edu.usc.infolab.kien.blockchaingeospatial.monitor.TransactionMonitor;
import edu.usc.infolab.kien.blockchaingeospatial.monitor.TransactionMonitorMessage;
import edu.usc.infolab.kien.blockchaingeospatial.storage.bound.AreaBound;
import edu.usc.infolab.kien.blockchaingeospatial.storage.bound.Bound;
import edu.usc.infolab.kien.blockchaingeospatial.storage.bound.BoundFactory;
import edu.usc.infolab.kien.blockchaingeospatial.storage.bound.BoundUtil;
import edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem.DataItem;
import edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem.DataItemMetadata;
import edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem.DataItemsContainer;
import edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem.DataItemsMetadataContainer;
import edu.usc.infolab.kien.blockchaingeospatial.storage.searchableindex.PlainIndex;
import edu.usc.infolab.kien.blockchaingeospatial.utils.Utils;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BuyerApp {

    private static final Logger logger = LoggerFactory.getLogger(BuyerApp.class);

    private Web3j web3j;

    private ContractGasProvider contractGasProvider;

    private ContractHelper privGeoMarktHelper;
    private ContractHelper privGeoMarktCommitmentStorageHelper;
    private ContractHelper privGeoMarktSearchableStorageHelper;



    private OwnerApp ownerApp;

    public static void main(String[] args) {
        try {
            logger.info("Starting BuyerApp...");
            (new BuyerApp()).run();
        } catch (Exception e) {
            logger.error("Error running CuratorApp: ", e);
        }
    }

    public BuyerApp() {
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

    public BuyerApp(Web3j web3j,
                      ContractGasProvider contractGasProvider,
                      ContractHelper privGeoMarktHelper,
                      ContractHelper privGeoMarktCommitmentStorageHelper,
                      ContractHelper privGeoMarktSearchableStorageHelper) {
        try {
            this.web3j = web3j;

            this.contractGasProvider = contractGasProvider;

            this.privGeoMarktHelper = privGeoMarktHelper;

            this.privGeoMarktCommitmentStorageHelper = privGeoMarktCommitmentStorageHelper;

            this.privGeoMarktSearchableStorageHelper = privGeoMarktSearchableStorageHelper;

            if (!privGeoMarktHelper.isValid()
                || !privGeoMarktCommitmentStorageHelper.isValid()
                || !privGeoMarktSearchableStorageHelper.isValid()) {
                throw new IOException("Contract binary invalid");
            }

        } catch (IOException e) {
            logger.error("Error creating helpers: ", e);
        }
    }

    public void run() throws Exception {
        if (!privGeoMarktHelper.isValid()
            || !privGeoMarktCommitmentStorageHelper.isValid()
            || !privGeoMarktSearchableStorageHelper.isValid()) {
            logger.error("Contract binary invalid");
            return;
        }

        logger.info("---------- Starting buyer ---------");

        int buyerIndex = Config.getBuyerIndex();

        logger.info("-----------Buyer get searchable index-----------");

        String buyerAddress = EthHelper.getBuyerAccounts().get(buyerIndex).getAddress();
        logger.info("buyerAddress: " + buyerAddress);
        PrivGeoMarktSearchableStorage buyerPrivGeoMarktSearchableStorage = (PrivGeoMarktSearchableStorage)
            privGeoMarktSearchableStorageHelper
            .getBuyersContracts()
            .get(buyerIndex);

        PrivGeoMarkt buyerPrivGeoMarkt = (PrivGeoMarkt) privGeoMarktHelper
            .getBuyersContracts()
            .get(buyerIndex);

        // submit public key if needed
        byte[] publicKey =  buyerPrivGeoMarkt.buyerPublicKey(buyerAddress).send();
        logger.info("publicKey=" + Arrays.toString(publicKey));
        if (publicKey.length == 0) {
            logger.info("NO public key.");
            RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
            RSAKeyPairGenerator.writeToFile(
                RSAUtils.generatePrivateKeyFile(Config.getDataRsaPrivateKeyFile(), buyerAddress),
                keyPairGenerator.getPrivateKey().getEncoded());
            RSAKeyPairGenerator.writeToFile(
                RSAUtils.generatePublicKeyFile(Config.getDataRsaPubKeyFile(), buyerAddress),
                keyPairGenerator.getPublicKey().getEncoded());

            publicKey = keyPairGenerator.getPublicKey().getEncoded();

            logger.info("Set public key");
            TransactionReceipt receipt = buyerPrivGeoMarkt.setPublicKey(publicKey).send();
            TransactionMonitor.addMessage(
                new TransactionMonitorMessage(
                    PrivGeoMarkt.FUNC_SETPUBLICKEY,
                    contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_SETPUBLICKEY),
                    receipt));
        }

        int numCurator = buyerPrivGeoMarktSearchableStorage.getCuratorCount().send().intValue();
        if (numCurator == 0) {
            logger.info("No curator");
            return;
        }

        logger.info("Getting the last searchable index file of the 1st curator");
        String curatorAddress = buyerPrivGeoMarktSearchableStorage.curatorIndices(BigInteger.valueOf(0)).send();
        logger.info("curatorAddress = " + curatorAddress);
        int numSearchables =  buyerPrivGeoMarktSearchableStorage.getNumSearchableIndexInfos(curatorAddress).send().intValue();
        String indexAddress = Hex.encodeHexString(buyerPrivGeoMarktSearchableStorage.searchableIndexInfosIndexMapping(
            curatorAddress,
            BigInteger.valueOf(numSearchables - 1)).send());
        logger.info("indexAddress = " + indexAddress);

        Tuple3<BigInteger, BigInteger, BigInteger> searchableIndexInfoTimes = buyerPrivGeoMarktSearchableStorage
            .getSearchableIndexInfo(curatorAddress, Hex.decodeHex(indexAddress.toCharArray()))
            .send();
        logger.info("Times of searchable index info: startTime=" + searchableIndexInfoTimes.getValue1()
            + ", endTime=" + searchableIndexInfoTimes.getValue2()
            + ", modifiedTime=" + searchableIndexInfoTimes.getValue3());

        boolean withData = false;
        Gson gson = new Gson();
        Bound bound = BoundFactory.createBound(AreaBound.LOS_ANGELES);
        if (withData) {
            String searchableIndexStr = SwarmHelper.getContent(indexAddress).toString();
            logger.info("Content of file:" + searchableIndexStr);

            gson = new Gson();
            PlainIndex plainIndex = gson.fromJson(searchableIndexStr, PlainIndex.class);
            logger.info("Converted to object: " + plainIndex.toString());

            //create buyer's query

            bound.setMaxLat(bound.getMinLat() + bound.getLatLength() / 2); //only search half of the space

            List<DataItemsMetadataContainer> matchedContainers = new ArrayList<>();
            for (DataItemsMetadataContainer dataItemsMetadataContainer : plainIndex.getMetadataList()) {
                for (DataItemMetadata dataItemMetadata: dataItemsMetadataContainer.getDataItemMetadatas()) {
                    if (BoundUtil.isInside(bound, dataItemMetadata.getLat(), dataItemMetadata.getLon())) {
                        matchedContainers.add(dataItemsMetadataContainer);
                        break;
                    }
                }
            }

            logger.info("Matched addresses: " + matchedContainers.size());

            if (matchedContainers.isEmpty()) {
                return;
            }

        }


        logger.info("-----------Buyer purchase the 1ST data-----------");

        logger.info("Make offer");
        int matchedContrainerIndexToBuy = 0;
        double offerPrice = Config.getOwnerMinPrice() + 1;
        long offerExpirationPeriod = 120; //should be enough time to send key
//        long refundExpirationPeriod = REFUND_EXPIRATION;
        long refundExpirationPeriod = 132;
        long currentTime = Utils.currentTimeInSeconds(); //time in seconds
        long offerExpiration = currentTime + offerExpirationPeriod;
        long refundExpiration = currentTime + refundExpirationPeriod;
        String ownerAddress = "0xcd4eb59e28a6701e74d06d52431d83cb4c8e0cb8";
        String dataAddress = "6b1c64c2e7c03cf33f9eb3ba10a6d6ccb95cca3cb8277bca2f6ac310ada8ba20";
//        String ownerAddress = matchedContainers.get(matchedContrainerIndexToBuy).getOwner();
//        String dataAddress = matchedContainers.get(matchedContrainerIndexToBuy).getStorageAddress();
        List<byte[]> dataAddressesToPurchase = new ArrayList<>();
        dataAddressesToPurchase.add(Hex.decodeHex(dataAddress.toCharArray()));

        TransactionReceipt receipt = buyerPrivGeoMarkt
            .makeOffer(ownerAddress,
                dataAddressesToPurchase,
                BigInteger.valueOf(offerExpiration),
                BigInteger.valueOf(refundExpiration),
                Convert.toWei(BigDecimal.valueOf(offerPrice), Convert.Unit.ETHER).toBigInteger())
            .send();
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarkt.FUNC_MAKEOFFER,
                contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_MAKEOFFER),
                receipt));

        logger.info(receipt.toString());

        BigInteger numOffers = buyerPrivGeoMarkt.getNumOffers().send();
        BigInteger indexInOfferList = numOffers.subtract(BigInteger.valueOf(1));


        //ask owner to send key
        //TODO: event based
        ownerApp.runSendKeysPlain(indexInOfferList);

        //get key
        Tuple2<byte[], BigInteger> offerKeysTuple = buyerPrivGeoMarkt.getOfferKeys(indexInOfferList).send();
        byte[] encryptionKey = RSAUtils.decrypt(
            offerKeysTuple.getValue1(),
            RSAKeyPairGenerator.getFromFile(
                RSAUtils.generatePrivateKeyFile(
                    Config.getDataRsaPrivateKeyFile(),
                    buyerAddress)));
        logger.info("offerKeys=" + Arrays.toString(encryptionKey));
        logger.info("offerNumKey=" + offerKeysTuple.getValue2().toString());

        if (withData) {
            //download data
            List<DataItem> foundItems = new ArrayList<>();
            DataItemsContainer container = gson.fromJson(SwarmHelper.getContent(dataAddress).toString(), DataItemsContainer.class);
            for (DataItem item: container.getDataItems()) {
                DataItem decryptedItem = AESUtils.decryptDataItem(encryptionKey, item);
                if (BoundUtil.isInside(bound, decryptedItem.getMetadata().getLat(), decryptedItem.getMetadata().getLon())) {
                    foundItems.add(decryptedItem);
                }
            }
            logger.info("foundItems=" + foundItems.toString());
        }



        ownerApp.runWithdraw(indexInOfferList);

        logger.info("Transaction summary:");
        logger.info(TransactionMonitor.getSummary());
    }

    public OwnerApp getOwnerApp() {
        return ownerApp;
    }

    public void setOwnerApp(OwnerApp ownerApp) {
        this.ownerApp = ownerApp;
    }
}
