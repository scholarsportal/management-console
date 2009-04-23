
package org.duraspace.storage.domain;

public enum StorageProviderType {
    AMAZON_S3("amazon-s3"), MICROSOFT_AZURE("ms-azure"), SUN("sun"),
    EMC("emc"), RACKSPACE("rackspace"), UNKNOWN("unknown");

    private final String text;

    private StorageProviderType(String pt) {
        text = pt;
    }

    public static StorageProviderType fromString(String pt) {
        for (StorageProviderType pType : values()) {
            if (pType.text.equalsIgnoreCase(pt)) {
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
