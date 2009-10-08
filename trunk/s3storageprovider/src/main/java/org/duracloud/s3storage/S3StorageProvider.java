package org.duracloud.s3storage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.storage.error.StorageException;
import static org.duracloud.storage.error.StorageException.RETRY;
import static org.duracloud.storage.error.StorageException.NO_RETRY;
import org.duracloud.storage.provider.StorageProvider;
import static org.duracloud.storage.util.StorageProviderUtil.compareChecksum;
import static org.duracloud.storage.util.StorageProviderUtil.loadMetadata;
import static org.duracloud.storage.util.StorageProviderUtil.storeMetadata;
import static org.duracloud.storage.util.StorageProviderUtil.wrapStream;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GrantAndPermission;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides content storage backed by Amazon's Simple Storage Service.
 *
 * @author Bill Branan
 */
public class S3StorageProvider
        implements StorageProvider {

    private final Log log = LogFactory.getLog(this.getClass());

    private String accessKeyId = null;
    private S3Service s3Service = null;
    
    public S3StorageProvider(String accessKey, String secretKey) {
        accessKeyId = accessKey;
        AWSCredentials awsCredentials = new AWSCredentials(accessKey, secretKey);
        try {
            s3Service = new RestS3Service(awsCredentials);
        } catch (S3ServiceException e) {
            String err = "Could not create connection to S3 due to error: "
                    + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    public S3StorageProvider(S3Service s3Service, String accessKey) {
        this.accessKeyId = accessKey;
        this.s3Service = s3Service;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getSpaces() {
        log.debug("getSpaces()");

        List<String> spaces = new ArrayList<String>();
        S3Bucket[] buckets = listAllBuckets();
        for (S3Bucket bucket : buckets) {
            String bucketName = bucket.getName();
            if (isSpace(bucketName)) {
                spaces.add(getSpaceId(bucketName));
            }
        }

        return spaces.iterator();
    }

    private S3Bucket[] listAllBuckets() {
        try {
            return s3Service.listAllBuckets();
        }
        catch (S3ServiceException e) {
            String err = "Could not retrieve list of S3 buckets due to error: "
                    + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getSpaceContents(String spaceId) {
        log.debug("getSpaceContents(" + spaceId + ")");

        throwIfSpaceNotExist(spaceId);

        String bucketName = getBucketName(spaceId);
        String bucketMetadata = bucketName + SPACE_METADATA_SUFFIX;
        List<String> spaceContents = getCompleteSpaceContents(spaceId);
        spaceContents.remove(bucketMetadata);

        return spaceContents.iterator();
    }

    private List<String> getCompleteSpaceContents(String spaceId) {
        List<String> contentItems = new ArrayList<String>();

        S3Object[] objects = listObjects(spaceId);
        for (S3Object object : objects) {
            contentItems.add(object.getKey());
        }
        return contentItems;
    }

    private S3Object[] listObjects(String spaceId) {
        String bucketName = getBucketName(spaceId);
        S3Bucket bucket = new S3Bucket(bucketName);
        try {
            return s3Service.listObjects(bucket);
        } catch (S3ServiceException e) {
            String err = "Could not get contents of S3 bucket " + bucketName
                    + " due to error: " + e.getMessage();
            throw new StorageException(err, e, RETRY);
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
        String bucketName = getBucketName(spaceId);
        boolean exists = false;
        try {
            exists = s3Service.isBucketAccessible(bucketName);
        } catch (S3ServiceException e) {
        }
        return exists;
    }

    /**
     * {@inheritDoc}
     */
    public void createSpace(String spaceId) {
        log.debug("createSpace(" + spaceId + ")");
        throwIfSpaceExists(spaceId);

        S3Bucket bucket = createBucket(spaceId);

        // Add space metadata
        Map<String, String> spaceMetadata = new HashMap<String, String>();
        Date created = bucket.getCreationDate();
        spaceMetadata.put(METADATA_SPACE_CREATED, created.toString());
        setSpaceMetadata(spaceId, spaceMetadata);
    }

    private S3Bucket createBucket(String spaceId) {
        String bucketName = getBucketName(spaceId);
        try {
            // TODO: Convert to using getOrCreateBucket() with JetS3t 0.7.x
            // or not :)
            return s3Service.createBucket(bucketName);
        } catch (S3ServiceException e) {
            String err = "Could not create S3 bucket with name " + bucketName
                    + " due to error: " + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteSpace(String spaceId) {
        log.debug("deleteSpace(" + spaceId + ")");
        throwIfSpaceNotExist(spaceId);

        List<String> contents = getCompleteSpaceContents(spaceId);
        for (String contentItem : contents) {
            deleteContent(spaceId, contentItem);
        }
        deleteBucket(spaceId);
    }

    private void deleteBucket(String spaceId) {
        String bucketName = getBucketName(spaceId);
        try {
            s3Service.deleteBucket(bucketName);
        } catch (S3ServiceException e) {
            String err = "Could not delete S3 bucket with name " + bucketName
                    + " due to error: " + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getSpaceMetadata(String spaceId) {
        log.debug("getSpaceMetadata(" + spaceId + ")");

        throwIfSpaceNotExist(spaceId);

        // Space metadata is stored as a content item
        String bucketName = getBucketName(spaceId);
        InputStream is = getContent(spaceId, bucketName + SPACE_METADATA_SUFFIX);
        Map<String, String> spaceMetadata = loadMetadata(is);

        if (!spaceMetadata.containsKey(METADATA_SPACE_CREATED)) {
            S3Bucket bucket = getBucket(bucketName);
            Date created = bucket.getCreationDate();
            if (created != null) {
                spaceMetadata.put(METADATA_SPACE_CREATED, created.toString());
            }
        }

        int count = getCompleteSpaceContents(spaceId).size();
        spaceMetadata.put(METADATA_SPACE_COUNT, String.valueOf(count));

        AccessType access = getSpaceAccess(spaceId);
        spaceMetadata.put(METADATA_SPACE_ACCESS, access.toString());

        return spaceMetadata;
    }

    private S3Bucket getBucket(String bucketName) {
        try {
            return s3Service.getBucket(bucketName);
        } catch (S3ServiceException e) {
            String err = "Could not retrieve metadata from S3 bucket "
                    + bucketName + " due to error: " + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setSpaceMetadata(String spaceId,
                                 Map<String, String> spaceMetadata) {
        log.debug("setSpaceMetadata(" + spaceId + ")");

        throwIfSpaceNotExist(spaceId);

        String bucketName = getBucketName(spaceId);
        ByteArrayInputStream is = storeMetadata(spaceMetadata);
        addContent(spaceId, bucketName + SPACE_METADATA_SUFFIX, "text/xml",
                   is.available(), is);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public AccessType getSpaceAccess(String spaceId) {
        log.debug("getSpaceAccess(" + spaceId + ")");

        throwIfSpaceNotExist(spaceId);

        AccessType spaceAccess = AccessType.CLOSED;
        AccessControlList acl = getBucketAcl(spaceId);
        Set<GrantAndPermission> grants = acl.getGrants();
        for (GrantAndPermission grant : grants) {
            if (GroupGrantee.ALL_USERS.equals(grant.getGrantee())) {
                if (Permission.PERMISSION_READ
                        .equals(grant.getPermission())) {
                    spaceAccess = AccessType.OPEN;
                }
            }
        }
        return spaceAccess;
    }

    private AccessControlList getBucketAcl(String spaceId) {
        String bucketName = getBucketName(spaceId);
        try {
            return s3Service.getBucketAcl(bucketName);
        } catch (S3ServiceException e) {
            String err = "Could not retrieve access control list for S3 bucket "
                    + bucketName + " due to error: " + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setSpaceAccess(String spaceId, AccessType access) {
        log.debug("setSpaceAccess(" + spaceId + ")");

        throwIfSpaceNotExist(spaceId);

        AccessControlList bucketAcl = getBucketAcl(spaceId);
        if (AccessType.OPEN.equals(access)) {
            // Grants read permissions to all users
            bucketAcl.grantPermission(GroupGrantee.ALL_USERS,
                                      Permission.PERMISSION_READ);
        } else {
            // Revokes all permissions for user groups. This does not remove
            // permissions granted to specific users (such as owner.)
            bucketAcl.revokeAllPermissions(GroupGrantee.ALL_USERS);
            bucketAcl.revokeAllPermissions(GroupGrantee.AUTHENTICATED_USERS);
        }

        String bucketName = getBucketName(spaceId);
        S3Bucket bucket = new S3Bucket(bucketName);
        bucket.setAcl(bucketAcl);
        putBucketAcl(bucket);

        // Set ACL for all objects contained in the bucket
        S3Object[] objects = listObjects(spaceId);
        for (S3Object object : objects) {
            object.setAcl(bucketAcl);
            putObjectAcl(bucket, object);
        }

    }

    private void putBucketAcl(S3Bucket bucket) {
        try {
            s3Service.putBucketAcl(bucket);
        } catch (S3ServiceException e) {
            String err = "Could not set S3 bucket " + bucket.getName() + " ACL "
                    + " due to error: " + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    private void putObjectAcl(S3Bucket bucket,
                              S3Object object) {
        try {
            s3Service.putObjectAcl(bucket, object);
        } catch (S3ServiceException e) {
            String err = "Could not set S3 object " + bucket.getName() +
                    ":" + object.getKey()
                    + " ACL due to error: " + e.getMessage();
            throw new StorageException(err, e, RETRY);
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

        // Wrap the content to be able to compute a checksum during transfer
        DigestInputStream wrappedContent = wrapStream(content);

        S3Object contentItem = new S3Object(contentId);
        contentItem.setContentType(contentMimeType);
        contentItem.setDataInputStream(wrappedContent);

        if (contentSize > 0) {
            contentItem.setContentLength(contentSize);
        }

        // Set access control to mirror the bucket
        AccessControlList bucketAcl = getBucketAcl(spaceId);
        contentItem.setAcl(bucketAcl);

        // Add the object
        putObject(contentItem, spaceId);

        // Compare checksum
        String checksum = compareChecksum(this, spaceId, contentId, wrappedContent);

        // Set default content metadata values
        Map<String, String> contentMetadata = new HashMap<String, String>();
        contentMetadata.put(METADATA_CONTENT_MIMETYPE, contentMimeType);
        setContentMetadata(spaceId, contentId, contentMetadata);

        return checksum;
    }

    private void putObject(S3Object contentItem,
                           String spaceId) {
        String bucketName = getBucketName(spaceId);
        try {
            s3Service.putObject(bucketName, contentItem);
        } catch (S3ServiceException e) {
            String err = "Could not add content " + contentItem.getKey()
                    + " with type " + contentItem.getContentType()
                    + " and size " + contentItem.getContentLength()
                    + " to S3 bucket " + bucketName + " due to error: "
                    + e.getMessage();
            throw new StorageException(err, e, NO_RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getContent(String spaceId, String contentId) {
        log.debug("getContent(" + spaceId + ", " + contentId + ")");

        throwIfSpaceNotExist(spaceId);

        String bucketName = getBucketName(spaceId);        
        S3Object contentItem = getObject(contentId, bucketName);
        InputStream content = getDataInputStream(contentItem);

        return content;
    }

    private S3Object getObject(String contentId,
                               String bucketName) {
        try {
            return s3Service.getObject(new S3Bucket(bucketName), contentId);
        } catch (S3ServiceException e) {
            String err = "Could not retrieve content " + contentId
                    + " in S3 bucket " + bucketName + " due to error: "
                    + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    private InputStream getDataInputStream(S3Object contentItem) {
        try {
            return contentItem.getDataInputStream();
        } catch (S3ServiceException e) {
            String err = "Could not retrieve content " + contentItem.getKey()
                    + " in S3 bucket " + contentItem.getBucketName()
                    + " due to error: " + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteContent(String spaceId, String contentId) {
        log.debug("deleteContent(" + spaceId + ", " + contentId + ")");

        throwIfSpaceNotExist(spaceId);

        String bucketName = getBucketName(spaceId);
        try {
            s3Service.deleteObject(bucketName, contentId);
        } catch (S3ServiceException e) {
            String err = "Could not delete content " + contentId
                    + " from S3 bucket " + bucketName
                    + " due to error: " + e.getMessage();
            throw new StorageException(err, e, RETRY);
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
        contentMetadata.remove(S3Object.METADATA_HEADER_CONTENT_LENGTH);
        contentMetadata.remove(S3Object.METADATA_HEADER_LAST_MODIFIED_DATE);
        contentMetadata.remove(S3Object.METADATA_HEADER_DATE);
        contentMetadata.remove(S3Object.METADATA_HEADER_ETAG);

        // Remove mimetype to set later
        String newMimeType = contentMetadata.remove(METADATA_CONTENT_MIMETYPE);

        // Get the object and replace its metadata
        String bucketName = getBucketName(spaceId);
        S3Bucket bucket = new S3Bucket(bucketName);
        S3Object contentItem = getObjectDetails(bucket, contentId, NO_RETRY);
        contentItem.setAcl(getObjectAcl(contentId, bucket));
        contentItem.replaceAllMetadata(contentMetadata);

        // Update Content-Type to the new mime type
        if (newMimeType != null && newMimeType != "") {
            contentItem.addMetadata(S3Object.METADATA_HEADER_CONTENT_TYPE,
                                    newMimeType);
        }

        updateObjectMetadata(bucketName, contentItem);
    }

    private S3Object getObjectDetails(S3Bucket bucket, String contentId,
                                      boolean retry) {
        try {
            return s3Service.getObjectDetails(bucket, contentId);
        } catch (S3ServiceException e) {
            String err = "Could not get details for content " + contentId
                    + " in S3 bucket " + bucket.getName() + " due to error: "
                    + e.getMessage();
            throw new StorageException(err, e, retry);
        }
    }

    private AccessControlList getObjectAcl(String contentId,
                                           S3Bucket bucket) {
        try {
            return s3Service.getObjectAcl(bucket, contentId);
        } catch (S3ServiceException e) {
            String err = "Could not get ACL for content " + contentId
                    + " in S3 bucket " + bucket.getName() + " due to error: "
                    + e.getMessage();
            throw new StorageException(err, e, NO_RETRY);
        }
    }

    private void updateObjectMetadata(String bucketName,
                                      S3Object contentItem) {
        try {
            s3Service.updateObjectMetadata(bucketName, contentItem);
        } catch (S3ServiceException e) {
            String err = "Could not update metadata for content "
                    + contentItem.getKey() + " in S3 bucket " + bucketName
                    + " due to error: " + e.getMessage();
            throw new StorageException(err, e, NO_RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getContentMetadata(String spaceId,
                                                  String contentId) {
        log.debug("getContentMetadata(" + spaceId + ", " + contentId + ")");

        throwIfSpaceNotExist(spaceId);

        // Get the content item from S3
        String bucketName = getBucketName(spaceId);
        S3Object contentItem = getObjectDetails(new S3Bucket(bucketName),
                                                contentId, RETRY);

        if (contentItem == null) {
            String err = "No metadata is available for item " + contentId
                    + " in S3 bucket " + bucketName;
            throw new StorageException(err, NO_RETRY);
        }

        // Load the metadata Map
        Map<String, String> contentMetadata = new HashMap<String, String>();
        Map contentItemMetadata = contentItem.getMetadataMap();
        Iterator metaIterator = contentItemMetadata.keySet().iterator();
        while (metaIterator.hasNext()) {
            String metaName = metaIterator.next().toString();
            Object metaValueObj = contentItemMetadata.get(metaName);
            String metaValue;
            if (metaValueObj instanceof Date) {
                metaValue = RFC822_DATE_FORMAT.format(metaValueObj);
            } else {
                metaValue = metaValueObj.toString();
            }
            contentMetadata.put(metaName, metaValue);
        }

        // Set MIMETYPE
        String contentType =
                contentMetadata.get(S3Object.METADATA_HEADER_CONTENT_TYPE);
        if (contentType != null) {
            contentMetadata.put(METADATA_CONTENT_MIMETYPE, contentType);
        }

        // Set SIZE
        String contentLength =
                contentMetadata.get(S3Object.METADATA_HEADER_CONTENT_LENGTH);
        if (contentLength != null) {
            contentMetadata.put(METADATA_CONTENT_SIZE, contentLength);
        }

        // Set CHECKSUM
        String checksum = contentMetadata.get(S3Object.METADATA_HEADER_ETAG);
        if (checksum != null) {
            if (checksum.indexOf("\"") == 0
                    && checksum.lastIndexOf("\"") == checksum.length() - 1) {
                // Remove wrapping quotes
                checksum = checksum.substring(1, checksum.length() - 1);
            }
            contentMetadata.put(METADATA_CONTENT_CHECKSUM, checksum);
        }

        // Set MODIFIED
        String modified =
                contentMetadata.get(S3Object.METADATA_HEADER_LAST_MODIFIED_DATE);
        if (modified != null) {
            contentMetadata.put(METADATA_CONTENT_MODIFIED, modified);
        }

        return contentMetadata;
    }

    /**
     * Converts a provided space ID into a valid and unique S3 bucket name.
     *
     * @param spaceId
     * @return
     */
    protected String getBucketName(String spaceId) {
        String bucketName = accessKeyId + "." + spaceId;
        bucketName = bucketName.toLowerCase();
        bucketName = bucketName.replaceAll("[^a-z0-9-.]", "-");

        // Remove duplicate separators (. and -)
        while (bucketName.contains("--") || bucketName.contains("..")
                || bucketName.contains("-.") || bucketName.contains(".-")) {
            bucketName = bucketName.replaceAll("[-]+", "-");
            bucketName = bucketName.replaceAll("[.]+", ".");
            bucketName = bucketName.replaceAll("-[.]", "-");
            bucketName = bucketName.replaceAll("[.]-", ".");
        }

        if (bucketName.length() > 63) {
            bucketName = bucketName.substring(0, 63);
        }
        while (bucketName.endsWith("-") || bucketName.endsWith(".")) {
            bucketName = bucketName.substring(0, bucketName.length() - 1);
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
        if (isSpace(bucketName)) {
            spaceId = spaceId.substring(accessKeyId.length() + 1);
        }
        return spaceId;
    }

    /**
     * Determines if an S3 bucket is a DuraSpace space
     *
     * @param bucketName
     * @return
     */
    protected boolean isSpace(String bucketName) {
        boolean isSpace = false;
        if (bucketName.startsWith(accessKeyId.toLowerCase())) {
            isSpace = true;
        }
        return isSpace;
    }
}
