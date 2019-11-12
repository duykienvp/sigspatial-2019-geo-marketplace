package edu.usc.infolab.kien.blockchaingeospatial.app;


import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Base64;

import edu.usc.infolab.kien.blockchaingeospatial.crypto.AESGCMEncryption;
import edu.usc.infolab.kien.blockchaingeospatial.crypto.AuthenticatedEncryptionException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import edu.usc.infolab.kien.blockchaingeospatial.config.Config;
import edu.usc.infolab.kien.blockchaingeospatial.contract.deployer.ContractDeployer;
import edu.usc.infolab.kien.blockchaingeospatial.contract.deployer.PrivGeoMarktDeployer;
import edu.usc.infolab.kien.blockchaingeospatial.eth.EthHelper;

/**
 * Hello world!
 */
public final class MainApp {
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
    private MainApp() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        Config.load();

//        test();

//         deployPrivGeoMarktStorage();
//         deployPrivGeoMarkt();
//        runSendFundsToAllAccounts(100);
         runPrivGeoMarktApp();
//        runPrivGeoMarktAppAsync();
//        runCuratorApp();
//        runBuyerApp();
//        runPlainMarkt();
    }

    public static void runPlainMarkt() {
        try {
            logger.info("Starting runPlainMarkt...");
            (new PrivGeoMarktApp()).runPlainMarkt();
        } catch (Exception e) {
            logger.error("Error running runPlainMarkt: ", e);
        }
    }

    public static void runCuratorApp() {
        try {
            logger.info("Starting CuratorApp...");
            (new CuratorApp()).generateSearchableIndexAndSubmit();
        } catch (Exception e) {
            logger.error("Error running CuratorApp: ", e);
        }
    }

    public static void runBuyerApp() {
        try {
            logger.info("Starting BuyerApp...");
            (new BuyerApp()).run();
        } catch (Exception e) {
            logger.error("Error running CuratorApp: ", e);
        }
    }

    public static void runPrivGeoMarktApp() {
        try {
            logger.info("Starting PrivGeoMarktApp...");
            (new PrivGeoMarktApp()).runTestContracts();
        } catch (Exception e) {
            logger.error("Error running PrivGeoMarktApp: ", e);
        }
    }

    public static void runPrivGeoMarktAppAsync() {
        try {
            logger.info("Starting PrivGeoMarktApp Async...");
            (new PrivGeoMarktApp()).runAsync();
        } catch (Exception e) {
            logger.error("Error running PrivGeoMarktApp: ", e);
        }
    }

    public static void runSendFundsToAllAccounts(int etherValue) {
        try {
            logger.info("Starting runSendFundsToAllAccounts...");
            (new PrivGeoMarktApp()).sendFundsToAllAccounts(etherValue);
        } catch (Exception e) {
            logger.error("Error running PrivGeoMarktApp: ", e);
        }
    }

    public static void deployPrivGeoMarkt() {
        ContractDeployer deployer = new PrivGeoMarktDeployer();
        deployContract(deployer);
    }

    /**
     * Deploy a contract using its deployer
     * @param deployer
     */
    public static void deployContract(ContractDeployer deployer) {
        try {
            deployer.deploy();
        } catch (Exception e) {
            logger.error("Error running deployContract: ", e);
        }
    }

    public static void runGreeterApp() {
        try {
            new GreeterApp().run();
        } catch (Exception e) {
            logger.error("Error running runGreeterApp: ", e);
        }
    }

    /**
     * Test sending funds
     */
    public static void testSendFund() {
        try {
            logger.info("Starting testSendFund...");

            // We start by creating a new web3j instance to connect to remote nodes on the network.
            // Note: if using web3j Android, use Web3jFactory.build(...
            Web3j web3j = EthHelper.getWeb3jConnection();

            // We then need to load our Ethereum wallet file
            Credentials credentials = EthHelper.getCredentials();

            logger.info("Sending 1 Wei (" + Convert.fromWei("1", Convert.Unit.ETHER).toPlainString() + " Ether)");

            String toAddress = "0x3de715086cd3628fc964d560d17388edfcc4e8d1";

            TransactionReceipt transferReceipt = Transfer.sendFunds(
                web3j,
                credentials,
                toAddress,
                BigDecimal.ONE,
                Convert.Unit.WEI)  //
                .send();

            logger.info("Transaction complete, view it at https://rinkeby.etherscan.io/tx/"
                + transferReceipt.getTransactionHash());
        } catch (Exception e) {
            logger.error("Error running testSendFund: ", e);
        }
    }

    public static void test() {
        try {
            byte[] data = Hex.decodeHex("7f5fd7b9d0037d6b196b4f438ff3112214ca6746a39673f46822e9ff4c162df1".toCharArray());
            System.out.println("len = " + data.length);
            System.out.println(data.clone());
            System.out.println(Hex.encodeHexString(data.clone()));

            SecureRandom secureRandom = new SecureRandom();
            byte[] key = new byte[16];
            secureRandom.nextBytes(key);

            String plainText = "This is a plain text. Bytes is a utility library that makes it easy to create, parse, transform, validate and convert byte arrays in Java. It supports endianness as well as immutability and mutability, so the caller may decide to favor performance";
            logger.info("plainText=" + plainText);
            String metadata = "This is just metadata.";

            AESGCMEncryption encryption = new AESGCMEncryption();
            byte[] cipherText = encryption.encrypt(key, plainText.getBytes(), metadata.getBytes());

            String cipherTextStr = Base64.getEncoder().encodeToString(cipherText);

            logger.info("cipherTextStr=" + cipherTextStr);

            byte[] decryptedText = encryption.decrypt(key, cipherText, metadata.getBytes());

            String decryptedTextStr = new String(decryptedText);

            logger.info("decryptedTextStr=" + decryptedTextStr);

        } catch (DecoderException | AuthenticatedEncryptionException e) {
            e.printStackTrace();
        }
    }
}
