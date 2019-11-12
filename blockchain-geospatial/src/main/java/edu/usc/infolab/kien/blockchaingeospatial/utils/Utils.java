package edu.usc.infolab.kien.blockchaingeospatial.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;

public class Utils {

    public static final String HEX_PREFIX = "0x";

    /**
     * Get all files that match file name extension
     * @param directoryName
     * @param extension
     * @return
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public static Collection<File> getAllFilesThatMatchFilenameExtension(String directoryName, String extension)
        throws NullPointerException, IllegalArgumentException {
        File directory = new File(directoryName);
        return FileUtils.listFiles(directory, new WildcardFileFilter(extension), null);
    }

    /**
     * Get current time in seconds
     * @return
     */
    public static long currentTimeInSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * Concatenate {@code extraPath} to a {@code baseUrl}
     * @param baseUrl base url
     * @param extraPath extra path to concatenate
     * @return concatenated url
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public static URL concatenate(URL baseUrl, String extraPath) throws URISyntaxException,
        MalformedURLException {
        URI uri = baseUrl.toURI();
        String newPath = ensureTrailingSlashExists(uri.getPath() + '/' + extraPath);
        URI newUri = uri.resolve(newPath);
        return newUri.normalize().toURL();
    }

    /**
     * Ensure that a trailing slash (/) exists for a url
     * @param url
     * @return
     */
    public static String ensureTrailingSlashExists(String url) {
        return url.endsWith("/") ? url : url + "/";
    }


    /**
     * Convert a Hex string to byte array
     * @param hexString hex string (without
     * @return
     * @throws DecoderException
     */
    public static byte[] hexStringToBytes(String hexString) throws DecoderException {
        return Hex.decodeHex(removeLeadingHexStringPrefix(hexString).toCharArray());
    }

    /**
     * Remove leading {@link #HEX_PREFIX} if exists
     * @param hexString
     * @return
     */
    public static String removeLeadingHexStringPrefix(String hexString) {
        return hexString.startsWith(HEX_PREFIX) ? hexString.substring(HEX_PREFIX.length()) : hexString;
    }
}
