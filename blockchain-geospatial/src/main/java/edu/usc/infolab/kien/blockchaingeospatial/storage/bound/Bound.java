package edu.usc.infolab.kien.blockchaingeospatial.storage.bound;

public class Bound {
    private double maxLat = 0;
    private double minLat = 0;
    private double maxLon = 0;
    private double minLon = 0;

    public Bound() {
    }

    public Bound(double maxLat, double minLat, double maxLon, double minLon) {
        this.maxLat = maxLat;
        this.minLat = minLat;
        this.maxLon = maxLon;
        this.minLon = minLon;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public void setMaxLat(double maxLat) {
        this.maxLat = maxLat;
    }

    public double getMinLat() {
        return minLat;
    }

    public void setMinLat(double minLat) {
        this.minLat = minLat;
    }

    public double getMaxLon() {
        return maxLon;
    }

    public void setMaxLon(double maxLon) {
        this.maxLon = maxLon;
    }

    public double getMinLon() {
        return minLon;
    }

    public void setMinLon(double minLon) {
        this.minLon = minLon;
    }

    public double getLatLength() {
        return Math.abs(this.maxLat - this.minLat);
    }

    public double getLonLength() {
        return Math.abs(this.maxLon - this.minLon);
    }

    @Override
    public String toString() {
        return "Bound{" +
            "maxLat=" + maxLat +
            ", minLat=" + minLat +
            ", maxLon=" + maxLon +
            ", minLon=" + minLon +
            '}';
    }
}
