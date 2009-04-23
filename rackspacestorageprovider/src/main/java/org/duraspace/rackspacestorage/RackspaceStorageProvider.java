package org.duraspace.rackspacestorage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.mosso.client.cloudfiles.FilesCDNContainer;
import com.mosso.client.cloudfiles.FilesClient;
import com.mosso.client.cloudfiles.FilesContainer;
import com.mosso.client.cloudfiles.FilesContainerInfo;
import com.mosso.client.cloudfiles.FilesObject;
import com.mosso.client.cloudfiles.FilesObjectMetaData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.duraspace.storage.domain.StorageException;
import org.duraspace.storage.provider.StorageProvider;


/**
 * Provides content storage backed by Rackspace's Cloud Files service.
 *
 * @author Bill Branan
 */
public class RackspaceStorageProvider implements StorageProvider {

    private final Log log = LogFactory.getLog(this.getClass());
    protected static final String SPACE_METADATA_SUFFIX = "-space-metadata";

    private FilesClient filesClient = null;

    public RackspaceStorageProvider(String username, String apiAccessKey)
    throws StorageException {
        try {
            filesClient = new FilesClient(username, apiAccessKey);
            if(!filesClient.login()) {
                throw new Exception("Login to Rackspace failed");
            }
        } catch(Exception e) {
            String err = "Could not create connection to Rackspace due to error: " +
                         e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getSpaces()
    throws StorageException {
        try {
            List<FilesContainer> containers = filesClient.listContainers();

            List<String> spaces = new ArrayList<String>();
            for(FilesContainer container : containers) {
                String containerName = container.getName();
                spaces.add(containerName);
            }
            return spaces;
        } catch(Exception e) {
            String err = "Could not retrieve list of Rackspace containers due to error: " +
                         e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getSpaceContents(String spaceId)
    throws StorageException {
        String containerName = getContainerName(spaceId);
        String spaceMetadata = containerName+SPACE_METADATA_SUFFIX;
        List<String> spaceContents = getCompleteSpaceContents(spaceId);
        spaceContents.remove(spaceMetadata);
        return spaceContents;
    }

    private List<String> getCompleteSpaceContents(String spaceId)
    throws StorageException {
        String containerName = getContainerName(spaceId);
        try {
            List<FilesObject> objects = filesClient.listObjects(containerName);
            List<String> contentItems = new ArrayList<String>();
            for(FilesObject object : objects) {
                contentItems.add(object.getName());
            }
            return contentItems;
        } catch(Exception e) {
            String err = "Could not get contents of Rackspace container " +
                         containerName + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void createSpace(String spaceId)
    throws StorageException {
        String containerName = getContainerName(spaceId);
        try {
            if(!filesClient.containerExists(containerName)) {
                filesClient.createContainer(containerName);

                // Add space metadata
                Properties spaceProps = new Properties();
                Date created = new Date(System.currentTimeMillis());
                spaceProps.put(METADATA_SPACE_CREATED, created.toString());
                spaceProps.put(METADATA_SPACE_NAME, containerName);
                setSpaceMetadata(containerName, spaceProps);
            }
        } catch(Exception e) {
            String err = "Could not create Rackspace container with name " +
                         containerName + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteSpace(String spaceId)
    throws StorageException {
        List<String> contents = getCompleteSpaceContents(spaceId);
        for(String contentItem : contents) {
            deleteContent(spaceId, contentItem);
        }

        String containerName = getContainerName(spaceId);
        try {
            if(filesClient.containerExists(containerName)) {
                filesClient.deleteContainer(containerName);
            }
        } catch(Exception e) {
            String err = "Could not delete Rackspace container with name " +
                         containerName + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Properties getSpaceMetadata(String spaceId)
    throws StorageException {
        // Space metadata is stored as a content item
        String containerName = getContainerName(spaceId);
        InputStream is = getContent(spaceId,
                                    containerName+SPACE_METADATA_SUFFIX);

        Properties spaceProps = new Properties();
        if(is != null) {
            try {
                spaceProps.loadFromXML(is);
                is.close();
            } catch(Exception e) {
                String err = "Could not read metadata for space " + spaceId +
                             " due to error: " + e.getMessage();
                throw new StorageException(err, e);
            }
        }

        try {
            FilesContainerInfo containerInfo =
                filesClient.getContainerInfo(containerName);

            spaceProps.put(METADATA_SPACE_COUNT,
                           String.valueOf(containerInfo.getObjectCount()));

            spaceProps.put(METADATA_SPACE_SIZE,
                           String.valueOf(containerInfo.getTotalSize()));

            AccessType access = getSpaceAccess(spaceId);
            spaceProps.put(METADATA_SPACE_ACCESS, access.toString());
        } catch(Exception e) {
            String err = "Could not retrieve metadata from S3 bucket " +
                         containerName + " due to error: " + e.getMessage();
            log.warn(err, e);
        }

        return spaceProps;
    }

    /**
     * {@inheritDoc}
     */
    public void setSpaceMetadata(String spaceId,
                                 Properties spaceMetadata)
    throws StorageException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        // Pull out any computed values
        spaceMetadata.remove(METADATA_SPACE_COUNT);
        spaceMetadata.remove(METADATA_SPACE_ACCESS);

        try {
            spaceMetadata.storeToXML(os, "Metadata for " + spaceId);
        } catch (IOException e) {
            String err = "Could not set metadata for space " + spaceId +
                         " due to error: " + e.getMessage();
            throw new StorageException(err);
        }

        String containerName = getContainerName(spaceId);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        addContent(spaceId,
                   containerName+SPACE_METADATA_SUFFIX,
                   "text/xml",
                   is.available(),
                   is);
    }

    /**
     * {@inheritDoc}
     */
    public AccessType getSpaceAccess(String spaceId)
    throws StorageException {
        String containerName = getContainerName(spaceId);
        AccessType spaceAccess = AccessType.CLOSED;

        try {
            FilesCDNContainer cdnContainer =
                filesClient.getCDNContainerInfo(containerName);
            if(cdnContainer.isEnabled()) {
                spaceAccess = AccessType.OPEN;
            }
        } catch(Exception e) {
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
    public void setSpaceAccess(String spaceId,
                               AccessType access)
    throws StorageException {
        String containerName = getContainerName(spaceId);
        try {
            AccessType currentAccess = getSpaceAccess(spaceId);
            if(!currentAccess.equals(access)) {
                boolean cdnEnabled = false;
                if(access.equals(AccessType.OPEN)) {
                    cdnEnabled = true;
                    filesClient.cdnEnableContainer(containerName);
                } else {
                    filesClient.cdnUpdateContainer(containerName, -1, cdnEnabled);
                }
            }
        } catch(Exception e) {
            String err = "Could not set Rackspace container " + containerName +
                         " ACL to access type " + access.toString() +
                         " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addContent(String spaceId,
                           String contentId,
                           String contentMimeType,
                           long contentSize,
                           InputStream content)
    throws StorageException {
        String containerName = getContainerName(spaceId);
        try {
            Map<String, String> metadata = new HashMap<String, String>();
            metadata.put(METADATA_CONTENT_NAME, contentId);
            metadata.put(METADATA_CONTENT_MIMETYPE, contentMimeType);
            // TODO: Determine how to set Rackspace object mimetype.

            filesClient.storeStreamedObject(containerName,
                                            content,
                                            contentMimeType,
                                            contentId,
                                            metadata);
        } catch(Exception e) {
            String err = "Could not add content " + contentId + " with type " +
                         contentMimeType + " and size " + contentSize +
                         " to Rackspace container " + containerName +
                         " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getContent(String spaceId,
                                  String contentId)
    throws StorageException {
        String containerName = getContainerName(spaceId);
        InputStream content = null;
        try {
            content = filesClient.getObjectAsStream(containerName, contentId);
        } catch(Exception e) {
            String err = "Could not retrieve content " + contentId +
                         " from Rackspace container " + containerName +
                         " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
        return content;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteContent(String spaceId,
                              String contentId)
    throws StorageException {
        String containerName = getContainerName(spaceId);
        try {
            filesClient.deleteObject(containerName, contentId);
        } catch(Exception e) {
            String err = "Could not delete content " + contentId +
                         " from Rackspace container " + containerName +
                         " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setContentMetadata(String spaceId,
                                   String contentId,
                                   Properties contentMetadata)
    throws StorageException {
        String containerName = getContainerName(spaceId);

        FilesObjectMetaData metadata = null;
        Map<String, String> metamap = null;
        try {
            metadata = filesClient.getObjectMetaData(containerName, contentId);
            metamap = metadata.getMetaData();
        } catch(Exception e) {
            String err = "Could not retrieve metadata for content " +
                         contentId + " from Rackspace container " +
                         containerName + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }

        if(metamap == null) {
            metamap = new HashMap<String, String>();
        }

        // Add Properties into metadata Map
        Enumeration<?> metadataKeys = contentMetadata.propertyNames();
        while(metadataKeys.hasMoreElements()) {
            String key = (String)metadataKeys.nextElement();
            String value = contentMetadata.getProperty(key);
            if(key != null &&
               !key.equals(METADATA_CONTENT_CHECKSUM) &&
               !key.equals(METADATA_CONTENT_MODIFIED) &&
               !key.equals(METADATA_CONTENT_SIZE) &&
               //!key.equals(METADATA_CONTENT_MIMETYPE) &&
               value != null) {
                metamap.put(key, value);
            }
        }

        // Set name to contentId if it is not set already
        if(!metamap.containsKey(METADATA_CONTENT_NAME)) {
            metamap.put(METADATA_CONTENT_NAME, contentId);
        }

        // Set mimetype
        // TODO: Determine how to update Rackspace object mimetype
//        String newMimeType =
//            contentMetadata.getProperty(METADATA_CONTENT_MIMETYPE);
//        if(newMimeType != null && !newMimeType.equals("")) {
//            metamap.put("Content-Type", newMimeType);
//        }

        // Get the object and replace its metadata
        try {
            // TODO: This doesn't actually push the object metadata to the object
            metadata.setMetaData(metamap);

            // In the meantime, replace the object. This is terribly inefficient
            // and will only work for small files. Remove ASAP.
            byte[] content = filesClient.getObject(containerName, contentId);
            filesClient.storeObject(containerName,
                                    content,
                                    metadata.getMimeType(),
                                    contentId,
                                    metamap);
        } catch(Exception e) {
            String err = "Could not update metadata for content " +
                         contentId + " in Rackspace container " +
                         containerName + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Properties getContentMetadata(String spaceId,
                                         String contentId)
    throws StorageException {
        String containerName = getContainerName(spaceId);

        FilesObjectMetaData metadata = null;
        try {
            metadata = filesClient.getObjectMetaData(containerName, contentId);

        } catch(Exception e) {
            String err = "Could not retrieve metadata for content " +
                         contentId + " from Rackspace container " +
                         containerName + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }

        if(metadata == null) {
            String err = "No metadata is available for item " + contentId +
                         " in Rackspace container " + containerName;
            throw new StorageException(err);
        }

        // Convert from Map to Properties
        Map<String, String> metadataMap = metadata.getMetaData();
        Properties metaProps = new Properties();
        Iterator<String> metadataKeys = metadataMap.keySet().iterator();
        while(metadataKeys.hasNext()) {
            String key = metadataKeys.next();
            metaProps.put(key, metadataMap.get(key));
        }

        // Set MIMETYPE
        String mimetype = metadata.getMimeType();
        if(mimetype != null) {
            metaProps.put(METADATA_CONTENT_MIMETYPE, mimetype);
        }

        // Set SIZE
        String contentLength = metadata.getContentLength();
        if(contentLength != null) {
            metaProps.put(METADATA_CONTENT_SIZE, contentLength);
        }

        // Set CHECKSUM
        String checksum = metadata.getETag();
        if(checksum != null) {
            metaProps.put(METADATA_CONTENT_CHECKSUM, checksum);
        }

        // Set MODIFIED
        String modified = metadata.getLastModified();
        if(modified != null) {
            metaProps.put(METADATA_CONTENT_MODIFIED, modified);
        }

        return metaProps;
    }

    /**
     * Converts a provided space ID into a valid Rackspace
     * container name.
     *
     * From Cloud Files Docs:
     * The only restrictions on Container names is that they cannot
     * contain a forward slash, �/� character or a question mark,
     * �?� character and they must be less than 64 characters in
     * length (after URL encoding).
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

        if(containerName.length() > 63) {
            containerName = containerName.substring(0, 63);
        }

        return containerName;
    }

}
