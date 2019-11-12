package edu.usc.infolab.kien.blockchaingeospatial.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * Monitoring transaction expenses
 */
public class TransactionMonitor {
    private static final Logger logger = LoggerFactory.getLogger(TransactionMonitor.class);

    private static List<TransactionMonitorMessage> messages = new ArrayList<>();

    /**
     * Add new transaction monitor message for monitoring
     * @param message a message
     */
    public static void addMessage(TransactionMonitorMessage message) {
        try {
            if (message == null)
                throw new NullPointerException("null message");
            if (message.getFunctionName() == null
                || message.getGasPrice() == null
                || message.getReceipt() == null)
                throw new NullPointerException("null message content");
            messages.add(message);
            logger.info("New transaction monitor message added for functionName= "
                + message.getFunctionName()
                + ", with gasPrice=" + message.getGasPrice().toString()
                + ", with gasUsed=" + message.getReceipt().getGasUsed().toString());
        } catch (NullPointerException ex) {
            logger.error("Error adding transaction monitor message", ex);
        }
    }

    public static String getSummary() {
        StringBuilder builder = new StringBuilder();

        builder.append("Number of transactions: " + getNumMessages() + "\n");

        builder.append("Total gas used = ");
        builder.append(getTotalGasUsed().toString());
        builder.append("\n");

        return builder.toString();
    }

    public static int getNumMessages() {
        return messages.size();
    }

    public static BigInteger getTotalGasUsed() {
        BigInteger totalGasUsed = BigInteger.valueOf(0);
        for (TransactionMonitorMessage message : messages) {
            totalGasUsed = totalGasUsed.add(message.getReceipt().getGasUsed());
        }

        return totalGasUsed;
    }
}
