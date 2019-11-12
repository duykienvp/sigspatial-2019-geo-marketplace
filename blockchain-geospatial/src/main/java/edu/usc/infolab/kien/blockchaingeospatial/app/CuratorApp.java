package edu.usc.infolab.kien.blockchaingeospatial.app;


import com.google.gson.Gson;
import edu.usc.infolab.kien.blockchaingeospatial.config.Config;
import edu.usc.infolab.kien.blockchaingeospatial.contract.generated.PrivGeoMarktSearchableStorage;
import edu.usc.infolab.kien.blockchaingeospatial.eth.*;
import edu.usc.infolab.kien.blockchaingeospatial.monitor.TransactionMonitor;
import edu.usc.infolab.kien.blockchaingeospatial.monitor.TransactionMonitorMessage;
import edu.usc.infolab.kien.blockchaingeospatial.storage.bound.AreaBound;
import edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem.DataItemsMetadataContainer;
import edu.usc.infolab.kien.blockchaingeospatial.storage.searchableindex.PlainIndex;
import edu.usc.infolab.kien.blockchaingeospatial.storage.searchableindex.PlainIndexFactory;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class CuratorApp {

    private static final Logger logger = LoggerFactory.getLogger(CuratorApp.class);

    private Web3j web3j;

    private ContractGasProvider contractGasProvider;

    private ContractHelper privGeoMarktHelper;
    private ContractHelper privGeoMarktCommitmentStorageHelper;
    private ContractHelper privGeoMarktSearchableStorageHelper;

    public static void main(String[] args) {
        try {
            logger.info("Starting CuratorApp...");
            (new CuratorApp()).generateSearchableIndexAndSubmit();
            logger.info("Transaction summary:");
            logger.info(TransactionMonitor.getSummary());
        } catch (Exception e) {
            logger.error("Error running CuratorApp: ", e);
        }
    }

    public CuratorApp() {
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

    public CuratorApp(Web3j web3j,
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

    /**
     * Generate searchable index from without any metadata
     * and submit
     * @throws Exception
     */
    public void generateSearchableIndexAndSubmit() throws Exception {
        generateSearchableIndexAndSubmit(new ArrayList<>());
    }

    /**
     * Generate searchable index from container metadata with all private data cleared
     * and submit
     * @param containerMetadatas list of container metadata
     * @throws Exception
     */
    public void generateSearchableIndexAndSubmit(List<DataItemsMetadataContainer> containerMetadatas) throws Exception {
        if (!privGeoMarktHelper.isValid()
            || !privGeoMarktCommitmentStorageHelper.isValid()
            || !privGeoMarktSearchableStorageHelper.isValid()) {
            logger.error("Contract binary invalid");
            return;
        }

        logger.info("---------- Starting curator ---------");

        int curatorIndex = Config.getCuratorIndex();

        // Curator submitting searchable index
        logger.info("-----------Curator submitting searchable index-----------");


        String curatorAddress = EthHelper.getCuratorAccount().getAddress();
        logger.info("curatorAddress: " + curatorAddress);
        PrivGeoMarktSearchableStorage curatorPrivGeoMarktSearchableStorage = (PrivGeoMarktSearchableStorage)
            privGeoMarktSearchableStorageHelper
                .getCuratorsContracts()
                .get(curatorIndex);

        // create searchable index
        PlainIndex searchableIndex = preparePlainIndex(curatorAddress,
            containerMetadatas,
            AreaBound.LOS_ANGELES);

        //store searchable index
        Gson gson = new Gson();
        String containerStr = gson.toJson(searchableIndex);
        logger.info(containerStr);
        String indexAddress = SwarmHelper.postContent(containerStr);
        if (indexAddress == null) {
            logger.error("Unable to save searchable index");
        } else {
            TransactionReceipt receipt = curatorPrivGeoMarktSearchableStorage
                .submitSearchableIndex(
                    Hex.decodeHex(indexAddress.toCharArray()),
                    BigInteger.valueOf(searchableIndex.getStartTime()),
                    BigInteger.valueOf(searchableIndex.getEndTime()))
                .send();
            logger.info("submitIndexAddressReceipt: " + receipt);
            TransactionMonitor.addMessage(
                new TransactionMonitorMessage(
                    PrivGeoMarktSearchableStorage.FUNC_SUBMITSEARCHABLEINDEX,
                    contractGasProvider.getGasPrice(PrivGeoMarktSearchableStorage.FUNC_SUBMITSEARCHABLEINDEX),
                    receipt));

            Tuple3<BigInteger, BigInteger, BigInteger> searchableIndexInfoTimes = curatorPrivGeoMarktSearchableStorage
                .getSearchableIndexInfo(curatorAddress, Hex.decodeHex(indexAddress.toCharArray()))
                .send();
            logger.info("Times of searchable index info: startTime=" + searchableIndexInfoTimes.getValue1()
                + ", endTime=" + searchableIndexInfoTimes.getValue2()
                + ", modifiedTime=" + searchableIndexInfoTimes.getValue3());
        }
    }

    /**
     * Prepare a plain index with all private data are cleared
     * @param curatorAddress curator address
     * @param containerMetadatas list of container metadata
     * @param areaBound area
     * @return  a plain index with all private data are cleared
     */
    public PlainIndex preparePlainIndex(String curatorAddress,
                                        List<DataItemsMetadataContainer> containerMetadatas,
                                        AreaBound areaBound) {
        PlainIndex searchableIndex = PlainIndexFactory.createPlainIndex(areaBound);

        searchableIndex.setCurator(curatorAddress);
        for (DataItemsMetadataContainer containerMetadata : containerMetadatas) {
            DataItemsMetadataContainer containerMetadataClone = (DataItemsMetadataContainer) containerMetadata.clone();
            //TODO: is this ok to let it this plain
//            containerMetadataClone.clearPrivateMetadata();

            searchableIndex.addDataItemMetadata(containerMetadataClone);
        }

        searchableIndex.prepare();

        return searchableIndex;
    }
}
