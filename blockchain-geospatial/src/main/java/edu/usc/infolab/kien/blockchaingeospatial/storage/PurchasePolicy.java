package edu.usc.infolab.kien.blockchaingeospatial.storage;

/**
 * Purchase policy of an owner
 */
public class PurchasePolicy {
    private double minPrice = 0;

    public PurchasePolicy() {
    }

    public PurchasePolicy(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    @Override
    public String toString() {
        return "PurchasePolicy{" +
            "minPrice=" + minPrice +
            '}';
    }
}
