package edu.usc.infolab.kien.blockchaingeospatial.crypto;

import edu.usc.infolab.kien.blockchaingeospatial.config.Config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class RSAKeyPairGenerator {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Create a new key pair
     * @throws NoSuchAlgorithmException
     */
    public RSAKeyPairGenerator() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(Config.getCryptoRsaKeyLength());
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    /**
     * Write an encoded key to a string file
     * @param path path to file
     * @param key encoded key
     * @throws IOException
     */
    public static void writeToFile(String path, byte[] key) throws IOException {
        Base64.Encoder encoder = Base64.getEncoder();

        Writer out = new FileWriter(path);
        out.write(encoder.encodeToString(key));
        out.close();
    }

    /**
     * Get encoded key from a string file
     * @param path path to file
     * @return encoded key
     * @throws IOException
     */
    public static byte[] getFromFile(String path) throws IOException {
        File f = new File(path);
        String content = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(content);
    }

    /**
     * Get private key
     * @return private key
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * Get public key
     * @return public key
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
        writeToFile(Config.getDataRsaPubKeyFile(), keyPairGenerator.getPublicKey().getEncoded());
        writeToFile(Config.getDataRsaPrivateKeyFile(), keyPairGenerator.getPrivateKey().getEncoded());

        System.out.println(Base64.getEncoder().encodeToString(keyPairGenerator.getPublicKey().getEncoded()));
        System.out.println(Base64.getEncoder().encodeToString(keyPairGenerator.getPrivateKey().getEncoded()));

        System.out.println(Base64.getEncoder().encodeToString(getFromFile(Config.getDataRsaPubKeyFile())));
        System.out.println(Base64.getEncoder().encodeToString(getFromFile(Config.getDataRsaPrivateKeyFile())));

        System.out.println(Arrays.equals(keyPairGenerator.getPublicKey().getEncoded(), getFromFile(Config.getDataRsaPubKeyFile())));
        System.out.println(Arrays.equals(keyPairGenerator.getPublicKey().getEncoded(), getFromFile(Config.getDataRsaPubKeyFile())));
    }
}
