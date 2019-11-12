package edu.usc.infolab.kien.blockchaingeospatial.crypto;

public enum AuthenticatedEncryptionKeyStrength {
    /**
     * High Security which is equivalent to a AES key size of 128 bit
     */
    STRENGTH_HIGH(0),
    /**
     * Very high security which is equivalent to a AES key size of 256 bit
     * Note: This is usually not required.
     */
    STRENGTH_VERY_HIGH(1);

    private int value;

    AuthenticatedEncryptionKeyStrength(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AuthenticatedEncryptionKeyStrength fromValue(int value) throws IllegalArgumentException {
        for (AuthenticatedEncryptionKeyStrength strength : AuthenticatedEncryptionKeyStrength.values()) {
            if (strength.getValue() == value) {
                return strength;
            }
        }

        throw new IllegalArgumentException("Illegal value");
    }
}
