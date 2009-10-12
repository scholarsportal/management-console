package org.duracloud.duradmin.util;


public class ControllerUtils {

    public static void checkContentRequestParams(String spaceId,
                                                 String contentId) throws IllegalArgumentException{
        if (StringUtils.isEmptyOrAllWhiteSpace(spaceId)) {
            throw new IllegalArgumentException("Space ID must be provided.");
        }

        if (StringUtils.isEmptyOrAllWhiteSpace(contentId)) {
            throw new IllegalArgumentException("Content ID must be provided.");
        }        
    }

}
