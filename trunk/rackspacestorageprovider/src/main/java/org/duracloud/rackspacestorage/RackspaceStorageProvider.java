
package org.duracloud.rackspacestorage;

import com.mosso.client.cloudfiles.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.storage.error.StorageException;
import static org.duracloud.storage.error.StorageException.*;
import org.duracloud.storage.provider.StorageProvider;
import static org.duracloud.storage.util.StorageProviderUtil.compareChecksum;
import static org.duracloud.storage.util.StorageProviderUtil.loadMetadata;
import static org.duracloud.storage.util.StorageProviderUtil.storeMetadata;
import static org.duracloud.storage.util.StorageProviderUtil.wrapStream;
import org.duracloud.storage.domain.ContentIterator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.text.ParseException;

/**
 * Provides content storage backed by Rackspace's Cloud Files service.
 *
 * @author Bill Branan
 */
public class RackspaceStorageProvider
        implements StorageProvider {

    private final Log log = LogFactory.getLog(this.getClass());

    private FilesClient filesClient = null;

    public RackspaceStorageProvider(String username, String apiAccessKey) {
        try {
            filesClient = new FilesClient(username, apiAccessKey);
            if (!filesClient.login()) {
                throw new Exception("Login to Rackspace failed");
            }
        } catch (Exception e) {
            String err = "Could not connect to Rackspace due to error: "
                    + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    public RackspaceStorageProvider(FilesClient filesClient) {
        this.filesClient = filesClient;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getSpaces() {
        log.debug("getSpace()");

        List<FilesContainer> containers = listContainers();
        List<String> spaces = new ArrayList<String>();
        for (FilesContainer container : containers) {
            String containerName = container.getName();
            spaces.add(containerName);
        }
        return spaces.iterator();
    }

    private List<FilesContainer> listContainers() {
        StringBuilder err = new StringBuilder("Could not retrieve list of " +
                "Rackspace containers due to error: ");
        try {
            return filesClient.listContainers();

        } catch (IOException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);

        } catch (FilesException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getSpaceContents(String spaceId,
                                             String prefix) {
        return new ContentIterator(this, spaceId, prefix);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getSpaceContentsChunked(String spaceId,
                                                String prefix,
                                                long maxResults,
                                                String marker) {
        log.debug("getSpaceContents(" + spaceId + ", " + prefix + ", " +
                                    maxResults + ", " + marker + ")");

        throwIfSpaceNotExist(spaceId);

        String bucketName = getContainerName(spaceId);
        String bucketMetadata = bucketName + SPACE_METADATA_SUFFIX;

        if(maxResults <= 0) {
            maxResults = StorageProvider.DEFAULT_MAX_RESULTS;
        }

        // Queries for maxResults +1 to account for the possibility of needing
        // to remove the space metadata but still maintain a full result
        // set (size == maxResults).
        List<String> spaceContents =
            getCompleteSpaceContents(spaceId, prefix, maxResults + 1, marker);

        if(spaceContents.contains(bucketMetadata)) {
            // Remove space metadata
            spaceContents.remove(bucketMetadata);
        } else if(spaceContents.size() > maxResults) {
            // Remove extra content item
            spaceContents.remove(spaceContents.size()-1);
        }

        return spaceContents;
    }

    private List<String> getCompleteSpaceContents(String spaceId,
                                                  String prefix,
                                                  long maxResults,
                                                  String marker) {
        String containerName = getContainerName(spaceId);

        List<FilesObject> objects = listObjects(containerName,
                                                prefix,
                                                maxResults,
                                                marker);
        List<String> contentItems = new ArrayList<String>();
        for (FilesObject object : objects) {
            contentItems.add(object.getName());
        }
        return contentItems;
    }

    private List<FilesObject> listObjects(String containerName,
                                          String prefix,
                                          long maxResults,
                                          String marker) {
        StringBuilder err = new StringBuilder("Could not get contents of " +
                "Rackspace container " + containerName + " due to error: ");
        try {
            int limit = new Long(maxResults).intValue();
            if (prefix != null) {
                return filesClient.listObjectsStaringWith(containerName,
                                                          prefix,
                                                          null,
                                                          limit,
                                                          marker);
            } else {
                return filesClient.listObjects(containerName,
                                               null,
                                               limit,
                                               marker);
            }
        } catch (IOException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);

        } catch (FilesException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);
        }
    }

    private void throwIfSpaceExists(String spaceId) {
        if (spaceExists(spaceId)) {
            String msg = "Error: Space already exists: " + spaceId;
            throw new StorageException(msg, NO_RETRY);
        }
    }

    private void throwIfSpaceNotExist(String spaceId) {
        if (!spaceExists(spaceId)) {
            String msg = "Error: Space does not exist: " + spaceId;
            throw new StorageException(msg, NO_RETRY);
        }
    }

    private boolean spaceExists(String spaceId) {
        String containerName = getContainerName(spaceId);
        boolean exists = false;
        try {
            exists = filesClient.containerExists(containerName);
        } catch (IOException e) {
        }
        return exists;
    }

    /**
     * {@inheritDoc}
     */
    public void createSpace(String spaceId) {
        log.debug("getCreateSpace(" + spaceId + ")");
        throwIfSpaceExists(spaceId);

        createContainer(spaceId);

        // Add space metadata
        Map<String, String> spaceMetadata = new HashMap<String, String>();
        Date created = new Date(System.currentTimeMillis());
        spaceMetadata.put(METADATA_SPACE_CREATED, formattedDate(created));
        setSpaceMetadata(spaceId, spaceMetadata);
    }

    private String formattedDate(Date created) {
        RFC822_DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        return RFC822_DATE_FORMAT.format(created);
    }

    private void createContainer(String spaceId) {
        String containerName = getContainerName(spaceId);

        StringBuilder err = new StringBuilder("Could not create Rackspace " +
                "container with name " + containerName + " due to error: ");
        try {
            filesClient.createContainer(containerName);

        } catch (IOException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);

        } catch (FilesException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteSpace(String spaceId) {
        log.debug("deleteSpace(" + spaceId + ")");
        throwIfSpaceNotExist(spaceId);

        Iterator<String> contents = getSpaceContents(spaceId, null);
        while(contents.hasNext()) {
            deleteContent(spaceId, contents.next());
        }

        String bucketMetadata =
            getContainerName(spaceId) + SPACE_METADATA_SUFFIX;
        deleteContent(spaceId, bucketMetadata);

        deleteContainer(spaceId);
    }

    private void deleteContainer(String spaceId) {
        String containerName = getContainerName(spaceId);
        StringBuilder err = new StringBuilder("Could not delete Rackspace" +
                " container with name " + containerName + " due to error: ");

        try {
            filesClient.deleteContainer(containerName);

        } catch (IOException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);

        } catch (FilesAuthorizationException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, NO_RETRY);

        } catch (FilesInvalidNameException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, NO_RETRY);

        } catch (FilesNotFoundException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, NO_RETRY);

        } catch (FilesContainerNotEmptyException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getSpaceMetadata(String spaceId) {
        log.debug("getSpaceMetadata(" + spaceId + ")");

        throwIfSpaceNotExist(spaceId);

        // Space metadata is stored as a content item
        String containerName = getContainerName(spaceId);
        InputStream is =
                getContent(spaceId, containerName + SPACE_METADATA_SUFFIX);
        Map<String, String> spaceMetadata = loadMetadata(is);

        FilesContainerInfo containerInfo = getContainerInfo(containerName);

        final int sysMetadataObjectCount = 1;
        int totalObjectCount = containerInfo.getObjectCount();
        int visibleObjectCount = totalObjectCount - sysMetadataObjectCount;
        spaceMetadata.put(METADATA_SPACE_COUNT,
                          String.valueOf(visibleObjectCount));

        spaceMetadata.put("space-total-size", String.valueOf(containerInfo
                .getTotalSize()));

        AccessType access = getSpaceAccess(spaceId);
        spaceMetadata.put(METADATA_SPACE_ACCESS, access.toString());

        return spaceMetadata;
    }

    private FilesContainerInfo getContainerInfo(String containerName) {
        StringBuilder err = new StringBuilder("Could not retrieve metadata " +
                "from Rackspace container " + containerName + " due to error: ");

        try {
            return filesClient.getContainerInfo(containerName);

        } catch (IOException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);

        } catch (FilesException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setSpaceMetadata(String spaceId,
                                 Map<String, String> spaceMetadata) {
        log.debug("setSpaceMetadata(" + spaceId + ")");

        throwIfSpaceNotExist(spaceId);

        Date created = getCreationDate(spaceId, spaceMetadata);
        if (created != null) {
            spaceMetadata.put(METADATA_SPACE_CREATED, formattedDate(created));
        }

        String containerName = getContainerName(spaceId);
        ByteArrayInputStream is = storeMetadata(spaceMetadata);
        addContent(spaceId,
                   containerName + SPACE_METADATA_SUFFIX,
                   "text/xml",
                   is.available(),
                   is);
    }

    private Date getCreationDate(String spaceId,
                                 Map<String, String> spaceMetadata) {
        String dateText;
        if (!spaceMetadata.containsKey(METADATA_SPACE_CREATED)) {
            dateText = getCreationTimestamp(spaceId);
        } else {
            dateText = spaceMetadata.get(METADATA_SPACE_CREATED);
        }

        Date created = null;
        try {
            created =  RFC822_DATE_FORMAT.parse(dateText);
        } catch (ParseException e) {
            log.warn("Unable to parse date: '" + dateText + "'");
        }
        return created;
    }

    private String getCreationTimestamp(String spaceId) {
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
    public AccessType getSpaceAccess(String spaceId) {
        log.debug("getSpaceAccess(" + spaceId + ")");

        throwIfSpaceNotExist(spaceId);

        String containerName = getContainerName(spaceId);
        AccessType spaceAccess = AccessType.CLOSED;

        try {
            FilesCDNContainer cdnContainer = getCDNContainerInfo(containerName);
            if (cdnContainer.isEnabled()) {
                spaceAccess = AccessType.OPEN;
            }
        } catch (StorageException e) {
            // While a bug in the Rackspace SDK is being ironed out
            // just return CLOSED when an exception is thrown
            // TODO: Return to above code after SDK has been fixed
            log.warn("Return temp error access: "+AccessType.CLOSED, e);
            spaceAccess = AccessType.CLOSED;
        }

        return spaceAccess;
    }

    private FilesCDNContainer getCDNContainerInfo(String containerName) {
        StringBuilder err = new StringBuilder("Could not retrieve CDN info " +
                "for Rackspace container " + containerName + " due to error: ");

        try {
            return filesClient.getCDNContainerInfo(containerName);

        } catch (IOException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);

        } catch (FilesException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setSpaceAccess(String spaceId, AccessType access) {
        log.debug("setSpaceAccess(" + spaceId + ", " + access + ")");

        throwIfSpaceNotExist(spaceId);

        String containerName = getContainerName(spaceId);
        AccessType currentAccess = getSpaceAccess(spaceId);
        if (!currentAccess.equals(access)) {
            boolean cdnEnabled = false;
            if (access.equals(AccessType.OPEN)) {
                cdnEnabled = true;
                cdnEnableContainer(containerName);
            } else {
                cdnUpdateContainer(containerName, cdnEnabled);
            }
        }
    }


    private void cdnEnableContainer(String containerName) {
        StringBuilder err = new StringBuilder("Could not set Rackspace "
                + "container " + containerName
                + " ACL to access enabled due to error: ");

        try {
            filesClient.cdnEnableContainer(containerName);

        } catch (IOException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);

        } catch (FilesException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);
        }
    }

    private void cdnUpdateContainer(String containerName, boolean cdnEnabled) {
        StringBuilder err = new StringBuilder("Could not set Rackspace "
                + "container " + containerName
                + " ACL to access enabled: " + cdnEnabled + " due to error: ");

        try {
            filesClient.cdnUpdateContainer(containerName, -1, cdnEnabled);

        } catch (IOException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);

        } catch (FilesException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String addContent(String spaceId,
                             String contentId,
                             String contentMimeType,
                             long contentSize,
                             InputStream content) {
        log.debug("addContent(" + spaceId + ", " + contentId + ", "
                + contentMimeType + ", " + contentSize + ")");

        throwIfSpaceNotExist(spaceId);

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(METADATA_CONTENT_MIMETYPE, contentMimeType);
        // TODO: Determine how to set Rackspace object mimetype.

        // Wrap the content to be able to compute a checksum during transfer
        DigestInputStream wrappedContent = wrapStream(content);

        storeStreamedObject(contentId,
                            contentMimeType,
                            spaceId,
                            metadata,
                            wrappedContent);

        // Compare checksum
        return compareChecksum(this, spaceId, contentId, wrappedContent);
    }

    private void storeStreamedObject(String contentId, String contentMimeType,
                                     String spaceId,
                                     Map<String, String> metadata,
                                     DigestInputStream wrappedContent) {
        String containerName = getContainerName(spaceId);
        StringBuilder err = new StringBuilder("Could not add content "
                + contentId + " with type " + contentMimeType
                + " to Rackspace container " + containerName
                + " due to error: ");

        try {
            filesClient.storeStreamedObject(containerName,
                                            wrappedContent,
                                            contentMimeType,
                                            contentId,
                                            metadata);
        } catch (IOException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, NO_RETRY);

        } catch (FilesException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, NO_RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getContent(String spaceId, String contentId) {
        log.debug("getContent(" + spaceId + ", " + contentId + ")");

        throwIfSpaceNotExist(spaceId);

        String containerName = getContainerName(spaceId);

        StringBuilder err = new StringBuilder("Could not retrieve content "
                + contentId + " from Rackspace container " + containerName
                + " due to error: ");
        try {
            return filesClient.getObjectAsStream(containerName, contentId);
        } catch (IOException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);

        } catch (FilesAuthorizationException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, NO_RETRY);

        } catch (FilesInvalidNameException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, NO_RETRY);
            
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteContent(String spaceId, String contentId) {
        log.debug("deleteContent(" + spaceId + ", " + contentId + ")");

        throwIfSpaceNotExist(spaceId);

        int statusCode = deleteObject(contentId, spaceId);
        if (statusCode == HTTP_NOT_FOUND) {
            String err = "Object to delete not found: " + spaceId + ":" + contentId;
            log.error(err);
            throw new StorageException(err, RETRY);
        }
    }

    private int deleteObject(String contentId,
                             String spaceId) {
        String containerName = getContainerName(spaceId);
        StringBuilder err = new StringBuilder("Could not delete content " + contentId
                    + " from Rackspace container " + containerName
                    + " due to error: ");

        try {
            return filesClient.deleteObject(containerName, contentId);
        } catch (IOException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);

        } catch (FilesAuthorizationException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, NO_RETRY);

        } catch (FilesInvalidNameException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, NO_RETRY);

        }
    }

    /**
     * {@inheritDoc}
     */
    public void setContentMetadata(String spaceId,
                                   String contentId,
                                   Map<String, String> contentMetadata) {
        log.debug("setContentMetadata(" + spaceId + ", " + contentId + ")");

        throwIfSpaceNotExist(spaceId);

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
        // TODO: Determine how to update object metadata directly.
        // This doesn't actually push the object metadata to the store.
        //             FilesObjectMetaData objMetadata =
        //              filesClient.getObjectMetaData(containerName, contentId);
        //             objMetadata.setMetaData(contentMetadata);

        // In the meantime, replace the object. This is terribly inefficient
        // and will only work for small files. Remove ASAP.
        byte[] content = getObject(contentId, containerName);
        storeObject(contentId, contentMetadata, containerName, content);

    }

    private byte[] getObject(String contentId, String containerName) {
        StringBuilder err = new StringBuilder("Could not get object for "
                + contentId + " in Rackspace container "
                + containerName + " due to error: ");

        try {
            return filesClient.getObject(containerName, contentId);

        } catch (IOException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);

        } catch (FilesAuthorizationException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, NO_RETRY);

        } catch (FilesInvalidNameException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, NO_RETRY);

        } catch (FilesNotFoundException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);

        }
    }

    private void storeObject(String contentId,
                             Map<String, String> contentMetadata,
                             String containerName,
                             byte[] content) {
        StringBuilder err = new StringBuilder("Could not update metadata "
                + "for content " + contentId + " in Rackspace container "
                + containerName + " due to error: ");

        try {
            filesClient.storeObject(containerName,
                                    content,
                                    contentMetadata.get(METADATA_CONTENT_MIMETYPE),
                                    contentId,
                                    contentMetadata);
        } catch (IOException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);

        } catch (FilesException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);

        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getContentMetadata(String spaceId,
                                                  String contentId) {
        log.debug("getContentMetadata(" + spaceId + ", " + contentId + ")");

        throwIfSpaceNotExist(spaceId);

        FilesObjectMetaData metadata = getObjectMetadata(spaceId, contentId);
        if (metadata == null) {
            String err = "No metadata is available for item " + contentId
                    + " in Rackspace space " + spaceId;
            throw new StorageException(err, RETRY);
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

    private FilesObjectMetaData getObjectMetadata(String spaceId,
                                                  String contentId) {
        String containerName = getContainerName(spaceId);

        StringBuilder err = new StringBuilder("Could not retrieve metadata"
                + " for content " + contentId
                + " from Rackspace container " + containerName
                + " due to error: ");

        try {
            return filesClient.getObjectMetaData(containerName, contentId);

        } catch (IOException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, RETRY);

        } catch (FilesAuthorizationException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, NO_RETRY);

        } catch (FilesInvalidNameException e) {
            err.append(e.getMessage());
            throw new StorageException(err.toString(), e, NO_RETRY);
        }
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
