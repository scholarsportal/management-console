package org.duracloud.storage.domain;

public enum StorageProviderType {
    AMAZON_S3("amazon-s3"),
    MICROSOFT_AZURE("ms-azure"),
    SUN("sun"),
    EMC("emc"),
    EMC_SECONDARY("emc-secondary"),
    RACKSPACE("rackspace"),
    UNKNOWN("unknown"),
    TEST_RETRY("test-retry"),
    TEST_VERIFY_CREATE("test-verify-create"),
    TEST_VERIFY_DELETE("test-verify-delete");

    private final String text;

    private StorageProviderType(String pt) {
        text = pt;
    }

    public static StorageProviderType fromString(String pt) {
        for (StorageProviderType pType : values()) {
            if (pType.text.equalsIgnoreCase(pt)||
                pType.name().equalsIgnoreCase(pt)) {
                return pType;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return text;
    }
}
