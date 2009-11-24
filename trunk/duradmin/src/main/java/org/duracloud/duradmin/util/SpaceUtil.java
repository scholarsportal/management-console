
package org.duracloud.duradmin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.duradmin.domain.ContentItem;
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
                                      org.duracloud.domain.Space cloudSpace) {
        space.setSpaceId(cloudSpace.getId());
        space.setMetadata(getSpaceMetadata(cloudSpace.getMetadata()));
        space.setExtendedMetadata(cloudSpace.getMetadata());
        space.setContents(cloudSpace.getContentIds());
        return space;
    }

    private static SpaceMetadata getSpaceMetadata(Map<String, String> spaceProps) {
        SpaceMetadata spaceMetadata = new SpaceMetadata();
        spaceMetadata.setCreated(spaceProps.get(ContentStore.SPACE_CREATED));
        spaceMetadata.setCount(spaceProps.get(ContentStore.SPACE_COUNT));
        spaceMetadata.setAccess(spaceProps.get(ContentStore.SPACE_ACCESS));
        spaceMetadata.setTags(TagUtil.parseTags(spaceProps.get(TagUtil.TAGS)));
        return spaceMetadata;
    }

    public static void populateContentItem(ContentItem contentItem,
                                           String spaceId,
                                           String contentId,
                                           ContentStore store)
            throws ContentStoreException {
        Map<String, String> contentMetadata =
                store.getContentMetadata(spaceId, contentId);
        ContentMetadata metadata = populateContentMetadata(contentMetadata);
        contentItem.setMetadata(metadata);
        contentItem.setExtendedMetadata(contentMetadata);
    }

    private static ContentMetadata populateContentMetadata(Map<String, String> contentMetadata) {
        ContentMetadata metadata = new ContentMetadata();
        metadata
                .setMimetype(contentMetadata.get(ContentStore.CONTENT_MIMETYPE));
        metadata.setSize(contentMetadata.get(ContentStore.CONTENT_SIZE));
        metadata
                .setChecksum(contentMetadata.get(ContentStore.CONTENT_CHECKSUM));
        metadata
                .setModified(contentMetadata.get(ContentStore.CONTENT_MODIFIED));
        metadata.setTags(TagUtil.parseTags(contentMetadata.get(TagUtil.TAGS)));
       
        return metadata;
    }
}
