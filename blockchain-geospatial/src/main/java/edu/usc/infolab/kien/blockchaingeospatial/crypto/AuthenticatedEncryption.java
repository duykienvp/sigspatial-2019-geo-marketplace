package edu.usc.infolab.kien.blockchaingeospatial.crypto;

/**
 * Authenticated encryption (AE) and authenticated encryption with associated data (AEAD, variant of AE)
 * is a form of encryption which simultaneously provides confidentiality, integrity, and authenticity assurances
 * on the data. These attributes are provided under a single, easy to use programming interface.
 * <p>
 * See: https://en.wikipedia.org/wiki/Authenticated_encryption
 *
 * @author Patrick Favre-Bulle
 * @since 18.12.2017
 */

public interface AuthenticatedEncryption {

    /**
     * Encrypts and adds a authentication tag the given content
     *
     * @param rawEncryptionKey to use as encryption key material
     * @param rawData          to encrypt
     * @param associatedData   additional data used to create the auth tag and will be subject to integrity/authentication check
     * @return encrypted content
     * @throws AuthenticatedEncryptionException if any crypto fails
     */
    byte[] encrypt(byte[] rawEncryptionKey, byte[] rawData, byte[] associatedData) throws AuthenticatedEncryptionException;

    /**
     * Decrypt and verifies the authenticity of given encrypted data
     *
     * @param rawEncryptionKey to use as decryption key material
     * @param encryptedData    to decrypt
     * @param associatedData   additional data used to create the auth tag; must be same as provided
     *                         in the encrypt step
     * @return decrypted, original data
     * @throws AuthenticatedEncryptionException if any crypto fails
     */
    byte[] decrypt(byte[] rawEncryptionKey, byte[] encryptedData, byte[] associatedData) throws AuthenticatedEncryptionException;

    /**
     * Get the required key size length in bytes for given security strength type
     *
     * @param keyStrengthType STRENGTH_HIGH or STRENGTH_VERY_HIGH
     * @return required size in byte
     */
    int byteSizeLength(AuthenticatedEncryptionKeyStrength keyStrengthType);
}
