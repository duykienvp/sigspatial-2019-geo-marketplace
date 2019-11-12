package edu.usc.infolab.kien.blockchaingeospatial.eth;

import edu.usc.infolab.kien.blockchaingeospatial.config.Config;
import edu.usc.infolab.kien.blockchaingeospatial.utils.Utils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class SwarmHelper {
    public static final String SWARM_URL_BZZ = "bzz:";
    private static final Logger logger = LoggerFactory.getLogger(ContractHelper.class);

    /**
     * Get content of file from Swarm with given hash
     * @param hash 64-character hash
     * @return content of file or {@code null} if error occurred
     * @throws IOException
     */
    public static ByteArrayOutputStream getContent(String hash) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            URL baseUrl = new URL(Config.getNetworkSwarm());
            URL bzzUrl = Utils.concatenate(baseUrl, SWARM_URL_BZZ);
            URL url = Utils.concatenate(bzzUrl, hash);
            baos = new ByteArrayOutputStream();
            InputStream is = null;
            try {
                is = url.openStream ();
                byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
                int n;
                while ( (n = is.read(byteChunk)) > 0 ) {
                    baos.write(byteChunk, 0, n);
                }
            }
            catch (IOException e) {
                System.err.printf("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
                e.printStackTrace();
            }
            finally {
                if (is != null) { is.close(); }
            }

        } catch (IOException ioe) {
            logger.error("Error getting content for hash:" + hash, ioe);
            throw ioe;
        } catch (Exception e) {
            logger.error("Error getting content for hash:" + hash, e);
        }

        return baos;
    }

    /**
     * Post content to Swarm
     * @param content contetn
     * @return address or {@code null} if error occurred
     */
    public static String postContent(String content) {
        try {
            URL baseUrl = new URL(Config.getNetworkSwarm());
            URL bzzUrl = Utils.concatenate(baseUrl, SWARM_URL_BZZ);

            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpPost httppost = new HttpPost(bzzUrl.toURI());
            httppost.setHeader(HTTP.CONTENT_TYPE, ContentType.TEXT_PLAIN.toString());


            final String msg = content;
            httppost.setEntity(new StringEntity(msg));

            HttpResponse httpResponse = httpclient.execute(httppost);
            HttpEntity entity = httpResponse.getEntity();

            return EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }
}
