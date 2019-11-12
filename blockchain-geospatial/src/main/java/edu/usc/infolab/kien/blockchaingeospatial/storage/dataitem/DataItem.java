package edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem;

import java.io.Serializable;
import java.util.Arrays;

public class DataItem implements Serializable {
    private DataItemMetadata metadata = new DataItemMetadata();
    private byte[] content = null;

    public DataItem() {
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public DataItemMetadata getMetadata() {
        return this.metadata;
    }

    public void setMetadata(DataItemMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public Object clone() {
        DataItem dataItem;
        try {
            dataItem = (DataItem) super.clone();
        } catch (CloneNotSupportedException e) {
            dataItem = new DataItem();
        }
        dataItem.setContent(this.getContent().clone());
        dataItem.setMetadata((DataItemMetadata)this.metadata.clone());

        return dataItem;
    }

    @Override
    public String toString() {
        return "DataItem{" +
            "metadata=" + metadata +
            ", content=" + Arrays.toString(content) +
            '}';
    }
}
