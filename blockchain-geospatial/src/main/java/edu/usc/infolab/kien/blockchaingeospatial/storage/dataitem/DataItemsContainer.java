package edu.usc.infolab.kien.blockchaingeospatial.storage.dataitem;

import java.util.ArrayList;
import java.util.List;

public class DataItemsContainer {
    private String owner = "";
    private String storageAddress = "";
    private List<DataItem> dataItems = new ArrayList<>();

    public DataItemsContainer() {
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

    public List<DataItem> getDataItems() {
        return dataItems;
    }

    public void setDataItems(List<DataItem> dataItems) {
        this.dataItems = dataItems;
    }

    public void addDataItem(DataItem item) {
        dataItems.add(item);
    }


    public DataItemsMetadataContainer getMetadata() {
        DataItemsMetadataContainer metadata = new DataItemsMetadataContainer();
        metadata.setOwner(this.owner);
        for (int i = 0; i < dataItems.size(); i++) {
            metadata.addDataItemMetadata(dataItems.get(i).getMetadata());
        }

        return metadata;
    }

    @Override
    public Object clone() {
        DataItemsContainer dataItemsContainer;
        try {
            dataItemsContainer = (DataItemsContainer) super.clone();
        } catch (CloneNotSupportedException e) {
            dataItemsContainer = new DataItemsContainer();
            dataItemsContainer.setOwner(this.getOwner());
            dataItemsContainer.setStorageAddress(this.getStorageAddress());
        }

        List<DataItem> dataItems = new ArrayList<>();
        for (DataItem dataItem: this.dataItems) {
            dataItems.add((DataItem) dataItem.clone());
        }

        dataItemsContainer.setDataItems(dataItems);

        return dataItemsContainer;
    }

    @Override
    public String toString() {
        return "DataItemsContainer{" +
            "owner='" + owner + '\'' +
            ", storageAddress='" + storageAddress + '\'' +
            ", dataItems=" + dataItems.toString() +
            '}';
    }
}
