/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://www.fedora.info/license/).
 */
package org.duraspace.customerwebapp.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.duraspace.customerwebapp.domain.Space;
import org.duraspace.customerwebapp.domain.SpaceMetadata;
import org.duraspace.storage.domain.StorageException;
import org.duraspace.storage.provider.StorageProvider;

/**
 * Provides utility methods for spaces.
 *
 * @author Bill Branan
 */
public class SpaceUtil {

    public static List<Space> getSpacesList(String accountId)
    throws StorageException{
        StorageProvider storage =
            StorageProviderFactory.getStorageProvider(accountId);

        Iterator<String> spaceIds = storage.getSpaces();

        List<Space> spaces = new ArrayList<Space>();
        if(spaceIds != null) {
            while(spaceIds.hasNext()) {
                String spaceId = spaceIds.next();
                Space space = new Space();
                space.setAccountId(accountId);
                space.setSpaceId(spaceId);
                space.setMetadata(getSpaceMetadata(storage, spaceId));
                spaces.add(space);
            }
        }
        return spaces;
    }

    public static SpaceMetadata getSpaceMetadata(StorageProvider storage, String spaceId)
    throws StorageException{
        Map<String, String> spaceProps = storage.getSpaceMetadata(spaceId);
        SpaceMetadata spaceMetadata = new SpaceMetadata();
        spaceMetadata.setName(
            spaceProps.get(StorageProvider.METADATA_SPACE_NAME));
        spaceMetadata.setCreated(
            spaceProps.get(StorageProvider.METADATA_SPACE_CREATED));
        spaceMetadata.setCount(
            spaceProps.get(StorageProvider.METADATA_SPACE_COUNT));
        spaceMetadata.setAccess(
            spaceProps.get(StorageProvider.METADATA_SPACE_ACCESS));
        return spaceMetadata;
    }
}
