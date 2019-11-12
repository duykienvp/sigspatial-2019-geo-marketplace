package edu.usc.infolab.kien.blockchaingeospatial.storage.bound;

public enum AreaBound {
    LOS_ANGELES(0);

    private int value;

    AreaBound(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AreaBound fromValue(int value) throws IllegalArgumentException {
        for (AreaBound areaBound : AreaBound.values()) {
            if (areaBound.getValue() == value) {
                return areaBound;
            }
        }

        throw new IllegalArgumentException("Illegal value");
    }
}
