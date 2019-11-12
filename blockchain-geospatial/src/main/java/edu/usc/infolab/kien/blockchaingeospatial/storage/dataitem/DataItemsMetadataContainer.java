package edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem;

import java.util.ArrayList;
import java.util.List;

public class DataItemsMetadataContainer {
    private String owner = "";
    private String storageAddress = "";
    private List<DataItemMetadata> dataItemMetadatas = new ArrayList<>();

    public DataItemsMetadataContainer() {
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStorageAddress() {
        return storageAddress;
    }

    public void setStorageAddress(String storageAddress) {
        this.storageAddress = storageAddress;
    }

    public List<DataItemMetadata> getDataItemMetadatas() {
        return dataItemMetadatas;
    }

    public void setDataItemMetadatas(List<DataItemMetadata> dataItemMetadatas) {
        this.dataItemMetadatas = dataItemMetadatas;
    }

    public void addDataItemMetadata(DataItemMetadata itemMetadata) {
        this.dataItemMetadatas.add(itemMetadata);
    }

    public void clearPrivateMetadata() {
        for (DataItemMetadata dataItemMetadata: dataItemMetadatas) {
            dataItemMetadata.clearPrivateMetadata();
        }
    }

    @Override
    public Object clone() {
        DataItemsMetadataContainer dataItemsMetadataContainer;
        try {
            dataItemsMetadataContainer = (DataItemsMetadataContainer) super.clone();
        } catch (CloneNotSupportedException e) {
            dataItemsMetadataContainer = new DataItemsMetadataContainer();
            dataItemsMetadataContainer.setOwner(this.getOwner());
            dataItemsMetadataContainer.setStorageAddress(this.getStorageAddress());
        }

        List<DataItemMetadata> dataItemMetadataList = new ArrayList<>();
        for (DataItemMetadata dataItemMetadata: this.dataItemMetadatas) {
            dataItemMetadataList.add((DataItemMetadata) dataItemMetadata.clone());
        }

        dataItemsMetadataContainer.setDataItemMetadatas(dataItemMetadataList);

        return dataItemsMetadataContainer;
    }

    @Override
    public String toString() {
        return "DataItemsMetadataContainer{" +
            "owner='" + owner + '\'' +
            ", storageAddress='" + storageAddress + '\'' +
            ", dataItemMetadatas=" + dataItemMetadatas.toString() +
            '}';
    }
}
