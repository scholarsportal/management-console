/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://www.fedora.info/license/).
 */
package org.duracloud.customerwebapp.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.duracloud.customerwebapp.domain.Space;
import org.duracloud.customerwebapp.domain.SpaceMetadata;
import org.duracloud.storage.domain.StorageException;
import org.duracloud.storage.provider.StorageProvider;

/**
 * Provides utility methods for spaces.
 *
 * @author Bill Branan
 */
public class SpaceUtil {

    public static List<Space> getSpacesList()
    throws StorageException{
        StorageProvider storage = StorageProviderFactory.getStorageProvider();

        Iterator<String> spaceIds = storage.getSpaces();

        List<Space> spaces = new ArrayList<Space>();
        if(spaceIds != null) {
            while(spaceIds.hasNext()) {
                String spaceId = spaceIds.next();
                Space space = new Space();
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
        spaceMetadata.setCreated(
            spaceProps.get(StorageProvider.METADATA_SPACE_CREATED));
        spaceMetadata.setCount(
            spaceProps.get(StorageProvider.METADATA_SPACE_COUNT));
        spaceMetadata.setAccess(
            spaceProps.get(StorageProvider.METADATA_SPACE_ACCESS));
        return spaceMetadata;
    }
}
