
package org.duraspace.serviceprovider.domain;

public enum ComputeProviderType {
    AMAZON_EC2("amazon-ec2"), MICROSOFT_AZURE("ms-azure"), SUN("sun"), UNKNOWN(
            "unknown");

    private final String text;

    private ComputeProviderType(String pt) {
        text = pt;
    }

    public static ComputeProviderType fromString(String pt) {
        for (ComputeProviderType pType : values()) {
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