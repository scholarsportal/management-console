
package org.duracloud.duradmin.util;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ServicesManager;
import org.duracloud.common.web.EncodeUtil;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.domain.ContentMetadata;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.domain.SpaceMetadata;
import org.duracloud.error.ContentStoreException;
import org.duracloud.serviceconfig.ServiceInfo;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Provides utility methods for spaces.
 * 
 * @author Bill Branan
 */
public class SpaceUtil {

    private static final String IMAGE_J2K_MIME_TYPE = "image/jp2";

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
                                           ContentStore store,
                                           ServicesManager servicesManager)
            throws ContentStoreException {
        Map<String, String> contentMetadata =
                store.getContentMetadata(spaceId, contentId);
        ContentMetadata metadata = populateContentMetadata(contentMetadata);
        contentItem.setMetadata(metadata);
        contentItem.setExtendedMetadata(contentMetadata);
        populateURLs(contentItem, store, servicesManager);
        
    }

    private static void populateURLs(ContentItem contentItem,
                                     ContentStore store,
                                     ServicesManager servicesManager) {
        String mimetype = contentItem.getMetadata().getMimetype();
        String j2KBaseURL = null;
        j2KBaseURL = resolveJ2KServiceBaseURL(servicesManager);
        if(j2KBaseURL != null && mimetype.toLowerCase().startsWith("image/")){
            contentItem.setTinyThumbnailURL(formatThumbnail(contentItem, store, j2KBaseURL, 1));
            contentItem.setThumbnailURL(formatThumbnail(contentItem, store, j2KBaseURL, 2));
            contentItem.setViewerURL(formatViewerURL(contentItem, store, j2KBaseURL));
        }

        contentItem.setDownloadURL(formatDownloadURL(contentItem,store));
    }

    


    private static String formatViewerURL(ContentItem contentItem, ContentStore store, String j2KBaseURL) {
         String standardURL = formatDownloadURL(contentItem,store);
         return MessageFormat.format("{0}/viewer.html?rft_id={1}", 
                                     j2KBaseURL, 
                                     EncodeUtil.urlEncode(standardURL));
    }
    

    private static String formatThumbnail(ContentItem contentItem, ContentStore store, String j2KBaseURL, int size) {
        String pattern = "{0}/resolver?url_ver=Z39.88-2004&rft_id={1}&" + 
                            "svc_id=info:lanl-repo/svc/getRegion&svc_val_fmt=info:ofi/fmt:kev:mtx:jpeg2000&" +
                            "svc.format=image/png&svc.level={2}&svc.rotate=0&svc.region=0,0,500,500";

        return MessageFormat.format(
                                    pattern, 
                                    j2KBaseURL,
                                    EncodeUtil.urlEncode(formatDownloadURL(contentItem, store)),
                                    size
                                    );
    }
    
    public static String formatDownloadURL(ContentItem contentItem, ContentStore store) {
        String pattern = "{0}/{1}/{2}?storeID={3}&attachment=true";
 
        return MessageFormat.format(pattern,
                                    store.getBaseURL(),
                                    contentItem.getSpaceId(),
                                    EncodeUtil.urlEncode(contentItem.getContentId()),
                                    store.getStoreId());
    }

    /**
     * Returns the j2k service base URL if the service is running
     * @return null if the J2K Service is not running
     */
    private static String resolveJ2KServiceBaseURL(ServicesManager servicesManager) {
        int deploymentId;
        Map<String, String> props;
        try {
            List<ServiceInfo> serviceInfos = servicesManager.getDeployedServices();
            for (ServiceInfo serviceInfo : serviceInfos) {

                if (serviceInfo.getContentId().toLowerCase().contains("j2k") &&
                    serviceInfo.getDeploymentCount() > 0) {

                    deploymentId = serviceInfo.getDeployments().get(0).getId();
                    props = servicesManager.getDeployedServiceProps(serviceInfo.getId(),
                                                                    deploymentId);

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
