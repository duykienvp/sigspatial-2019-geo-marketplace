package edu.usc.infolab.kien.blockchaingeospatial.app;

import edu.usc.infolab.kien.blockchaingeospatial.config.Config;
import edu.usc.infolab.kien.blockchaingeospatial.contract.generated.PrivGeoMarktCommitmentStorage;
import edu.usc.infolab.kien.blockchaingeospatial.eth.*;
import edu.usc.infolab.kien.blockchaingeospatial.monitor.TransactionMonitor;
import edu.usc.infolab.kien.blockchaingeospatial.storage.CommitmentPublicParameters;
import edu.usc.infolab.kien.blockchaingeospatial.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BenchmarkApp {
    private static final Logger logger = LoggerFactory.getLogger(OwnerApp.class);

    private Web3j web3j;

    private ContractGasProvider contractGasProvider;

    private ContractHelper privGeoMarktHelper;
    private ContractHelper privGeoMarktCommitmentStorageHelper;
    private ContractHelper privGeoMarktSearchableStorageHelper;


    public static void main(String[] args) {
        try {
            logger.info("Starting OwnerApp...");

            BenchmarkApp benchmarkApp = new BenchmarkApp();
//            benchmarkApp.benchmarkSubmitCommitment();
            benchmarkApp.benchmarkCreateCommitmentPublicParameters();

            logger.info("Transaction summary:");
            logger.info(TransactionMonitor.getSummary());
        } catch (Exception e) {
            logger.error("Error running OwnerApp: ", e);
        }
    }

    public BenchmarkApp() {
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

    public void benchmarkSubmitCommitment() throws Exception {
        int ownerIndex = Config.getOwnerIndex();
        String ownerAddress = EthHelper.getOwnerAccounts().get(ownerIndex).getAddress();
        logger.info("ownerAddress: " + ownerAddress);

        String dataAddress = SwarmHelper.postContent("benchmark-commitment-content");
        if (dataAddress == null) {
            logger.error("Unable to save data");
            return;
        }

        PrivGeoMarktCommitmentStorage ownerPrivGeoMarktCommitmentStorage = (PrivGeoMarktCommitmentStorage)
            privGeoMarktCommitmentStorageHelper
                .getOwnersContracts()
                .get(ownerIndex);

        List<Integer> keySizes = Arrays.asList(96, 128, 192, 256);
        for (Integer commitmentLen: keySizes) {
            // commitment should be a HEX string
            // so its length should be double the benchmark len
            StringBuilder commitmentBuilder = new StringBuilder();
            for (int i = 0; i < commitmentLen; i++)
                commitmentBuilder.append("12");
            String commitment = commitmentBuilder.toString();

            logger.info("commitment:" + commitment);
            logger.info("-----------Owner submitting commitment-----------");
            TransactionReceipt receipt = OwnerHelper.submitCommitment(ownerPrivGeoMarktCommitmentStorage, dataAddress, commitment, contractGasProvider);
            logger.info("Receipt for len " + commitmentLen + ":" + receipt.toString());
            logger.info("Submitted commitment:" + OwnerHelper.getCommitment(ownerPrivGeoMarktCommitmentStorage, ownerAddress, dataAddress));

        }
    }

    public void benchmarkCreateCommitmentPublicParameters() throws Exception {
        int ownerIndex = Config.getOwnerIndex();
        String ownerAddress = EthHelper.getOwnerAccounts().get(ownerIndex).getAddress();
        logger.info("ownerAddress: " + ownerAddress);


        PrivGeoMarktCommitmentStorage ownerPrivGeoMarktCommitmentStorage = (PrivGeoMarktCommitmentStorage)
            privGeoMarktCommitmentStorageHelper
                .getOwnersContracts()
                .get(ownerIndex);

        List<Integer> keySizes = Arrays.asList(96, 128, 192, 256);
        int messageElementLen = 8;
        int prfSeedLen = 32;
        for (Integer keySize: keySizes) {
            // element should be a HEX string
            // so its length should be double the benchmark len
            StringBuilder valueBuilder = new StringBuilder();
            for (int i = 0; i < keySize; i++)
                valueBuilder.append("12");
            byte[] N = Utils.hexStringToBytes(valueBuilder.toString());
            byte[] a = Utils.hexStringToBytes(valueBuilder.toString());

            StringBuilder prfSeedBuilder = new StringBuilder();
            for (int i = 0; i < prfSeedLen; i++)
                prfSeedBuilder.append("34");
            byte[] s = Utils.hexStringToBytes(prfSeedBuilder.toString());

            StringBuilder messageLenBuilder = new StringBuilder();
            for (int i = 0; i < messageElementLen; i++)
                messageLenBuilder.append("56");
            byte[] c = Utils.hexStringToBytes(messageLenBuilder.toString());

            CommitmentPublicParameters commitmentPublicParameters = new CommitmentPublicParameters();
            commitmentPublicParameters.setN(N);
            commitmentPublicParameters.setA(a);
            commitmentPublicParameters.setS(s);
            commitmentPublicParameters.setC(c);

            logger.info("N:" + valueBuilder.toString());
            logger.info("a:" + valueBuilder.toString());
            logger.info("s:" + prfSeedBuilder.toString());
            logger.info("c:" + messageLenBuilder.toString());

            TransactionReceipt deleteCommitmentPPReceitp =
                OwnerHelper.deleteCommitmentPublicParameters(ownerPrivGeoMarktCommitmentStorage);
            logger.info("-----------Onwer deleted his previous commitment public parameters");
            logger.info("Current pp:" + OwnerHelper.getCommitmentPublicParameters(ownerPrivGeoMarktCommitmentStorage, ownerAddress).toString());

            logger.info("-----------Owner create commitment public parameters for keySize = " + keySize.toString());
            TransactionReceipt receipt = OwnerHelper.createCommitmentPublicParameters(
                commitmentPublicParameters,
                ownerPrivGeoMarktCommitmentStorage,
                contractGasProvider);
            logger.info("Receipt for keySize " + keySize.toString() + ":" + receipt.toString());
            logger.info("Submitted pp:" + OwnerHelper.getCommitmentPublicParameters(ownerPrivGeoMarktCommitmentStorage, ownerAddress).toString());
        }
    }

}
