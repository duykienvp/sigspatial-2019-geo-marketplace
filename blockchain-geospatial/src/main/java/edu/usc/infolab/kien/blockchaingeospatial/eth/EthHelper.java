package edu.usc.infolab.kien.blockchaingeospatial.eth;

import edu.usc.infolab.kien.blockchaingeospatial.config.Config;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Helper class for Ethereum network connections
 */
public class EthHelper {
    private static final Logger logger = LoggerFactory.getLogger(EthHelper.class);

    private static final String FUNDING_ACCOUNT_ADDRESS = "0x6a628798abeb39cf04505a0d27c632616743dd2d";
    private static final String CURATOR_ACCOUNT_ADDERSS = "0xcd4eb59e28a6701e74d06d52431d83cb4c8e0cb8";

    private static ArrayList<Credentials> allCredentials = new ArrayList<>();
    private static ArrayList<Credentials> ownerAccounts = new ArrayList<>();
    private static ArrayList<Credentials> buyerAccounts = new ArrayList<>();
    private static Credentials curatorAccount = null;
    private static Credentials fundingAccount = null;

    private static boolean credentialsLoaded = false;


    /**
     * Get Web3j connection to Ethereum network.
     *
     * Configuration should be loaded before calling this method.
     *
     * @return Web3j connection to Ethereum network
     * @throws IOException
     */
    public static Web3j getWeb3jConnection() throws IOException {
        Web3j web3j = Web3j.build(new HttpService(Config.getNetworkGeth()));
        logger.info("Connected to Ethereum client version: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());

        return web3j;
    }

    /**
     * Get the first credentials to connect to Ethereum network.
     *
     * @return credentials to connect to Ethereum network.
     * @throws IOException
     * @throws CipherException
     */
    public static Credentials getCredentials() throws IOException, CipherException {
        return getCredentials(0);
    }

    /**
     * Get credentials to connect to Ethereum network.
     *
     * Configuration should be loaded before calling this method
     *
     * @return credentials to connect to Ethereum network or {@code null} if error occurred
     * @throws IOException
     * @throws CipherException
     */
    public static Credentials getCredentials(int index) throws IndexOutOfBoundsException {
        if (!credentialsLoaded)
            prepareCredentials();

        return allCredentials.get(index);
    }


    public static void prepareCredentials() {
        if (credentialsLoaded) {
            return;
        }

        try {
            allCredentials = new ArrayList<>();

            ArrayList<File> keyFiles = new ArrayList<>(
                FileUtils.listFiles(
                    new File(Config.getWalletDir()),
                    new WildcardFileFilter("UTC*"),
                    null));
            Collections.sort(keyFiles);
//            logger.info(keyFiles.toString());

            for (File file : keyFiles) {
                Credentials credentials = WalletUtils.loadCredentials(Config.getWalletPassword(), file.getAbsolutePath());
                allCredentials.add(credentials);

                if (credentials.getAddress().equals(FUNDING_ACCOUNT_ADDRESS)) {
                    fundingAccount = credentials;
                }

                if (credentials.getAddress().equals(CURATOR_ACCOUNT_ADDERSS)) {
                    curatorAccount = credentials;
                }
            }

            if (fundingAccount == null) {
                logger.error("Unable to find funding account");
            } else {
                logger.info("Funding account address: " + fundingAccount.getAddress());
            }

            if (curatorAccount == null) {
                logger.error("Unable to find curator account");
            } else {
                logger.info("Curator account address: " + curatorAccount.getAddress());
            }

            //Divide other accounts to owners and buyers
            // num onwer = half of (num all account - funding - curator)
            int numOwner = (allCredentials.size() - 2) / 2;
            for (int i = 0; i < allCredentials.size(); i++) {
                Credentials credentials = allCredentials.get(i);
                if (credentials.getAddress().equals(FUNDING_ACCOUNT_ADDRESS)
                    || credentials.getAddress().equals(CURATOR_ACCOUNT_ADDERSS)) {
                    continue;
                }

                // not funding or curator account
                if (ownerAccounts.size() < numOwner) {
                    ownerAccounts.add(credentials);
                } else {
                    buyerAccounts.add(credentials);
                }
            }

            logger.info("There are " + ownerAccounts.size() + " owner accounts");
            logger.info("There are " + buyerAccounts.size() + " buyer accounts");


            credentialsLoaded = true;

            logger.info("Loaded " + allCredentials.size() + " credentials");
        } catch (Exception e) {
            logger.error("Error preparing credential", e);
        }
    }

    public static Credentials getFundingAccount() {
        if (!credentialsLoaded) {
            prepareCredentials();
        }

        return fundingAccount;
    }

    public static Credentials getCuratorAccount() {
        if (!credentialsLoaded) {
            prepareCredentials();
        }

        return curatorAccount;
    }

    public static ArrayList<Credentials> getOwnerAccounts() {
        if (!credentialsLoaded) {
            prepareCredentials();
        }

        return ownerAccounts;
    }

    public static ArrayList<Credentials> getBuyerAccounts() {
        if (!credentialsLoaded) {
            prepareCredentials();
        }

        return buyerAccounts;
    }

    public static BigDecimal getBalance(Web3j web3j, String address) throws IOException {
        if (web3j == null || address == null) {
            return null;
        }
        BigInteger balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
        return Convert.fromWei(String.valueOf(balance), Convert.Unit.ETHER);
    }

    /**
     * Conver String to Eth Byte32
     * @param string
     * @return
     */
    public static Bytes32 converStringToBytes32(String string) {
        byte[] byteValue = string.getBytes();
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return new Bytes32(byteValueLen32);
    }

    /**
     * Convert Eth Byte32 to String
     * @param value
     * @return
     */
    public static String convertByte32ToString(Bytes32 value) {
        return StringUtils.newStringUsAscii(value.getValue());
    }
}
