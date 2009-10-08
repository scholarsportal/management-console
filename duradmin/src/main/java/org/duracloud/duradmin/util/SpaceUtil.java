
package org.duracloud.duradmin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.domain.SpaceMetadata;

/**
 * Provides utility methods for spaces.
 * 
 * @author Bill Branan
 */
public class SpaceUtil {

    public static List<Space> getSpacesList(List<org.duracloud.domain.Space> cloudSpaces)
            throws Exception {
        List<Space> spaces = new ArrayList<Space>();
        for (org.duracloud.domain.Space cloudSpace : cloudSpaces) {
            Space space = new Space();
            space.setSpaceId(cloudSpace.getId());
            populateSpace(space, cloudSpace);
            spaces.add(space);
        }
        return spaces;
    }

    public static Space populateSpace(Space space,
                                      org.duracloud.domain.Space cloudSpace)
            throws Exception {
        space.setSpaceId(cloudSpace.getId());
        space.setMetadata(SpaceUtil.getSpaceMetadata(cloudSpace.getMetadata()));
        space.setContents(cloudSpace.getContentIds());
        return space;
    }

    public static SpaceMetadata getSpaceMetadata(Map<String, String> spaceProps)
            throws Exception {
        SpaceMetadata spaceMetadata = new SpaceMetadata();
        spaceMetadata.setCreated(spaceProps.get(ContentStore.SPACE_CREATED));
        spaceMetadata.setCount(spaceProps.get(ContentStore.SPACE_COUNT));
        spaceMetadata.setAccess(spaceProps.get(ContentStore.SPACE_ACCESS));
        return spaceMetadata;
    }
}
