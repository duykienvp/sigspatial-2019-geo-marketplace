package edu.usc.infolab.kien.blockchaingeospatial.storage.offer;

import org.web3j.abi.datatypes.Address;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Offer {
    private Address buyer;
    private Address owner;
    private BigInteger value;
    private BigInteger offerExpiration;
    private BigInteger refundExpiration;
    private List<byte[]> dataAddresses;
    private byte[] encryptedKeys;
    private BigInteger numEncryptedKeys;
    private BigInteger inOwnerIndex;
    private BigInteger inBuyerIndex;
    private OfferStatus status;

    public Offer() {
    }

    public Address getBuyer() {
        return buyer;
    }

    public void setBuyer(Address buyer) {
        this.buyer = buyer;
    }

    public void setBuyer(String address) {
        this.buyer = new Address(address);
    }

    public Address getOwner() {
        return owner;
    }

    public void setOwner(Address owner) {
        this.owner = owner;
    }

    public void setOwner(String address) {
        this.owner = new Address(address);
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public void setValue(long value) {
        this.value = BigInteger.valueOf(value);
    }

    public BigInteger getOfferExpiration() {
        return offerExpiration;
    }

    public void setOfferExpiration(BigInteger offerExpiration) {
        this.offerExpiration = offerExpiration;
    }

    public void setOfferExpiration(long offerExpiration) {
        this.offerExpiration = BigInteger.valueOf(offerExpiration);
    }

    public BigInteger getRefundExpiration() {
        return refundExpiration;
    }

    public void setRefundExpiration(BigInteger refundExpiration) {
        this.refundExpiration = refundExpiration;
    }

    public void setRefundExpiration(long refundExpiration) {
        this.refundExpiration = BigInteger.valueOf(refundExpiration);
    }

    public List<byte[]> getDataAddresses() {
        return dataAddresses;
    }

    public void setDataAddresses(List<byte[]> dataAddresses) {
        this.dataAddresses = dataAddresses;
    }

    public byte[] getEncryptedKeys() {
        return encryptedKeys;
    }

    public void setEncryptedKeys(byte[] encryptedKeys) {
        this.encryptedKeys = encryptedKeys;
    }

    public void setEncryptedKeys(String encryptedKeys) {
        this.encryptedKeys = encryptedKeys.getBytes();
    }

    public BigInteger getNumEncryptedKeys() {
        return numEncryptedKeys;
    }

    public void setNumEncryptedKeys(BigInteger numEncryptedKeys) {
        this.numEncryptedKeys = numEncryptedKeys;
    }

    public void setNumEncryptedKeys(long numEncryptedKeys) {
        this.numEncryptedKeys = BigInteger.valueOf(numEncryptedKeys);
    }

    public BigInteger getInOwnerIndex() {
        return inOwnerIndex;
    }

    public void setInOwnerIndex(BigInteger inOwnerIndex) {
        this.inOwnerIndex = inOwnerIndex;
    }

    public void setInOwnerIndex(long inOwnerIndex) {
        this.inOwnerIndex = BigInteger.valueOf(inOwnerIndex);
    }

    public BigInteger getInBuyerIndex() {
        return inBuyerIndex;
    }

    public void setInBuyerIndex(BigInteger inBuyerIndex) {
        this.inBuyerIndex = inBuyerIndex;
    }

    public void setInBuyerIndex(long inBuyerIndex) {
        this.inBuyerIndex = BigInteger.valueOf(inBuyerIndex);
    }

    public OfferStatus getStatus() {
        return status;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }

    public void setStatus(int status) {
        this.status = OfferStatus.fromValue(status);
    }

    public List<String> getDataAddressesAsStrings() {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < this.dataAddresses.size(); i++) {
            res.add(new String(dataAddresses.get(i)));
        }

        return res;
    }

    @Override
    public String toString() {
        return "Offer{" +
            "buyer=" + buyer +
            ", owner=" + owner +
            ", value=" + value +
            ", offerExpiration=" + offerExpiration +
            ", refundExpiration=" + refundExpiration +
            ", dataAddresses=" + getDataAddressesAsStrings() +
            ", encryptedKeys=" + Arrays.toString(encryptedKeys) +
            ", numEncryptedKeys=" + numEncryptedKeys +
            ", inOwnerIndex=" + inOwnerIndex +
            ", inBuyerIndex=" + inBuyerIndex +
            ", status=" + status +
            '}';
    }
}
