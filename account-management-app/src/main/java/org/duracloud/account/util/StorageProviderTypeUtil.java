package org.duracloud.account.util;

import java.util.ArrayList;
import java.util.List;

import org.duracloud.storage.domain.StorageProviderType;

public class StorageProviderTypeUtil {
    private static final List<StorageProviderType> SECONDARY_PROVIDER_TYPES;
    
    static {
        SECONDARY_PROVIDER_TYPES = new ArrayList<StorageProviderType>(3);
        SECONDARY_PROVIDER_TYPES.add(StorageProviderType.RACKSPACE);
        SECONDARY_PROVIDER_TYPES.add(StorageProviderType.MICROSOFT_AZURE);
        SECONDARY_PROVIDER_TYPES.add(StorageProviderType.SDSC);
    }
    
    public static List<StorageProviderType> getAvailableSecondaryTypes() {
        return new ArrayList<StorageProviderType>(SECONDARY_PROVIDER_TYPES);
    }
}
