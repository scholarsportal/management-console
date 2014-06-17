package org.duracloud.account.util;

import org.duracloud.storage.domain.StorageProviderType;

import java.util.ArrayList;
import java.util.List;

public class StorageProviderTypeUtil {
    private static final List<StorageProviderType> SECONDARY_PROVIDER_TYPES;
    
    static {
        SECONDARY_PROVIDER_TYPES = new ArrayList<>();
        SECONDARY_PROVIDER_TYPES.add(StorageProviderType.RACKSPACE);
        SECONDARY_PROVIDER_TYPES.add(StorageProviderType.MICROSOFT_AZURE);
        SECONDARY_PROVIDER_TYPES.add(StorageProviderType.SDSC);
        SECONDARY_PROVIDER_TYPES.add(StorageProviderType.AMAZON_GLACIER);
    }
    
    public static List<StorageProviderType> getAvailableSecondaryTypes() {
        return new ArrayList<>(SECONDARY_PROVIDER_TYPES);
    }
}
