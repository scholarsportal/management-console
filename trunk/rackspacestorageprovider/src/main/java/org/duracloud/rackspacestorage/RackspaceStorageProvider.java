
package org.duracloud.rackspacestorage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.security.DigestInputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mosso.client.cloudfiles.FilesCDNContainer;
import com.mosso.client.cloudfiles.FilesClient;
import com.mosso.client.cloudfiles.FilesContainer;
import com.mosso.client.cloudfiles.FilesContainerInfo;
import com.mosso.client.cloudfiles.FilesObject;
import com.mosso.client.cloudfiles.FilesObjectMetaData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.duracloud.storage.domain.StorageException;
import org.duracloud.storage.provider.StorageProvider;

import static org.duracloud.storage.util.StorageProviderUtil.compareChecksum;
import static org.duracloud.storage.util.StorageProviderUtil.loadMetadata;
import static org.duracloud.storage.util.StorageProviderUtil.storeMetadata;
import static org.duracloud.storage.util.StorageProviderUtil.wrapStream;

/**
 * Provides content storage backed by Rackspace's Cloud Files service.
 *
 * @author Bill Branan
 */
public class RackspaceStorageProvider
        implements StorageProvider {

    private final Log log = LogFactory.getLog(this.getClass());

    private FilesClient filesClient = null;

    public RackspaceStorageProvider(String username, String apiAccessKey)
            throws StorageException {
        try {
            filesClient = new FilesClient(username, apiAccessKey);
            if (!filesClient.login()) {
                throw new Exception("Login to Rackspace failed");
            }
        } catch (Exception e) {
            String err =
                    "Could not create connection to Rackspace due to error: "
                            + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    public RackspaceStorageProvider(FilesClient filesClient) {
        this.filesClient = filesClient;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getSpaces() throws StorageException {
        log.debug("getSpace()");
        try {
            List<FilesContainer> containers = filesClient.listContainers();

            List<String> spaces = new ArrayList<String>();
            for (FilesContainer container : containers) {
                String containerName = container.getName();
                spaces.add(containerName);
            }
            return spaces.iterator();
        } catch (Exception e) {
            String err =
                    "Could not retrieve list of Rackspace containers due to error: "
                            + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getSpaceContents(String spaceId)
            throws StorageException {
        log.debug("getSpaceContents(" + spaceId + ")");
        String containerName = getContainerName(spaceId);
        String spaceMetadata = containerName + SPACE_METADATA_SUFFIX;
        List<String> spaceContents = getCompleteSpaceContents(spaceId);
        spaceContents.remove(spaceMetadata);
        return spaceContents.iterator();
    }

    private List<String> getCompleteSpaceContents(String spaceId)
            throws StorageException {
        String containerName = getContainerName(spaceId);
        try {
            checkContainerExists(spaceId);
            List<FilesObject> objects = filesClient.listObjects(containerName);
            List<String> contentItems = new ArrayList<String>();
            for (FilesObject object : objects) {
                contentItems.add(object.getName());
            }
            return contentItems;
        } catch (Exception e) {
            String err =
                    "Could not get contents of Rackspace container "
                            + containerName + " due to error: "
                            + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * Checks if a space (container) exists, throws a StorageException if false.
     * Call before attempting to get information from a space (container)
     */
    private void checkContainerExists(String spaceId) throws StorageException {
        String containerName = getContainerName(spaceId);
        try {
            if (!filesClient.containerExists(containerName)) {
                throw new StorageException("Rackspace container "
                        + containerName + " does not exist");
            }
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void createSpace(String spaceId) throws StorageException {
        log.debug("getCreateSpace(" + spaceId + ")");
        String containerName = getContainerName(spaceId);
        try {
            if (!filesClient.containerExists(containerName)) {
                filesClient.createContainer(containerName);

                // Add space metadata
                Map<String, String> spaceMetadata =
                        new HashMap<String, String>();
                Date created = new Date(System.currentTimeMillis());
                spaceMetadata.put(METADATA_SPACE_CREATED, created.toString());
                setSpaceMetadata(containerName, spaceMetadata);
            }
        } catch (Exception e) {
            String err =
                    "Could not create Rackspace container with name "
                            + containerName + " due to error: "
                            + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteSpace(String spaceId) throws StorageException {
        log.debug("deleteSpace(" + spaceId + ")");
        List<String> contents = getCompleteSpaceContents(spaceId);
        for (String contentItem : contents) {
            deleteContent(spaceId, contentItem);
        }

        String containerName = getContainerName(spaceId);
        try {
            if (filesClient.containerExists(containerName)) {
                filesClient.deleteContainer(containerName);
            }
        } catch (Exception e) {
            String err =
                    "Could not delete Rackspace container with name "
                            + containerName + " due to error: "
                            + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getSpaceMetadata(String spaceId)
            throws StorageException {
        log.debug("getSpaceMetadata(" + spaceId + ")");

        // Space metadata is stored as a content item
        String containerName = getContainerName(spaceId);
        InputStream is =
                getContent(spaceId, containerName + SPACE_METADATA_SUFFIX);
        Map<String, String> spaceMetadata = loadMetadata(is);

        try {
            FilesContainerInfo containerInfo =
                    filesClient.getContainerInfo(containerName);

            spaceMetadata.put(METADATA_SPACE_COUNT, String
                    .valueOf(containerInfo.getObjectCount()));

            spaceMetadata.put("space-total-size", String.valueOf(containerInfo
                    .getTotalSize()));

            AccessType access = getSpaceAccess(spaceId);
            spaceMetadata.put(METADATA_SPACE_ACCESS, access.toString());
        } catch (Exception e) {
            String err =
                    "Could not retrieve metadata from Rackspace container "
                            + containerName + " due to error: "
                            + e.getMessage();
            log.warn(err, e);
        }

        return spaceMetadata;
    }

    /**
     * {@inheritDoc}
     */
    public void setSpaceMetadata(String spaceId,
                                 Map<String, String> spaceMetadata)
            throws StorageException {
        log.debug("setSpaceMetadata(" + spaceId + ")");

        if (!spaceMetadata.containsKey(METADATA_SPACE_CREATED)) {
            spaceMetadata.put(METADATA_SPACE_CREATED,
                              creationTimestamp(spaceId));
        }

        String containerName = getContainerName(spaceId);
        ByteArrayInputStream is = storeMetadata(spaceMetadata);
        addContent(spaceId,
                   containerName + SPACE_METADATA_SUFFIX,
                   "text/xml",
                   is.available(),
                   is);
    }

    private String creationTimestamp(String spaceId) throws StorageException {
        Map<String, String> spaceMd = getSpaceMetadata(spaceId);
        String creationTime = spaceMd.get(METADATA_SPACE_CREATED);

        if (creationTime == null) {
            StringBuffer msg = new StringBuffer("Error: ");
            msg.append("No " + METADATA_SPACE_CREATED + " found ");
            msg.append("for spaceId: " + spaceId);
            log.error(msg.toString());
            throw new StorageException(msg.toString());
        }

        return creationTime;
    }

    /**
     * {@inheritDoc}
     */
    public AccessType getSpaceAccess(String spaceId) throws StorageException {
        log.debug("getSpaceAccess(" + spaceId + ")");

        checkContainerExists(spaceId);
        String containerName = getContainerName(spaceId);
        AccessType spaceAccess = AccessType.CLOSED;

        try {
            FilesCDNContainer cdnContainer =
                    filesClient.getCDNContainerInfo(containerName);
            if (cdnContainer.isEnabled()) {
                spaceAccess = AccessType.OPEN;
            }
        } catch (Exception e) {
            //            String err = "Could not retrieve CDN info for Rackspace container " +
            //                          containerName + " due to error: " + e.getMessage();
            //            throw new StorageException(err, e);

            // While a bug in the Rackspace SDK is being ironed out
            // just return CLOSED when an exception is thrown
            // TODO: Return to above code after SDK has been fixed
            spaceAccess = AccessType.CLOSED;
        }

        return spaceAccess;
    }

    /**
     * {@inheritDoc}
     */
    public void setSpaceAccess(String spaceId, AccessType access)
            throws StorageException {
        log.debug("setSpaceAccess(" + spaceId + ", " + access + ")");

        String containerName = getContainerName(spaceId);
        try {
            AccessType currentAccess = getSpaceAccess(spaceId);
            if (!currentAccess.equals(access)) {
                boolean cdnEnabled = false;
                if (access.equals(AccessType.OPEN)) {
                    cdnEnabled = true;
                    filesClient.cdnEnableContainer(containerName);
                } else {
                    filesClient.cdnUpdateContainer(containerName,
                                                   -1,
                                                   cdnEnabled);
                }
            }
        } catch (Exception e) {
            String err =
                    "Could not set Rackspace container " + containerName
                            + " ACL to access type " + access.toString()
                            + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String addContent(String spaceId,
                             String contentId,
                             String contentMimeType,
                             long contentSize,
                             InputStream content) throws StorageException {
        log.debug("addContent(" + spaceId + ", " + contentId + ", "
                + contentMimeType + ", " + contentSize + ")");

        String checksum;
        String containerName = getContainerName(spaceId);
        try {
            checkContainerExists(spaceId);
            Map<String, String> metadata = new HashMap<String, String>();
            metadata.put(METADATA_CONTENT_MIMETYPE, contentMimeType);
            // TODO: Determine how to set Rackspace object mimetype.

            // Wrap the content to be able to compute a checksum during transfer
            DigestInputStream wrappedContent = wrapStream(content);

            filesClient.storeStreamedObject(containerName,
                                            wrappedContent,
                                            contentMimeType,
                                            contentId,
                                            metadata);

            // Compare checksum
            checksum =
                    compareChecksum(this, spaceId, contentId, wrappedContent);
        } catch (Exception e) {
            String err =
                    "Could not add content " + contentId + " with type "
                            + contentMimeType + " and size " + contentSize
                            + " to Rackspace container " + containerName
                            + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
        return checksum;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getContent(String spaceId, String contentId)
            throws StorageException {
        log.debug("getContent(" + spaceId + ", " + contentId + ")");

        String containerName = getContainerName(spaceId);
        InputStream content = null;
        try {
            checkContainerExists(spaceId);
            content = filesClient.getObjectAsStream(containerName, contentId);
        } catch (Exception e) {
            String err =
                    "Could not retrieve content " + contentId
                            + " from Rackspace container " + containerName
                            + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
        return content;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteContent(String spaceId, String contentId)
            throws StorageException {
        log.debug("deleteContent(" + spaceId + ", " + contentId + ")");

        int statusCode = -1;
        String containerName = getContainerName(spaceId);

        try {
            checkContainerExists(spaceId);
            statusCode = filesClient.deleteObject(containerName, contentId);
        } catch (Exception e) {
            String err =
                    "Could not delete content " + contentId
                            + " from Rackspace container " + containerName
                            + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }

        if (statusCode == HTTP_NOT_FOUND) {
            String err =
                    "Object to delete not found: " + spaceId + ", " + contentId;
            log.error(err);
            throw new StorageException(err);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setContentMetadata(String spaceId,
                                   String contentId,
                                   Map<String, String> contentMetadata)
            throws StorageException {
        log.debug("setContentMetadata(" + spaceId + ", " + contentId + ")");

        checkContainerExists(spaceId);

        // Remove calculated properties
        contentMetadata.remove(METADATA_CONTENT_CHECKSUM);
        contentMetadata.remove(METADATA_CONTENT_MODIFIED);
        contentMetadata.remove(METADATA_CONTENT_SIZE);

        String containerName = getContainerName(spaceId);

        // Set a default mime type value if one is not set
        if (!contentMetadata.containsKey(METADATA_CONTENT_MIMETYPE)) {
            contentMetadata.put(METADATA_CONTENT_MIMETYPE, DEFAULT_MIMETYPE);
        }
        // TODO: Determine how to update Rackspace-specific object mimetype

        // Get the object and replace its metadata
        try {
            // TODO: Determine how to update object metadata directly.
            // This doesn't actually push the object metadata to the store.
            //             FilesObjectMetaData objMetadata =
            //                 filesClient.getObjectMetaData(containerName, contentId);
            //             objMetadata.setMetaData(contentMetadata);

            // In the meantime, replace the object. This is terribly inefficient
            // and will only work for small files. Remove ASAP.
            byte[] content = filesClient.getObject(containerName, contentId);
            filesClient
                    .storeObject(containerName,
                                 content,
                                 contentMetadata.get(METADATA_CONTENT_MIMETYPE),
                                 contentId,
                                 contentMetadata);
        } catch (Exception e) {
            String err =
                    "Could not update metadata for content " + contentId
                            + " in Rackspace container " + containerName
                            + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getContentMetadata(String spaceId,
                                                  String contentId)
            throws StorageException {
        log.debug("getContentMetadata(" + spaceId + ", " + contentId + ")");

        checkContainerExists(spaceId);
        String containerName = getContainerName(spaceId);

        FilesObjectMetaData metadata = null;
        try {
            metadata = filesClient.getObjectMetaData(containerName, contentId);
        } catch (Exception e) {
            String err =
                    "Could not retrieve metadata for content " + contentId
                            + " from Rackspace container " + containerName
                            + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }

        if (metadata == null) {
            String err =
                    "No metadata is available for item " + contentId
                            + " in Rackspace container " + containerName;
            throw new StorageException(err);
        }

        Map<String, String> metadataMap = metadata.getMetaData();

        // Set expected metadata values
        // MIMETYPE
        if (!metadataMap.containsKey(METADATA_CONTENT_MIMETYPE)) {
            String mimetype = metadata.getMimeType();
            if (mimetype != null) {
                metadataMap.put(METADATA_CONTENT_MIMETYPE, mimetype);
            }
        }
        // SIZE
        String contentLength = metadata.getContentLength();
        if (contentLength != null) {
            metadataMap.put(METADATA_CONTENT_SIZE, contentLength);
        }
        // CHECKSUM
        String checksum = metadata.getETag();
        if (checksum != null) {
            metadataMap.put(METADATA_CONTENT_CHECKSUM, checksum);
        }
        // MODIFIED DATE
        String modified = metadata.getLastModified();
        if (modified != null) {
            metadataMap.put(METADATA_CONTENT_MODIFIED, modified);
        }

        // Normalize metadata keys to lowercase.
        Map<String, String> resultMap = new HashMap<String, String>();
        Iterator<String> keys = metadataMap.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String val = metadataMap.get(key);
            resultMap.put(key.toLowerCase(), val);
        }

        return resultMap;
    }

    /**
     * Converts a provided space ID into a valid Rackspace container name. From
     * Cloud Files Docs: The only restrictions on Container names is that they
     * cannot contain a forward slash (/) character or a question mark (?)
     * character and they must be less than 64 characters in length (after URL
     * encoding).
     *
     * @param spaceId
     * @return
     */
    protected String getContainerName(String spaceId) {
        String containerName = spaceId;
        containerName = containerName.replaceAll("/", "-");
        containerName = containerName.replaceAll("[?]", "-");
        containerName = containerName.replaceAll("[-]+", "-");
        containerName = FilesClient.sanitizeForURI(containerName);

        if (containerName.length() > 63) {
            containerName = containerName.substring(0, 63);
        }

        return containerName;
    }

}
