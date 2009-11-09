
package org.duracloud.duradmin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.domain.ContentMetadata;
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
        spaceMetadata.setCreated(spaceProps.remove(ContentStore.SPACE_CREATED));
        spaceMetadata.setCount(spaceProps.remove(ContentStore.SPACE_COUNT));
        spaceMetadata.setAccess(spaceProps.remove(ContentStore.SPACE_ACCESS));
        spaceMetadata.setTags(TagUtil.parseTags(spaceProps.remove(TagUtil.TAGS)));
        return spaceMetadata;
    }
    
    public static ContentMetadata populateContentMetadata(Map<String, String> contentMetadata) {
        ContentMetadata metadata = new ContentMetadata();
        metadata
                .setMimetype(contentMetadata.remove(ContentStore.CONTENT_MIMETYPE));
        metadata.setSize(contentMetadata.remove(ContentStore.CONTENT_SIZE));
        metadata
                .setChecksum(contentMetadata.remove(ContentStore.CONTENT_CHECKSUM));
        metadata
                .setModified(contentMetadata.remove(ContentStore.CONTENT_MODIFIED));
        metadata.setTags(TagUtil.parseTags(contentMetadata.remove(TagUtil.TAGS)));

        return metadata;
    }
}
