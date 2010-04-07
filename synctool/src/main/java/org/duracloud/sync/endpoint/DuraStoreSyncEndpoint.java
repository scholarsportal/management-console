package org.duracloud.sync.endpoint;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.common.model.Credential;
import org.duracloud.common.util.ChecksumUtil;
import org.duracloud.error.ContentStoreException;
import org.duracloud.error.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Endpoint which pushes files to DuraCloud.
 *
 * @author: Bill Branan
 * Date: Mar 17, 2010
 */
public class DuraStoreSyncEndpoint implements SyncEndpoint {

    private final Logger logger =
        LoggerFactory.getLogger(DuraStoreSyncEndpoint.class);

    private ContentStore contentStore;
    private String spaceId;

    public DuraStoreSyncEndpoint(String host,
                                 int port,
                                 String context,
                                 String username,
                                 String password,
                                 String spaceId) {
        String baseUrl;
        try {
            baseUrl = new URL("http", host, port, context).toString();
        } catch(MalformedURLException e) {
            throw new RuntimeException("Could not create connection to " +
                                       "DuraStore due to: " + e.getMessage());
        }

        ContentStoreManager storeManager =
            new ContentStoreManagerImpl(host, String.valueOf(port), context);
        storeManager.login(new Credential(username, password));

        try {
            contentStore = storeManager.getPrimaryContentStore();
        } catch(ContentStoreException e) {
            throw new RuntimeException("Could not create connection to " +
                "DuraStore due to " + e.getMessage(), e);
        }

        this.spaceId = spaceId;
        ensureSpaceExists();
    }

    private void ensureSpaceExists() {
        boolean spaceExists = false;
        for(int i=0; i<10; i++) {
            if(spaceExists()) {
                spaceExists = true;
                break;
            }
        }
        if(!spaceExists) {
            throw new RuntimeException("Could not connect to space with ID '" +
                spaceId + "'.");
        }
    }

    private boolean spaceExists() {
        try {
            try {
                Iterator<String> contents =
                    contentStore.getSpaceContents(spaceId);
                if(contents.hasNext()) {
                    logger.warn("The specified space '" + spaceId +
                        "' is not empty. If this space is being used for an " +
                        "activity other than sync there is the possibility " +
                        "of data loss.");
                }
                return true;
            } catch (NotFoundException e) {
                contentStore.createSpace(spaceId,
                                         new HashMap<String, String>());
                return false;
            }
        } catch (ContentStoreException e) {
            throw new RuntimeException("Could not connect to space with ID '" +
                spaceId + "' due to error: " + e.getMessage(), e);
        }
    }

    public boolean syncFile(File syncFile, File watchDir) {
        String contentId = getContentId(syncFile, watchDir);
        logger.info("Syncing file " + syncFile.getAbsolutePath() +
                    " to DuraCloud with ID " + contentId);

        try {
            Map<String, String> contentMetadata = null;
            boolean dcFileExists = false;
            try {
                contentMetadata =
                     contentStore.getContentMetadata(spaceId, contentId);
                if(contentMetadata != null) {
                    dcFileExists = true;
                }
            } catch(NotFoundException e) {
                dcFileExists = false;
            }

            if(syncFile.exists()) {
                if(dcFileExists) { // File was updated
                    String dcChecksum =
                        contentMetadata.get(ContentStore.CONTENT_CHECKSUM);
                    String localChecksum = computeChecksum(syncFile);
                    if(dcChecksum.equals(localChecksum)) {
                        logger.debug("Checksum for local file {} matches " +
                            "file in DuraCloud, no update needed.",
                            syncFile.getAbsolutePath());
                    } else {
                        logger.debug("Local file {} changed, updating DuraCloud.",
                                     syncFile.getAbsolutePath());
                        addUpdateContent(contentId, syncFile);
                    }
                } else { // File was added
                    logger.debug("Local file {} added, moving to DuraCloud.",
                                 syncFile.getAbsolutePath());
                    addUpdateContent(contentId, syncFile);
                }
            } else { // File was deleted
                if(dcFileExists) {
                    logger.debug("Local file {} deleted, removing from DuraCloud.",
                                 syncFile.getAbsolutePath());
                    contentStore.deleteContent(spaceId, contentId);
                }
            }
        } catch(ContentStoreException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private void addUpdateContent(String contentId, File syncFile)
        throws ContentStoreException {
        InputStream fileStream;
        try {
            fileStream = new FileInputStream(syncFile);
        } catch(FileNotFoundException e) {
            throw new RuntimeException("Could not get stream for " +
                "file: " + syncFile.getAbsolutePath() + " due to " +
                e.getMessage(), e);
        }

        String mimetype = getMimeType(syncFile);
        contentStore.addContent(spaceId,
                                contentId,
                                fileStream,
                                syncFile.length(),
                                mimetype,
                                null);        
        try {
            fileStream.close();
        } catch(IOException e) {
            logger.error("Error attempting to close stream for file " +
                syncFile.getAbsolutePath() + ": " + e.getMessage(), e);
        }
    }

    /*
     * Determines the MIME type of the file. This is currently not a very
     * robust implementation. Using this method the mime type is determined
     * based on the file extension, and the mapping comes from the file
     * content-types.properties under the lib/ directory of the running JRE
     */
    private String getMimeType(File file) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        return fileNameMap.getContentTypeFor(file.getName());
    }

    /*
     * Determines the content ID of a file: the path of the file relative to
     * the watched directory
     */
    private String getContentId(File syncFile, File watchDir) {
        URI relativeFileURI = watchDir.toURI().relativize(syncFile.toURI());
        return relativeFileURI.getPath();
    }

    /*
     * Computes the checksum of a local file
     */
    private String computeChecksum(File file) {
        try {
        ChecksumUtil cksumUtil = new ChecksumUtil(ChecksumUtil.Algorithm.MD5);
        return cksumUtil.generateChecksum(file);
        } catch(FileNotFoundException e) {
            throw new RuntimeException("File not found: " +
                file.getAbsolutePath(), e);
        }
    }

    public Iterator<String> getFilesList() {
        Iterator<String> spaceContents;
        try {
            spaceContents = contentStore.getSpaceContents(spaceId);
        } catch(ContentStoreException e) {
            throw new RuntimeException("Unable to get list of files from " +
                                       "DuraStore due to: " + e.getMessage());
        }
        return spaceContents;
    }
}
