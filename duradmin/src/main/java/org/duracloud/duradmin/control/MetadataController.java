
package org.duracloud.duradmin.control;

import java.util.Map;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStoreException;

public abstract class MetadataController
        extends BaseCommandController {
    

    protected final Logger log = Logger.getLogger(getClass());

    protected void setMetadata(String spaceId,
                             String contentId,
                             Map<String, String> metadata) throws ContentStoreException{
        if(contentId != null){
            getContentStore().setContentMetadata(spaceId, contentId, metadata);
        }else{
            getContentStore().setSpaceMetadata(spaceId, metadata);
        }
        
    }

    protected Map<String, String> getMetadata(String spaceId, String contentId) throws ContentStoreException{
        if(contentId != null){
            return getContentStore().getContentMetadata(spaceId, contentId);
        }else{
            return getContentStore().getSpaceMetadata(spaceId);
        }
    }
}