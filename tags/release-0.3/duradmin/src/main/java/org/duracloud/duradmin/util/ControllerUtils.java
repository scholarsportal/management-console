
package org.duracloud.duradmin.util;

import org.springframework.util.StringUtils;

public class ControllerUtils {

    public static void checkContentItemId(String spaceId, String contentId)
            throws IllegalArgumentException {

        checkSpaceId(spaceId);
        if (!StringUtils.hasText(contentId)) {
            throw new IllegalArgumentException("Content ID must be provided.");
        }
    }

    public static void checkSpaceId(String spaceId)
            throws IllegalArgumentException {
        if (!StringUtils.hasText(spaceId)) {
            throw new IllegalArgumentException("Space ID must be provided.");
        }

    }

}
