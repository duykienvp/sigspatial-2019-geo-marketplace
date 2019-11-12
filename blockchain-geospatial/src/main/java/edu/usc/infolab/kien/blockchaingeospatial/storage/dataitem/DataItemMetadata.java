package edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem;

import java.io.Serializable;

public class DataItemMetadata implements Serializable {
    private String id = "";
    private double lat = 0;
    private double lon = 0;
    private long timestamp = 0;
    private String owner = "";

    public DataItemMetadata() {
    }

    public DataItemMetadata(String id, double lat, double lon, long timestamp, String owner) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Remove private metadata
     *  - location
     */
    public void clearPrivateMetadata() {
        lat = 0;
        lon = 0;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return new DataItemMetadata(this.getId(), this.getLat(), this.getLon(), this.getTimestamp(), this.getOwner());
        }
    }

    @Override
    public String toString() {
        return "DataItemMetadata{" +
            "id='" + id + '\'' +
            ", lat=" + lat +
            ", lon=" + lon +
            ", timestamp=" + timestamp +
            ", owner='" + owner + '\'' +
            '}';
    }
}
