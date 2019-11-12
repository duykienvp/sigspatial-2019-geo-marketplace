package edu.usc.infolab.kien.blockchaingeospatial.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class AddressConfig {
    private static final Logger logger = LoggerFactory.getLogger(AddressConfig.class);

    public static final String ADDRESS_CONFIG_FILE = "addresses.properties";

    public static final String KEY_CONTRACT_GREETER = "contract.greeter";
    public static final String KEY_CONTRACT_PRIV_GEO_MARKT = "contract.privgeomarkt";
    public static final String KEY_CONTRACT_PRIV_GEO_MARKT_STORAGE_COMMITMENT = "contract.privgeomarkt.storage.commitment";
    public static final String KEY_CONTRACT_PRIV_GEO_MARKT_STORAGE_SEARCHABLE = "contract.privgeomarkt.storage.searchable";

    private static boolean loaded = false;
    private static Properties properties = new Properties();
    /**
     * Load configuration from files
     */
    public static void load() {
        try {
            logger.info("Start loading addresses");

            properties = new Properties();
            properties.load(AddressConfig.class.getClassLoader().getResourceAsStream(ADDRESS_CONFIG_FILE));

            loaded = true;

            logger.info("Addresses loaded");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the address for {@code key}
     * @param key
     * @return the address for {@code key} or {@code null} if {@code key} does not exist
     */
    public static String getAddress(String key) {
        if (!loaded)
            load();

        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }

        return null;
    }

    /**
     * Save an address to the list and store to file
     * @param key key of the address
     * @param value address
     */
    public static void saveAddress(String key, String value) {
        if (!loaded)
            load();

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(ADDRESS_CONFIG_FILE);

            properties.setProperty(key, value);

            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
