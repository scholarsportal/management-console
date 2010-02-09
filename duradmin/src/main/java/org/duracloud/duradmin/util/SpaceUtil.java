
package org.duracloud.duradmin.util;

import java.util.List;
import java.util.Map;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.control.ControllerSupport;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.domain.ContentMetadata;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.domain.SpaceMetadata;
import org.duracloud.error.ContentStoreException;
import org.duracloud.serviceconfig.ServiceInfo;

/**
 * Provides utility methods for spaces.
 * 
 * @author Bill Branan
 */
public class SpaceUtil {

    private static final String IMAGE_J2K_MIME_TYPE = "image/j2k";

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
        populateDownloadURL(contentItem, store);
        
    }
    
    public static void populateDownloadURL(ContentItem contentItem, ContentStore store){
        String mimetype = contentItem.getMetadata().getMimetype();
        String j2KBaseURL = null;
        if(IMAGE_J2K_MIME_TYPE.equals(mimetype)){
            j2KBaseURL = resolveJ2KServiceBaseURL();
        }
        contentItem.setDownloadURL(resolveContentDownloadURL(contentItem,store, j2KBaseURL));
    }
    
    public static void populateDownloadURL(List<ContentItem> contentItems, ContentStore store){
        boolean hasJ2K = false;
        for(ContentItem contentItem : contentItems){
            if(contentItem.getMetadata().getMimetype().equals(IMAGE_J2K_MIME_TYPE)){
                hasJ2K = true;
                break;
            }
        }
        
        String j2KBaseURL = null;
        if(hasJ2K){
            j2KBaseURL = resolveJ2KServiceBaseURL();
        }

        for(ContentItem contentItem : contentItems){
            contentItem.setDownloadURL(resolveContentDownloadURL(contentItem,store, j2KBaseURL));
        }
    }

    
    private static String resolveContentDownloadURL(ContentItem contentItem, ContentStore store, String j2KBaseURL) {
        StringBuffer buf = new StringBuffer();

        buf.append(j2KBaseURL != null && contentItem.getContentMimetype().equals(IMAGE_J2K_MIME_TYPE) ? j2KBaseURL : store.getBaseURL());
        buf.append("/");
        buf.append(contentItem.getSpaceId());
        buf.append("/");
        buf.append(contentItem.getEncodedContentId());
        buf.append("?");
        buf.append("storeID=");
        buf.append(store.getStoreId());
        return buf.toString();
    }
    /**
     * Returns the j2k service base URL if the service is running
     * @return null if the J2K Service is not running
     */
    private static String resolveJ2KServiceBaseURL() {
        try {
            ControllerSupport cs = new ControllerSupport();
            ServicesManager sm = cs.getServicesManager();
            List<ServiceInfo> serviceInfos = sm.getDeployedServices();
            for(ServiceInfo serviceInfo : serviceInfos){
                if(serviceInfo.getContentId().toLowerCase().contains("j2k") && serviceInfo.getDeploymentCount() > 0){
                    int deploymentId = serviceInfo.getDeployments().get(0).getId();
                    Map<String,String> props = sm.getDeployedServiceProps(serviceInfo.getId(), deploymentId);
                    return props.get("url");
                }
            }
            
            return null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        
    }

    private static boolean isJ2KServiceRunning() {
        // TODO Auto-generated method stub
        return true;
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
