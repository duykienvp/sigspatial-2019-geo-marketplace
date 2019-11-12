package edu.usc.infolab.kien.blockchaingeospatial.crypto;

import com.google.gson.Gson;
import edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem.DataItem;
import edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem.DataItemMetadata;
import edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem.DataItemsContainer;
import org.apache.commons.lang3.SerializationUtils;

import java.security.SecureRandom;

public class AESUtils {
    private static final Gson gson = new Gson();
    /**
     * Generate a secure random string with a given length in bytes
     * @param length in bytes
     * @return a secure random string with a given length in bytes
     */
    public static byte[] generateRandomBytes(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] array = new byte[length];
        secureRandom.nextBytes(array);
        return array;
    }

    /**
     * Encrypt data items in a container with the clean metadata of each item as the associated data
     * @param encryptionKey encryption key
     * @param container data container
     * @return new container with the metadata (private data cleaned) and with encrypted data items
     * @throws AuthenticatedEncryptionException
     */
    public static DataItemsContainer encryptDataContainer(byte[] encryptionKey, DataItemsContainer container) throws AuthenticatedEncryptionException {
        DataItemsContainer encryptedContainer = new DataItemsContainer();

        encryptedContainer.setOwner(container.getOwner());
        for (DataItem item: container.getDataItems()) {
            encryptedContainer.addDataItem(encryptDataItem(encryptionKey, item));
        }

        return encryptedContainer;
    }

    /**
     * Remove private metadata and Encrypt data item as content and with the clean metadata as the associated data
     * @param encryptionKey encryption key
     * @param item data item
     * @return new data item and with encrypted data item as content
     * @throws AuthenticatedEncryptionException
     */
    public static DataItem encryptDataItem(byte[] encryptionKey, DataItem item) throws AuthenticatedEncryptionException {
        DataItem encryptedItem = (DataItem) item.clone();
        encryptedItem.getMetadata().clearPrivateMetadata();

        DataItemMetadata cleanMetadata = (DataItemMetadata) encryptedItem.getMetadata().clone();

        String cleanMetadataStr = gson.toJson(cleanMetadata);

        byte[] itemAsBytes = SerializationUtils.serialize(item);

        //content is the encryption of the entire item
        AESGCMEncryption encrypt = new AESGCMEncryption();
        encryptedItem.setContent(
            encrypt.encrypt(
                encryptionKey,
                itemAsBytes,
                cleanMetadataStr.getBytes())
        );

        return encryptedItem;
    }

    /**
     * Remove private metadata and Encrypt data item as content and with the clean metadata as the associated data
     * @param encryptionKey encryption key
     * @param item data item
     * @return new data item and with encrypted data item as content
     * @throws AuthenticatedEncryptionException
     */
    public static DataItem decryptDataItem(byte[] encryptionKey, DataItem item) throws AuthenticatedEncryptionException {
        DataItemMetadata cleanMetadata = (DataItemMetadata) item.getMetadata().clone();
        cleanMetadata.clearPrivateMetadata();

        String cleanMetadataStr = gson.toJson(cleanMetadata);
        //content is the encryption of the entire item

        AESGCMEncryption decrypt = new AESGCMEncryption();
        byte[] itemAsBytes = decrypt.decrypt(
            encryptionKey,
            item.getContent(),
            cleanMetadataStr.getBytes());

        return SerializationUtils.deserialize(itemAsBytes);
    }
}
