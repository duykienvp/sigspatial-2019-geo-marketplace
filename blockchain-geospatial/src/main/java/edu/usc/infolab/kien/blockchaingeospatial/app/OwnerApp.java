package edu.usc.infolab.kien.blockchaingeospatial.app;

import com.google.gson.Gson;
import edu.usc.infolab.kien.blockchaingeospatial.config.Config;
import edu.usc.infolab.kien.blockchaingeospatial.config.Constants;
import edu.usc.infolab.kien.blockchaingeospatial.contract.generated.PrivGeoMarkt;
import edu.usc.infolab.kien.blockchaingeospatial.contract.generated.PrivGeoMarktCommitmentStorage;
import edu.usc.infolab.kien.blockchaingeospatial.crypto.AESUtils;
import edu.usc.infolab.kien.blockchaingeospatial.crypto.RSAUtils;
import edu.usc.infolab.kien.blockchaingeospatial.eth.*;
import edu.usc.infolab.kien.blockchaingeospatial.monitor.TransactionMonitor;
import edu.usc.infolab.kien.blockchaingeospatial.monitor.TransactionMonitorMessage;
import edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem.DataItem;
import edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem.DataItemMetadata;
import edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem.DataItemsContainer;
import edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem.DataItemsMetadataContainer;
import edu.usc.infolab.kien.blockchaingeospatial.storage.PurchasePolicy;
import edu.usc.infolab.kien.blockchaingeospatial.utils.Utils;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

public class OwnerApp {
    private static final Logger logger = LoggerFactory.getLogger(OwnerApp.class);

    private Web3j web3j;

    private ContractGasProvider contractGasProvider;

    private ContractHelper privGeoMarktHelper;
    private ContractHelper privGeoMarktCommitmentStorageHelper;
    private ContractHelper privGeoMarktSearchableStorageHelper;

    private byte[] encryptionKey = null;

    public static void main(String[] args) {
        try {
            logger.info("Starting OwnerApp...");
            (new OwnerApp()).generateDataAndSubmitCommitment();

            logger.info("Transaction summary:");
            logger.info(TransactionMonitor.getSummary());
        } catch (Exception e) {
            logger.error("Error running OwnerApp: ", e);
        }
    }

