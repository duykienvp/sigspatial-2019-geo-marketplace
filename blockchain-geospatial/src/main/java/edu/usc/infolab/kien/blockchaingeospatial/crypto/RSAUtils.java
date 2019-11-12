package edu.usc.infolab.kien.blockchaingeospatial.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class RSAUtils {
    private static final Logger logger = LoggerFactory.getLogger(RSAUtils.class);

    public static final String PRIVATE_KEY_FILE_SUFFIX = ".key";
    public static final String PUBLIC_KEY_FILE_SUFFIX = ".pub";

    public static final String KEY_GENERATION_ALGORITHM = "RSA";
    public static final String ALGORITHM = "RSA/ECB/PKCS1Padding";
    /**
     * Get public key from a Base64 encoded string
     * @param base64PublicKey a Base64 encoded string
     * @return public key or {@code null} if error occurred
     */
    public static PublicKey getPublicKey(String base64PublicKey){
        return getPublicKey(Base64.getDecoder().decode(base64PublicKey.getBytes()));
    }

    /**
     * Get public key from an encoded key
     * @param pk an encoded key
     * @return public key or {@code null} if error occurred
     */
    public static PublicKey getPublicKey(byte[] pk) {
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pk);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_GENERATION_ALGORITHM);
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (Exception e) {
            logger.error("Error getting public key from: " + Arrays.toString(pk), e);
        }
        return publicKey;
    }

    /**
     * Get private key from a Base64 encoded string
     * @param base64PrivateKey a Base64 encoded string
     * @return private key or {@code null} if error occurred
     */
    public static PrivateKey getPrivateKey(String base64PrivateKey){
        return getPrivateKey(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
    }

    /**
     * Get private key from an encoded key
     * @param key an encoded key
     * @return private key or {@code null} if error occurred
     */
    public static PrivateKey getPrivateKey(byte[] key){
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance(KEY_GENERATION_ALGORITHM);
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            logger.error("Error getting private key from: " + Arrays.toString(key), e);
        }
        return privateKey;
    }

    /**
     * Encryption data with an encoded public key
     * @param data
     * @param publicKey
     * @return
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     */
    public static byte[] encrypt(byte[] data, byte[] publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return cipher.doFinal(data);
    }

    /**
     * Decrypt data with an encoded private key
     * @param data
     * @param privateKey
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, byte[] privateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(data, getPrivateKey(privateKey));
    }

    public static String generatePrivateKeyFile(String prefix, String address) {
        return prefix + "." + address + "." + PRIVATE_KEY_FILE_SUFFIX;
    }

    public static String generatePublicKeyFile(String prefix, String address) {
        return prefix + "." + address + "." + PUBLIC_KEY_FILE_SUFFIX;
    }
}
