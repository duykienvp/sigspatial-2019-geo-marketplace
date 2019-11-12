package edu.usc.infolab.kien.blockchaingeospatial.storage;

import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;

public class CommitmentPublicParameters {
    private byte[] N;
    private byte[] a;
    private byte[] s;
    private byte[] c;
    private BigInteger modifiedTime;

    public CommitmentPublicParameters() {
    }

    public byte[] getN() {
        return N;
    }

    public void setN(byte[] n) {
        N = n;
    }

    public byte[] getA() {
        return a;
    }

    public void setA(byte[] a) {
        this.a = a;
    }

    public byte[] getS() {
        return s;
    }

    public void setS(byte[] s) {
        this.s = s;
    }

    public byte[] getC() {
        return c;
    }

    public void setC(byte[] c) {
        this.c = c;
    }

    public BigInteger getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(BigInteger modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Override
    public String toString() {
        return "CommitmentPublicParameters{" +
            "N=" + Hex.encodeHexString(N) +
            ", a=" + Hex.encodeHexString(a) +
            ", s=" + Hex.encodeHexString(s) +
            ", c=" + Hex.encodeHexString(c) +
            ", modifiedTime=" + modifiedTime.toString() +
            '}';
    }
}