    public OwnerApp() {
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
                || !privGeoMarktSearchableStorageHelper.isValid()) {
                throw new IOException("Contract binary invalid");
            }

        } catch (IOException e) {
            logger.error("Error creating helpers: ", e);
        }
    }

    public OwnerApp(Web3j web3j,
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

    public DataItemsMetadataContainer generateDataAndSubmitCommitment() throws Exception {
        if (!privGeoMarktHelper.isValid()
            || !privGeoMarktCommitmentStorageHelper.isValid()
            || !privGeoMarktSearchableStorageHelper.isValid()) {
            logger.error("Contract binary invalid");
            return null;
        }

        logger.info("---------- Starting owner ---------");

        //TODO: event based

        int ownerIndex = Config.getOwnerIndex();
        int ownerNumItems = Config.getOwnerNumItems();
        double ownerMinPrice = Config.getOwnerMinPrice();
        long ownerStartTime = Config.getOwnerStartTime();
        long ownerEndTime = Config.getOwnerEndTime();

        String ownerAddress = EthHelper.getOwnerAccounts().get(ownerIndex).getAddress();
        logger.info("ownerAddress: " + ownerAddress);


        PrivGeoMarktCommitmentStorage ownerPrivGeoMarktCommitmentStorage = (PrivGeoMarktCommitmentStorage)
            privGeoMarktCommitmentStorageHelper
                .getOwnersContracts()
                .get(ownerIndex);

        PrivGeoMarkt ownerPrivGeoMarkt = (PrivGeoMarkt) privGeoMarktHelper
            .getOwnersContracts()
            .get(ownerIndex);

        DataItemsContainer container = generateRandomContainer(ownerAddress, ownerNumItems, ownerStartTime, ownerEndTime);

        Gson gson = new Gson();
        logger.info("container=" + container.toString());

        int keyLength = Config.getCryptoAesKeyLength();
        logger.info("keyLength=" + keyLength);
        encryptionKey = AESUtils.generateRandomBytes(keyLength);

        DataItemsContainer encryptedContainer = AESUtils.encryptDataContainer(encryptionKey, container);


        String encryptedContainerStr = gson.toJson(encryptedContainer);
        logger.info("encryptedContainerStr=" + encryptedContainerStr);

        String dataAddress = SwarmHelper.postContent(encryptedContainerStr);
        if (dataAddress == null) {
            logger.error("Unable to save data");
            encryptionKey = null;
            return null;
        }

        logger.info("dataAddress=" + dataAddress);
        String retrievedContainerString = SwarmHelper.getContent(dataAddress).toString();
        logger.info("retrievedContainerString=" + retrievedContainerString);
        logger.info("Is the retrieved data the same as original?: " + encryptedContainerStr.equals(retrievedContainerString));

        String commitment = Utils.removeLeadingHexStringPrefix(Hash.sha3String(encryptedContainerStr));
        logger.info("commitment:" + commitment);

        PurchasePolicy purchasePolicy = OwnerHelper.getPurchasePolicyOfOwner(ownerAddress, ownerPrivGeoMarkt);
        if (purchasePolicy == null || purchasePolicy.getMinPrice() <= Constants.DOUBLE_EPSILON) {
            logger.info("No current purchase policy ether");

            TransactionReceipt receipt;
            purchasePolicy = new PurchasePolicy(ownerMinPrice);

            logger.info("Creating purchase policy to " + purchasePolicy.toString() + " ether");
            receipt = OwnerHelper.createPurchasePolicy(
                purchasePolicy,
                ownerPrivGeoMarkt,
                contractGasProvider);
            logger.info(receipt.toString());
        }


        logger.info("-----------Owner submitting commitment-----------");
        OwnerHelper.submitCommitment(ownerPrivGeoMarktCommitmentStorage, dataAddress, commitment, contractGasProvider);
        logger.info("Submitted commitment:" + OwnerHelper.getCommitment(ownerPrivGeoMarktCommitmentStorage, ownerAddress, dataAddress));

        //save storage address
        DataItemsMetadataContainer metadata = container.getMetadata();
        metadata.setStorageAddress(dataAddress);
        return metadata;
    }


    /**
     * Generate data items randomly
     * @param owner
     * @param numItems
     * @param startTime
     * @param endTime
     * @return
     */
    public static DataItemsContainer generateRandomContainer(String owner, int numItems, long startTime, long endTime) {
        DataItemsContainer container = new DataItemsContainer();

        container.setOwner(owner);

        Random rand = new Random();

        double laMaxLat = Config.getGridLosAngelesMaxLat();
        double laMinLat = Config.getGridLosAngelesMinLat();
        double diffLat = Math.abs(laMaxLat - laMinLat);

        double laMaxLon = Config.getGridLosAngelesMaxLon();
        double laMinLon = Config.getGridLosAngelesMinLon();
        double diffLon = Math.abs(laMaxLon - laMinLon);

        for (int i = 0; i < numItems; i++) {
            DataItemMetadata metadata = new DataItemMetadata();
            metadata.setOwner(owner);
            metadata.setTimestamp(startTime + (rand.nextLong() % (endTime - startTime)));
            metadata.setLat(laMinLat + Math.random() * diffLat);
            metadata.setLon(laMinLon + Math.random() * diffLon);

            DataItem item = new DataItem();
            item.setMetadata(metadata);
            item.setContent(AESUtils.generateRandomBytes(10));
            item.getMetadata().setId(Utils.removeLeadingHexStringPrefix(
                Hex.encodeHexString(Hash.sha256(item.getContent()))));

            container.addDataItem(item);
        }

        return container;
    }

    /**
     * Send key for this indexInOfferList.
     * @param indexInOfferList
     */
    public void runSendKeysPlain(BigInteger indexInOfferList) throws Exception {
        logger.info("send keys");
        int ownerIndex = Config.getOwnerIndex();

        String ownerAddress = EthHelper.getOwnerAccounts().get(ownerIndex).getAddress();
        logger.info("ownerAddress: " + ownerAddress);

        PrivGeoMarkt ownerPrivGeoMarkt = (PrivGeoMarkt) privGeoMarktHelper
            .getOwnersContracts()
            .get(ownerIndex);

        Tuple6<String, String, BigInteger, BigInteger, BigInteger, List<byte[]>> offerContributors =
            ownerPrivGeoMarkt.getOfferContributors(indexInOfferList).send();
        String offerBuyerAddress = offerContributors.getValue1();
        logger.info("offerBuyerAddress=" + offerBuyerAddress);

        byte[] encryptionKey = this.encryptionKey;

        byte[] buyerPublickey = ownerPrivGeoMarkt.getPublicKeyOfBuyer(offerBuyerAddress).send();

        byte[] encryptedEncryptionKey = RSAUtils.encrypt(encryptionKey, buyerPublickey);

        BigInteger numKeys = BigInteger.valueOf(1);

        TransactionReceipt receipt = ownerPrivGeoMarkt
            .sendKeys(indexInOfferList, encryptedEncryptionKey, numKeys)
            .send();
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarkt.FUNC_SENDKEYS,
                contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_SENDKEYS),
                receipt));
    }

    /**
     * Withdraw payment
     * @param indexInOfferList
     * @throws Exception
     */
    public void runWithdraw(BigInteger indexInOfferList) throws Exception {
        int ownerIndex = Config.getOwnerIndex();

        PrivGeoMarkt ownerPrivGeoMarkt = (PrivGeoMarkt) privGeoMarktHelper
            .getOwnersContracts()
            .get(ownerIndex);

        Tuple2<BigInteger, BigInteger> offerTimes = ownerPrivGeoMarkt.getOfferExpirationTimes(indexInOfferList).send();
        BigInteger refundExpirationStored = offerTimes.getValue2();

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

        TransactionReceipt receipt = ownerPrivGeoMarkt.withdrawPayment(indexInOfferList).send();
        logger.info("withdraw receipt:" + receipt.toString());
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarkt.FUNC_WITHDRAWPAYMENT,
                contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_WITHDRAWPAYMENT),
                receipt));
    }
}
