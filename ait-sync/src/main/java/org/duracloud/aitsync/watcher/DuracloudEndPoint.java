package org.duracloud.aitsync.watcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;
import org.duracloud.error.NotFoundException;
import org.duracloud.storage.provider.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public class DuracloudEndPoint implements EndPoint {
    private Logger log = LoggerFactory.getLogger(DuracloudEndPoint.class);

    private ContentStore contentStore;
    private String spaceId;
    private String username;

    public DuracloudEndPoint(
        ContentStore contentStore, String spaceId, String username) {
        super();
        this.contentStore = contentStore;
        this.spaceId = spaceId;
        this.username = username;
    }

    @Override
    public boolean sync(String filename, String md5, URL url, InputStream is) {
        ensureSpaceExists();

        String urlStr = url.toExternalForm();

        log.info("Syncing file "
            + filename + " to DuraCloud with ID " + filename);

        Map<String, String> contentProperties =
            getContentProperties(spaceId, filename);
        boolean dcFileExists = (null != contentProperties);
        try {
            if (dcFileExists) { // File was updated
                String dcChecksum =
                    contentProperties.get(ContentStore.CONTENT_CHECKSUM);
                if (dcChecksum.equals(md5)) {
                    log.debug("Checksum for source stream {} matches "
                        + "file in DuraCloud, no update needed.", urlStr);
                } else {
                    log.debug("source stream {} changed, updating DuraCloud.",
                              urlStr);
                    addUpdateContent(filename, md5, url, is);
                }
            } else { // File was added
                log.debug("Source stream {} added, moving to DuraCloud.",
                          urlStr);

                addUpdateContent(filename, md5, url, is);
            }
            
        } catch (ContentStoreException e) {
            log.error("failed to sync "+url, e);
            return false;
        }

        return true;

    }

    protected Map<String, String> getContentProperties(String spaceId,
                                                       String contentId) {
        Map<String, String> props = null;
        try {
            props = contentStore.getContentProperties(spaceId, contentId);

        } catch (ContentStoreException e) {
            log.info("Content properties !exist: {}/{}", spaceId, contentId);
        }
        return props;
    }

    protected void addUpdateContent(String contentId,
                                    String md5,
                                    URL url,
                                    InputStream is)
        throws ContentStoreException {

        Map<String, String> properties = new HashMap<String, String>();
        properties.put(StorageProvider.PROPERTIES_CONTENT_CREATOR, username);

        try {
            contentStore.addContent(spaceId,
                                    contentId,
                                    is,
                                    0,
                                    null,
                                    md5,
                                    properties);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                log.error("Error attempting to close stream for source  "
                    + url + ": " + e.getMessage(), e);
            }
        }
    }

    private void ensureSpaceExists() {
        boolean spaceExists = false;
        for (int i = 0; i < 10; i++) {
            if (spaceExists()) {
                spaceExists = true;
                break;
            }
            sleep(300);
        }
        if (!spaceExists) {
            throw new RuntimeException("Could not connect to space with ID '"
                + spaceId + "'.");
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    private boolean spaceExists() {
        try {
            try {
                Iterator<String> contents =
                    contentStore.getSpaceContents(spaceId);
                if (contents.hasNext()) {
                    log.warn("The specified space '"
                        + spaceId
                        + "' is not empty. If this space is being used for an "
                        + "activity other than sync there is the possibility "
                        + "of data loss.");
                }
                return true;
            } catch (NotFoundException e) {
                contentStore.createSpace(spaceId, new HashMap<String, String>());
                return false;
            }
        } catch (ContentStoreException e) {
            log.warn("Could not connect to space with ID '"
                + spaceId + "' due to error: " + e.getMessage(), e);
            return false;
        }
    }

}
