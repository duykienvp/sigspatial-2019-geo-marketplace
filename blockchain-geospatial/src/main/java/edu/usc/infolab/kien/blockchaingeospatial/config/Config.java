package edu.usc.infolab.kien.blockchaingeospatial.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration file
 */
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    public static final String CONFIG_FILE = "config.properties";

    public static final String KEY_NETWORK_GETH = "network.geth";
    public static final String KEY_NETWORK_SWARM = "network.swarm";

    public static final String KEY_WALLET_DIR = "wallet.dir";
    public static final String KEY_WALLET_PASSWORD = "wallet.password";

    public static final String KEY_GRID_LOS_ANGELES_MAX_LAT = "grid.losangeles.maxlat";
    public static final String KEY_GRID_LOS_ANGELES_MIN_LAT = "grid.losangeles.minlat";
    public static final String KEY_GRID_LOS_ANGELES_MAX_LON = "grid.losangeles.maxlon";
    public static final String KEY_GRID_LOS_ANGELES_MIN_LON = "grid.losangeles.minlon";

    public static final String KEY_OWNER_INDEX = "owner.index";
    public static final String KEY_OWNER_NUM_ITEMS = "owner.numitems";
    public static final String KEY_OWNER_MIN_PRICE = "owner.minprice";
    public static final String KEY_OWNER_START_TIME = "owner.startTime";
    public static final String KEY_OWNER_END_TIME = "owner.endTime";

    public static final String KEY_CURATOR_INDEX = "curator.index";
    public static final String KEY_CURATOR_INDEX_ADDRESS = "curator.indexaddress";
    public static final String KEY_CURATOR_START_TIME = "curator.starttime";
    public static final String KEY_CURATOR_END_TIME = "curator.endtime";

    public static final String KEY_BUYER_INDEX = "buyer.index";

    public static final String KEY_CRYPTO_AES_KEY_LENGTH = "crypto.aes.keylength";
    public static final String KEY_CRYPTO_RSA_KEY_LENGTH = "crypto.rsa.keylength";

    public static final String KEY_DATA_FOLDER = "data.folder";
    public static final String KEY_DATA_RSA_PRIVATE_KEY_FILE = "data.rsa.privatekeyfile";
    public static final String KEY_DATA_RSA_PUBLIC_KEY_FILE = "data.rsa.publickeyfile";


    private static String networkGeth = "http://127.0.0.1:8545";
    private static String networkSwarm = "http://127.0.0.1:8500";

    private static String walletDir = "";
    private static String walletPassword = "";

    private static double gridLosAngelesMaxLat = 0;
    private static double gridLosAngelesMinLat = 0;
    private static double gridLosAngelesMaxLon = 0;
    private static double gridLosAngelesMinLon = 0;


    private static int ownerIndex = 0;
    private static int ownerNumItems = 0;
    private static double ownerMinPrice = 0;
    private static long ownerStartTime = 0;
    private static long ownerEndTime = 0;

    private static int curatorIndex = 0;
    private static String curatorIndexAddress = "";
    private static long curatorStartTime = 0;
    private static long curatorEndTime = 0;

    private static int buyerIndex = 0;

    private static int cryptoAesKeyLength = 32;
    private static int cryptoRsaKeyLength = 2048;

    private static String dataFolder = ".";

    private static String dataRsaPrivateKeyFile = "rsa";
    private static String dataRsaPubKeyFile = "rsa.pub";

    private static boolean loaded = false;

    private static boolean loading = false;



    /**
     * Load configuration from files
     */
    public static void load() {
        try {
            logger.info("Start loading address configuration");
            if (loading)
                return;
            loading = true;

            Properties properties = new Properties();
            // properties.load(new FileInputStream(CONFIG_FILE));
            properties.load(Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE));

            if (properties.containsKey(KEY_NETWORK_GETH)) {
                setNetworkGeth(properties.getProperty(KEY_NETWORK_GETH));
            }
            if (properties.containsKey(KEY_NETWORK_SWARM)) {
                setNetworkSwarm(properties.getProperty(KEY_NETWORK_SWARM));
            }

            if (properties.containsKey(KEY_WALLET_DIR)) {
                setWalletDir(properties.getProperty(KEY_WALLET_DIR));
            }
            if (properties.containsKey(KEY_WALLET_PASSWORD)) {
                setWalletPassword(properties.getProperty(KEY_WALLET_PASSWORD));
            }

            if (properties.containsKey(KEY_GRID_LOS_ANGELES_MAX_LAT)) {
                setGridLosAngelesMaxLat(Double.valueOf(properties.getProperty(KEY_GRID_LOS_ANGELES_MAX_LAT)));
            }

            if (properties.containsKey(KEY_GRID_LOS_ANGELES_MIN_LAT)) {
                setGridLosAngelesMinLat(Double.valueOf(properties.getProperty(KEY_GRID_LOS_ANGELES_MIN_LAT)));
            }

            if (properties.containsKey(KEY_GRID_LOS_ANGELES_MAX_LON)) {
                setGridLosAngelesMaxLon(Double.valueOf(properties.getProperty(KEY_GRID_LOS_ANGELES_MAX_LON)));
            }

            if (properties.containsKey(KEY_GRID_LOS_ANGELES_MIN_LON)) {
                setGridLosAngelesMinLon(Double.valueOf(properties.getProperty(KEY_GRID_LOS_ANGELES_MIN_LON)));
            }

            if (properties.containsKey(KEY_OWNER_INDEX)) {
                setOwnerIndex(Integer.valueOf(properties.getProperty(KEY_OWNER_INDEX)));
            }

            if (properties.containsKey(KEY_OWNER_NUM_ITEMS)) {
                setOwnerNumItems(Integer.valueOf(properties.getProperty(KEY_OWNER_NUM_ITEMS)));
            }

            if (properties.containsKey(KEY_OWNER_MIN_PRICE)) {
                setOwnerMinPrice(Double.valueOf(properties.getProperty(KEY_OWNER_MIN_PRICE)));
            }

            if (properties.containsKey(KEY_OWNER_START_TIME)) {
                setOwnerStartTime(Long.valueOf(properties.getProperty(KEY_OWNER_START_TIME)));
            }

            if (properties.containsKey(KEY_OWNER_END_TIME)) {
                setOwnerEndTime(Long.valueOf(properties.getProperty(KEY_OWNER_END_TIME)));
            }

            if (properties.containsKey(KEY_CURATOR_INDEX)) {
                setCuratorIndex(Integer.valueOf(properties.getProperty(KEY_CURATOR_INDEX)));
            }

            if (properties.containsKey(KEY_CURATOR_INDEX_ADDRESS)) {
                setCuratorIndexAddress(properties.getProperty(KEY_CURATOR_INDEX_ADDRESS));
            }

            if (properties.containsKey(KEY_CURATOR_START_TIME)) {
                setCuratorStartTime(Long.valueOf(properties.getProperty(KEY_CURATOR_START_TIME)));
            }

            if (properties.containsKey(KEY_CURATOR_END_TIME)) {
                setCuratorEndTime(Long.valueOf(properties.getProperty(KEY_CURATOR_END_TIME)));
            }

            if (properties.containsKey(KEY_BUYER_INDEX)) {
                setBuyerIndex(Integer.valueOf(properties.getProperty(KEY_BUYER_INDEX)));
            }

            if (properties.containsKey(KEY_CRYPTO_AES_KEY_LENGTH)) {
                setCryptoAesKeyLength(Integer.valueOf(properties.getProperty(KEY_CRYPTO_AES_KEY_LENGTH)));
            }

            if (properties.containsKey(KEY_CRYPTO_RSA_KEY_LENGTH)) {
                setCryptoRsaKeyLength(Integer.valueOf(properties.getProperty(KEY_CRYPTO_RSA_KEY_LENGTH)));
            }

            //data
            if (properties.containsKey(KEY_DATA_FOLDER)) {
                setDataFolder(properties.getProperty(KEY_DATA_FOLDER));
            }

            if (properties.containsKey(KEY_DATA_RSA_PRIVATE_KEY_FILE)) {
                setDataRsaPrivateKeyFile(getDataFolder() + properties.getProperty(KEY_DATA_RSA_PRIVATE_KEY_FILE));
            }

            if (properties.containsKey(KEY_DATA_RSA_PUBLIC_KEY_FILE)) {
                setDataRsaPubKeyFile(getDataFolder() + properties.getProperty(KEY_DATA_RSA_PUBLIC_KEY_FILE));
            }

            loading = false;

            loaded = true;
            logger.info("Address configuration loaded");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isLoaded() {
        return loaded;
    }

    /**
     * @return the walletPassword
     */
    public static String getWalletPassword() {
        if (!loaded)
            load();

        return walletPassword;
    }

    /**
     * @param walletPassword the walletPassword to set
     */
    public static void setWalletPassword(String walletPassword) {
        Config.walletPassword = walletPassword;
    }

    /**
     * @return the walletFile
     */
    public static String getWalletDir() {
        if (!loaded)
            load();

        return walletDir;
    }

    /**
     * @param walletFile the walletFile to set
     */
    public static void setWalletDir(String walletFile) {
        Config.walletDir = walletFile;
    }

    /**
     * @return the networkGeth
     */
    public static String getNetworkGeth() {
        if (!loaded)
            load();

        return networkGeth;
    }

    /**
     * @param networkGeth the networkGeth to set
     */
    public static void setNetworkGeth(String networkGeth) {
        Config.networkGeth = networkGeth;
    }

    public static String getDataFolder() {
        if (!loaded)
            load();

        return dataFolder;
    }

    public static void setDataFolder(String dataFolder) {
        Config.dataFolder = dataFolder;
    }

    public static String getNetworkSwarm() {
        if (!loaded)
            load();
        return networkSwarm;
    }

    public static void setNetworkSwarm(String networkSwarm) {
        Config.networkSwarm = networkSwarm;
    }

    public static double getGridLosAngelesMaxLat() {
        if (!loaded)
            load();
        return gridLosAngelesMaxLat;
    }

    public static void setGridLosAngelesMaxLat(double gridLosAngelesMaxLat) {
        Config.gridLosAngelesMaxLat = gridLosAngelesMaxLat;
    }

    public static double getGridLosAngelesMinLat() {
        if (!loaded)
            load();
        return gridLosAngelesMinLat;
    }

    public static void setGridLosAngelesMinLat(double gridLosAngelesMinLat) {
        Config.gridLosAngelesMinLat = gridLosAngelesMinLat;
    }

    public static double getGridLosAngelesMaxLon() {
        if (!loaded)
            load();
        return gridLosAngelesMaxLon;
    }

    public static void setGridLosAngelesMaxLon(double gridLosAngelesMaxLon) {
        Config.gridLosAngelesMaxLon = gridLosAngelesMaxLon;
    }

    public static double getGridLosAngelesMinLon() {
        if (!loaded)
            load();
        return gridLosAngelesMinLon;
    }

    public static void setGridLosAngelesMinLon(double gridLosAngelesMinLon) {
        Config.gridLosAngelesMinLon = gridLosAngelesMinLon;
    }

    public static int getOwnerIndex() {
        if (!loaded)
            load();
        return ownerIndex;
    }

    public static void setOwnerIndex(int ownerIndex) {
        Config.ownerIndex = ownerIndex;
    }

    public static int getOwnerNumItems() {
        if (!loaded)
            load();
        return ownerNumItems;
    }

    public static void setOwnerNumItems(int ownerNumItems) {
        Config.ownerNumItems = ownerNumItems;
    }

    public static double getOwnerMinPrice() {
        if (!loaded)
            load();
        return ownerMinPrice;
    }

    public static void setOwnerMinPrice(double ownerMinPrice) {
        Config.ownerMinPrice = ownerMinPrice;
    }

    public static long getOwnerStartTime() {
        if (!loaded)
            load();
        return ownerStartTime;
    }

    public static void setOwnerStartTime(long ownerStartTime) {
        Config.ownerStartTime = ownerStartTime;
    }

    public static long getOwnerEndTime() {
        if (!loaded)
            load();
        return ownerEndTime;
    }

    public static void setOwnerEndTime(long ownerEndTime) {
        Config.ownerEndTime = ownerEndTime;
    }

    public static String getCuratorIndexAddress() {
        if (!loaded)
            load();

        return curatorIndexAddress;
    }

    public static void setCuratorIndexAddress(String curatorIndexAddress) {
        Config.curatorIndexAddress = curatorIndexAddress;
    }

    public static long getCuratorStartTime() {
        if (!loaded)
            load();

        return curatorStartTime;
    }

    public static void setCuratorStartTime(long curatorStartTime) {
        Config.curatorStartTime = curatorStartTime;
    }

    public static long getCuratorEndTime() {
        if (!loaded)
            load();

        return curatorEndTime;
    }

    public static void setCuratorEndTime(long curatorEndTime) {
        Config.curatorEndTime = curatorEndTime;
    }

    public static int getCuratorIndex() {
        if (!loaded)
            load();

        return curatorIndex;
    }

    public static void setCuratorIndex(int curatorIndex) {
        Config.curatorIndex = curatorIndex;
    }

    public static int getBuyerIndex() {
        if (!loaded)
            load();return buyerIndex;
    }

    public static void setBuyerIndex(int buyerIndex) {
        Config.buyerIndex = buyerIndex;
    }

    public static int getCryptoAesKeyLength() {
        if (!loaded)
            load();
        return cryptoAesKeyLength;
    }

    public static void setCryptoAesKeyLength(int cryptoAesKeyLength) {
        Config.cryptoAesKeyLength = cryptoAesKeyLength;
    }

    public static int getCryptoRsaKeyLength() {
        if (!loaded)
            load();
        return cryptoRsaKeyLength;
    }

    public static void setCryptoRsaKeyLength(int cryptoRsaKeyLength) {
        Config.cryptoRsaKeyLength = cryptoRsaKeyLength;
    }

    public static String getDataRsaPrivateKeyFile() {
        if (!loaded)
            load();
        return dataRsaPrivateKeyFile;
    }

    public static void setDataRsaPrivateKeyFile(String dataRsaPrivateKeyFile) {
        Config.dataRsaPrivateKeyFile = dataRsaPrivateKeyFile;
    }

    public static String getDataRsaPubKeyFile() {
        if (!loaded)
            load();
        return dataRsaPubKeyFile;
    }

    public static void setDataRsaPubKeyFile(String dataRsaPubKeyFile) {
        Config.dataRsaPubKeyFile = dataRsaPubKeyFile;
    }
}
