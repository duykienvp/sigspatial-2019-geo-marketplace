package edu.usc.infolab.kien.blockchaingeospatial.storage.searchableindex;

import edu.usc.infolab.kien.blockchaingeospatial.storage.bound.Bound;
import edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem.DataItemMetadata;
import edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem.DataItemsMetadataContainer;
import edu.usc.infolab.kien.blockchaingeospatial.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PlainIndex {
    private String curator = "";
    private List<DataItemsMetadataContainer> metadataList = new ArrayList<>();
    private long startTime = 0;
    private long endTime = 0;
    private long createdTime = 0;
    private Bound bound = new Bound();

    public PlainIndex() {
    }

    public PlainIndex(String curator, List<DataItemsMetadataContainer> dataItemMetadataList) {
        this.curator = curator;
        this.metadataList = dataItemMetadataList;
    }

    public String getCurator() {
        return curator;
    }

    public void setCurator(String curator) {
        this.curator = curator;
    }

    public List<DataItemsMetadataContainer> getMetadataList() {
        return metadataList;
    }

    public void setMetadataList(List<DataItemsMetadataContainer> metadataList) {
        this.metadataList = metadataList;
    }

    public void addDataItemMetadata(List<DataItemsMetadataContainer> containerMetadataList) {
        this.metadataList.addAll(containerMetadataList);
    }

    public void addDataItemMetadata(DataItemsMetadataContainer containerMetadata) {
        this.metadataList.add(containerMetadata);
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public Bound getBound() {
        return bound;
    }

    public void setBound(Bound bound) {
        this.bound = bound;
    }

    @Override
    public String toString() {
        return "PlainIndex{" +
            "curator='" + curator + '\'' +
            ", metadataList=" + metadataList.toString() +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", createdTime=" + createdTime +
            ", bound=" + bound.toString() +
            '}';
    }

    /**
     * Prepare the index
     */
    public void prepare() {
        boolean updated = false;
        long minStartTime = Long.MAX_VALUE;
        long maxEndTime = Long.MIN_VALUE;
        for (DataItemsMetadataContainer containerMetadata: metadataList) {
            for (DataItemMetadata metadata: containerMetadata.getDataItemMetadatas()) {
                minStartTime = Long.min(metadata.getTimestamp(), minStartTime);
                maxEndTime = Long.max(metadata.getTimestamp(), maxEndTime);

                updated = true;
            }

            if (updated) {
                startTime = minStartTime;
                endTime = maxEndTime;
            }
        }

        this.setCreatedTime(Utils.currentTimeInSeconds());
    }
}
