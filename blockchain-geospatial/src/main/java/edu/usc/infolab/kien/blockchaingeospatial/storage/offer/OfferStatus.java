package edu.usc.infolab.kien.blockchaingeospatial.storage.offer;

public enum OfferStatus {
    INVALID(0),
    PENDING(1),
    KEYS_SUBMITTED(2),
    CANCELED(3),
    WITHDRAWN(4),
    REFUNDED(5);

    private int value;

    OfferStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static OfferStatus fromValue(int value) throws IllegalArgumentException {
        for (OfferStatus offerStatus : OfferStatus.values()) {
            if (offerStatus.getValue() == value) {
                return offerStatus;
            }
        }

        throw new IllegalArgumentException("Illegal value");
    }
}
