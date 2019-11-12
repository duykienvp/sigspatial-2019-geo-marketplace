package edu.usc.infolab.kien.blockchaingeospatial.monitor;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

public class TransactionMonitorMessage {
    private String functionName;
    private BigInteger gasPrice;
    private TransactionReceipt receipt;

    public TransactionMonitorMessage() {
    }

    public TransactionMonitorMessage(String functionName, BigInteger gasPrice, TransactionReceipt receipt) {
        this.functionName = functionName;
        this.gasPrice = gasPrice;
        this.receipt = receipt;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public TransactionReceipt getReceipt() {
        return receipt;
    }

    public void setReceipt(TransactionReceipt receipt) {
        this.receipt = receipt;
    }
}
