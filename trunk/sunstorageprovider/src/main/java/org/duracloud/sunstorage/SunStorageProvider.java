package org.duracloud.sunstorage;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.security.DigestInputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sun.cloud.api.object.client.ObjectClient;
import com.sun.cloud.api.object.exceptions.ClientException;
import com.sun.cloud.api.object.model.Bucket;
import com.sun.cloud.api.object.model.ObjectInfo;
import com.sun.cloud.api.object.model.Owner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.duracloud.storage.domain.StorageException;
import org.duracloud.storage.provider.StorageProvider;

import static org.duracloud.storage.util.StorageProviderUtil.compareChecksum;
import static org.duracloud.storage.util.StorageProviderUtil.loadMetadata;
import static org.duracloud.storage.util.StorageProviderUtil.storeMetadata;
import static org.duracloud.storage.util.StorageProviderUtil.wrapStream;

/**
 * Provides content storage backed by the Sun Cloud Storage Service.
 *
 * @author Bill Branan
 */
public class SunStorageProvider implements StorageProvider {

    private final Log log = LogFactory.getLog(this.getClass());
    private static final String SUN_CLOUD_URI = "http://object.storage.network.com";

    private String accessKey = null;
    private ObjectClient sunService = null;

    public SunStorageProvider(String accessKey, String secretKey)
    throws StorageException {
        this.accessKey = accessKey;

        try {
            sunService = new ObjectClient(SUN_CLOUD_URI, accessKey, secretKey);
        } catch(Exception e) {
            String err = "Could not create connection to Sun Cloud due to error: " +
                         e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getSpaces()
    throws StorageException {
        try {
            Bucket[] buckets = sunService.getAllBuckets();
            List<String> spaces = new ArrayList<String>();
            for(Bucket bucket : buckets) {
                String bucketName = bucket.getName();
                if(isSpace(bucketName)) {
                    spaces.add(getSpaceId(bucketName));
                }
            }
            return spaces.iterator();
        } catch(Exception e) {
            String err = "Could not retrieve list of Sun buckets due to error: " +
            e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getSpaceContents(String spaceId)
    throws StorageException {
        String bucketName = getBucketName(spaceId);
        String bucketMetadata = bucketName+SPACE_METADATA_SUFFIX;
        List<String> spaceContents = getCompleteSpaceContents(spaceId);
        spaceContents.remove(bucketMetadata);
        return spaceContents.iterator();
    }

    private List<String> getCompleteSpaceContents(String spaceId)
    throws StorageException {
        String bucketName = getBucketName(spaceId);
        try {
            ObjectInfo[] objects = sunService.getBucket(bucketName);

            List<String> contentItems = new ArrayList<String>();
            for(ObjectInfo object : objects) {
                contentItems.add(object.getKey());
            }
            return contentItems;
        } catch(ClientException e) {
            String err = "Could not get contents of Sun bucket " + bucketName +
                         " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void createSpace(String spaceId)
    throws StorageException {
        String bucketName = getBucketName(spaceId);
        try {
            sunService.putBucket(bucketName);

            // Add space metadata
            Map<String, String> spaceMetadata = new HashMap<String, String>();
            Date created = new Date(System.currentTimeMillis());
            spaceMetadata.put(METADATA_SPACE_CREATED, created.toString());
            setSpaceMetadata(spaceId, spaceMetadata);
        } catch(Exception e) {
            String err = "Could not create Sun bucket with name " + bucketName +
                         " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteSpace(String spaceId)
    throws StorageException {
        String bucketName = getBucketName(spaceId);
        try {
            sunService.deleteBucket(bucketName, true);
        } catch(Exception e) {
            String err = "Could not delete Sun bucket with name " + bucketName +
                         " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getSpaceMetadata(String spaceId)
    throws StorageException {
        // Space metadata is stored as a content item
        String bucketName = getBucketName(spaceId);
        InputStream is = getContent(spaceId, bucketName+SPACE_METADATA_SUFFIX);
        Map<String, String> spaceMetadata = loadMetadata(is);

        List<String> spaceContents = getCompleteSpaceContents(spaceId);
        spaceMetadata.put(METADATA_SPACE_COUNT, String.valueOf(spaceContents.size()));

        AccessType access = getSpaceAccess(spaceId);
        spaceMetadata.put(METADATA_SPACE_ACCESS, access.toString());

        return spaceMetadata;
    }

    /**
     * {@inheritDoc}
     */
    public void setSpaceMetadata(String spaceId,
                                 Map<String, String> spaceMetadata)
    throws StorageException {
        String bucketName = getBucketName(spaceId);
        ByteArrayInputStream is = storeMetadata(spaceMetadata);
        addContent(spaceId,
                   bucketName+SPACE_METADATA_SUFFIX,
                   "text/xml",
                   is.available(),
                   is);
    }

    /**
     * {@inheritDoc}
     *
     * The Sun Cloud service does not currently support access control.
     * All spaces are CLOSED and cannot be set to OPEN.
     */
    public AccessType getSpaceAccess(String spaceId)
    throws StorageException {
        return AccessType.CLOSED;
    }

    /**
     * {@inheritDoc}
     *
     * The Sun Cloud service does not currently support access control.
     * All spaces are CLOSED and cannot be set to OPEN.
     */
    public void setSpaceAccess(String spaceId,
                               AccessType access)
    throws StorageException {
        String err = "The Sun Cloud service does not currently support " +
                     "access control. All spaces are CLOSED and cannot " +
                     "be set to OPEN.";
        throw new UnsupportedOperationException(err);
    }

    /**
     * {@inheritDoc}
     */
    public String addContent(String spaceId,
                             String contentId,
                             String contentMimeType,
                             long contentSize,
                             InputStream content)
    throws StorageException {
        // Wrap the content to be able to compute a checksum during transfer
        DigestInputStream wrappedContent = wrapStream(content);

        File tempFile = null;
        try {
            tempFile = File.createTempFile("duracloud-", "-temp");
            BufferedOutputStream tempOut =
                new BufferedOutputStream(new FileOutputStream(tempFile));
            int cByte = -1;
            while((cByte = wrappedContent.read()) >= 0) {
                tempOut.write(cByte);
            }
            tempOut.close();
            wrappedContent.close();
        } catch(Exception e) {
            String err = "Error writing content to temporary storage: " +
                         e.getMessage();
            throw new StorageException(err);
        }

        String bucketName = getBucketName(spaceId);
        try {
            sunService.putObject(bucketName,
                                 contentId,
                                 tempFile.getAbsolutePath());
        } catch(ClientException e) {
          String err = "Could not add content " + contentId +
                       " to Sun bucket " + bucketName +
                       " due to error: " + e.getMessage();
            throw new StorageException(err);
        }

        tempFile.delete();

        // Compare checksum
        String checksum =
            compareChecksum(this, spaceId, contentId, wrappedContent);

        // Set default content metadata values
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(METADATA_CONTENT_MIMETYPE, contentMimeType);
        setContentMetadata(spaceId, contentId, metadata);

        return checksum;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getContent(String spaceId,
                                  String contentId)
    throws StorageException {
        String bucketName = getBucketName(spaceId);
        InputStream content = null;
        try {
            content = sunService.getObject(bucketName, contentId).
                      getDataInputStream();
        } catch(Exception e) {
            content = null;
            String err = "Could not retrieve content " + contentId +
                         " in Sun bucket " + bucketName + " due to error: " +
                         e.getMessage();
            log.warn(err);
        }
        return content;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteContent(String spaceId,
                              String contentId)
    throws StorageException {
        String bucketName = getBucketName(spaceId);
        try {
            sunService.deleteObject(bucketName, contentId);
        } catch(Exception e) {
            String err = "Could not delete content " + contentId +
                         " from S3 bucket " + bucketName +
                         " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Sun Cloud does not currently support storing user metadata
     * with content. Due to that fact, all content metadata must
     * be stored as a separate content file.
     */
    public void setContentMetadata(String spaceId,
                                   String contentId,
                                   Map<String, String> contentMetadata)
    throws StorageException {
        // Don't create a metadata file for metadata files.
        // If storing metadata along with content is ever
        // implemented in Sun Cloud part of this check can go away.
        if(contentId.endsWith(SPACE_METADATA_SUFFIX) ||
           contentId.endsWith(CONTENT_METADATA_SUFFIX)) {
            return;
        }

        // Pull out values known by the storage system
        contentMetadata.remove(METADATA_CONTENT_CHECKSUM);
        contentMetadata.remove(METADATA_CONTENT_MODIFIED);
        contentMetadata.remove(METADATA_CONTENT_SIZE);
        contentMetadata.remove(ObjectInfo.METADATA_HEADER_ETAG);
        contentMetadata.remove(ObjectInfo.METADATA_HEADER_LAST_MODIFIED_DATE);
        contentMetadata.remove(ObjectInfo.METADATA_HEADER_CONTENT_LENGTH);

        // TODO: Set Content-Type to mimetype once content metadata is
        // editable in sun cloud.
        contentMetadata.remove(ObjectInfo.METADATA_HEADER_CONTENT_TYPE);

        ByteArrayInputStream is = storeMetadata(contentMetadata);
        addContent(spaceId,
                   contentId+CONTENT_METADATA_SUFFIX,
                   "text/xml",
                   is.available(),
                   is);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getContentMetadata(String spaceId,
                                                  String contentId)
    throws StorageException {
        String bucketName = getBucketName(spaceId);

        // Get the content item headers
        ObjectInfo objectInfo = null;
        try {
            objectInfo = sunService.getObjectHeader(bucketName, contentId);
        } catch(Exception e) {
            String err = "Could not retrieve headers for content " +
                         contentId + " from Sun bucket " + bucketName +
                         " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }

        Map<String, String> combinedMetadata = new HashMap<String, String>();

        // Add the content item header metdata

        String contentEncoding = objectInfo.getContentEncoding();
        if(contentEncoding != null && !contentEncoding.equals("")) {
            combinedMetadata.put(ObjectInfo.METADATA_HEADER_CONTENT_ENCODING,
                                 contentEncoding);
        }

        String contentLanguage = objectInfo.getContentLanguage();
        if(contentLanguage != null && !contentLanguage.equals("")) {
            combinedMetadata.put(ObjectInfo.METADATA_HEADER_CONTENT_LANGUAGE,
                                 contentLanguage);
        }

        long contentLength = objectInfo.getContentLength();
        if(contentLength > 0) {
            String length = String.valueOf(contentLength);
            combinedMetadata.put(ObjectInfo.METADATA_HEADER_CONTENT_LENGTH, length);
            combinedMetadata.put(METADATA_CONTENT_SIZE, length);
        }

        String contentType = objectInfo.getContentType();
        if(contentType != null && !contentType.equals("")) {
            combinedMetadata.put(ObjectInfo.METADATA_HEADER_CONTENT_TYPE,
                              contentType);
            combinedMetadata.put(METADATA_CONTENT_MIMETYPE, contentType);
        }

        String contentETag = objectInfo.getETag();
        if(contentETag != null && !contentETag.equals("")) {
            combinedMetadata.put(ObjectInfo.METADATA_HEADER_ETAG, contentETag);
            combinedMetadata.put(METADATA_CONTENT_CHECKSUM, contentETag);
        }

        Date contentLastModified = objectInfo.getLastModifiedDate();
        if(contentLastModified != null) {
            String lastModified = RFC822_DATE_FORMAT.format(contentLastModified);
            combinedMetadata.put(ObjectInfo.METADATA_HEADER_LAST_MODIFIED_DATE,
                                 lastModified);
            combinedMetadata.put(METADATA_CONTENT_MODIFIED,
                                 lastModified);
        }

        Owner contentOwner = objectInfo.getOwner();
        if(contentOwner != null) {
            combinedMetadata.put(ObjectInfo.METADATA_HEADER_OWNER,
                                 contentOwner.getDisplayName());
        }

        // Get the custom metadata file which is stored as a content item
        InputStream is = getContent(spaceId, contentId+CONTENT_METADATA_SUFFIX);
        Map<String, String> contentMetadata = loadMetadata(is);

        // Add any custom metadata values, overwrite any duplicates
        if(!contentMetadata.isEmpty()) {
            Iterator<String> names = contentMetadata.keySet().iterator();
            while(names.hasNext()) {
                String name = names.next();
                String value = contentMetadata.get(name);
                combinedMetadata.put(name, value);
            }
        }

        return combinedMetadata;
    }

    /**
     * Converts a provided space ID into a valid and unique
     * S3 bucket name.
     *
     * @param spaceId
     * @return
     */
    protected String getBucketName(String spaceId) {
        String bucketName = accessKey + "." + spaceId;
        bucketName = bucketName.toLowerCase();
        bucketName = bucketName.replaceAll("[^a-z0-9-.]", "-");

        // Remove duplicate separators (. and -)
        while(bucketName.contains("--") ||
              bucketName.contains("..") ||
              bucketName.contains("-.") ||
              bucketName.contains(".-")) {
            bucketName = bucketName.replaceAll("[-]+", "-");
            bucketName = bucketName.replaceAll("[.]+", ".");
            bucketName = bucketName.replaceAll("-[.]", "-");
            bucketName = bucketName.replaceAll("[.]-", ".");
        }

        if(bucketName.length() > 63) {
            bucketName = bucketName.substring(0, 63);
        }
        while(bucketName.endsWith("-") || bucketName.endsWith(".")) {
            bucketName = bucketName.substring(0, bucketName.length()-1);
        }
        return bucketName;
    }

    /**
     * Converts a bucket name into what could be passed in as a space ID.
     *
     * @param bucketName
     * @return
     */
    protected String getSpaceId(String bucketName) {
        String spaceId = bucketName;
        if(isSpace(bucketName)) {
            spaceId = spaceId.substring(accessKey.length() + 1);
        }
        return spaceId;
    }

    /**
     * Determines if a Sun bucket is a DuraSpace space
     *
     * @param bucketName
     * @return
     */
    protected boolean isSpace(String bucketName) {
        boolean isSpace = false;
        if(bucketName.startsWith(accessKey.toLowerCase())) {
            isSpace = true;
        }
        return isSpace;
    }
}
