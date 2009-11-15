
package org.duracloud.duradmin.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.springframework.util.StringUtils;

public class MetadataUtils {

    private static final String NAME_KEY_PREFIX = "ext-metadata-";

    public static void setMetadata(ContentStore contentStore,
                                   String spaceId,
                                   String contentId,
                                   Map<String, String> metadata)
            throws ContentStoreException {
        if (StringUtils.hasText(contentId)) {
            contentStore.setContentMetadata(spaceId, contentId, metadata);
        } else {
            contentStore.setSpaceMetadata(spaceId, metadata);
        }

    }

    public static Map<String, String> getMetadata(ContentStore contentStore,
                                                  String spaceId,
                                                  String contentId)
            throws ContentStoreException {
        if (StringUtils.hasText(contentId)) {
            return contentStore.getContentMetadata(spaceId, contentId);
        } else {
            return contentStore.getSpaceMetadata(spaceId);
        }
    }

    public static List<NameValuePair> convertExtendedMetadata(Map<String, String> metadata) {
        List<NameValuePair> extendedMetadata = new LinkedList<NameValuePair>();
        if (extendedMetadata != null) {
            for (String name : metadata.keySet()) {
                if (name.startsWith(NAME_KEY_PREFIX)) {
                    extendedMetadata.add(new NameValuePair(name
                            .substring(NAME_KEY_PREFIX.length()), metadata
                            .get(name)));
                }
            }
        }

        return extendedMetadata;
    }

    public static Object remove(String name, Map<String, String> metadata) {
        return metadata.remove(MetadataUtils.NAME_KEY_PREFIX + name);
    }

    public static Object add(String name,
                             String value,
                             Map<String, String> metadata) {
        return metadata.put(NAME_KEY_PREFIX + name, value);

    }

}
