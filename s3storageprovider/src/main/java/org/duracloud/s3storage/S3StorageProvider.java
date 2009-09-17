
package org.duracloud.s3storage;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.duracloud.storage.domain.StorageException;
import org.duracloud.storage.provider.StorageProvider;
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

import static org.duracloud.storage.util.StorageProviderUtil.compareChecksum;
import static org.duracloud.storage.util.StorageProviderUtil.loadMetadata;
import static org.duracloud.storage.util.StorageProviderUtil.storeMetadata;
import static org.duracloud.storage.util.StorageProviderUtil.wrapStream;

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

    public S3StorageProvider(String accessKey, String secretKey)
            throws StorageException {
        accessKeyId = accessKey;
        AWSCredentials awsCredentials =
                new AWSCredentials(accessKey, secretKey);

        try {
            s3Service = new RestS3Service(awsCredentials);
        } catch (S3ServiceException e) {
            String err =
                    "Could not create connection to S3 due to error: "
                            + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    public S3StorageProvider(S3Service s3Service, String accessKey) {
        this.accessKeyId = accessKey;
        this.s3Service = s3Service;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getSpaces() throws StorageException {
        log.debug("getSpaces()");
        try {
            S3Bucket[] buckets = s3Service.listAllBuckets();

            List<String> spaces = new ArrayList<String>();
            for (S3Bucket bucket : buckets) {
                String bucketName = bucket.getName();
                if (isSpace(bucketName)) {
                    spaces.add(getSpaceId(bucketName));
                }
            }
            return spaces.iterator();
        } catch (S3ServiceException e) {
            String err =
                    "Could not retrieve list of S3 buckets due to error: "
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
        String bucketName = getBucketName(spaceId);
        String bucketMetadata = bucketName + SPACE_METADATA_SUFFIX;
        List<String> spaceContents = getCompleteSpaceContents(spaceId);
        spaceContents.remove(bucketMetadata);
        return spaceContents.iterator();
    }

    private List<String> getCompleteSpaceContents(String spaceId)
            throws StorageException {
        String bucketName = getBucketName(spaceId);
        try {
            S3Bucket bucket = new S3Bucket(bucketName);
            S3Object[] objects = s3Service.listObjects(bucket);

            List<String> contentItems = new ArrayList<String>();
            for (S3Object object : objects) {
                contentItems.add(object.getKey());
            }
            return contentItems;
        } catch (S3ServiceException e) {
            String err =
                    "Could not get contents of S3 bucket " + bucketName
                            + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void createSpace(String spaceId) throws StorageException {
        if (spaceExists(spaceId)) {
            log.debug("createSpace(" + spaceId + ")");
            String msg = "Error: Space already exists: " + spaceId;
            log.warn(msg);
            throw new StorageException(msg);
        }

        String bucketName = getBucketName(spaceId);
        try {
            // TODO: Convert to using getOrCreateBucket() with JetS3t 0.7.x
            S3Bucket bucket = s3Service.createBucket(bucketName);

            // Add space metadata
            Map<String, String> spaceMetadata = new HashMap<String, String>();
            Date created = bucket.getCreationDate();
            spaceMetadata.put(METADATA_SPACE_CREATED, created.toString());
            setSpaceMetadata(spaceId, spaceMetadata);
        } catch (S3ServiceException e) {
            String err =
                    "Could not create S3 bucket with name " + bucketName
                            + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    private boolean spaceExists(String spaceId) {
        boolean exists = false;
        try {
            exists = s3Service.isBucketAccessible(spaceId);
        } catch (S3ServiceException e) {
        }
        return exists;
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

        String bucketName = getBucketName(spaceId);
        try {
            s3Service.deleteBucket(bucketName);
        } catch (S3ServiceException e) {
            String err =
                    "Could not delete S3 bucket with name " + bucketName
                            + " due to error: " + e.getMessage();
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
        String bucketName = getBucketName(spaceId);
        InputStream is =
                getContent(spaceId, bucketName + SPACE_METADATA_SUFFIX);
        Map<String, String> spaceMetadata = loadMetadata(is);

        try {
            if (!spaceMetadata.containsKey(METADATA_SPACE_CREATED)) {
                S3Bucket bucket = s3Service.getBucket(bucketName);
                Date created = bucket.getCreationDate();
                if (created != null) {
                    spaceMetadata.put(METADATA_SPACE_CREATED, created
                            .toString());
                }
            }

            int count = getCompleteSpaceContents(spaceId).size();
            spaceMetadata.put(METADATA_SPACE_COUNT, String.valueOf(count));

            AccessType access = getSpaceAccess(spaceId);
            spaceMetadata.put(METADATA_SPACE_ACCESS, access.toString());
        } catch (S3ServiceException e) {
            String err =
                    "Could not retrieve metadata from S3 bucket " + bucketName
                            + " due to error: " + e.getMessage();
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

        String bucketName = getBucketName(spaceId);
        ByteArrayInputStream is = storeMetadata(spaceMetadata);
        addContent(spaceId, bucketName + SPACE_METADATA_SUFFIX, "text/xml", is
                .available(), is);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public AccessType getSpaceAccess(String spaceId) throws StorageException {
        log.debug("getSpaceAccess(" + spaceId + ")");

        String bucketName = getBucketName(spaceId);
        AccessType spaceAccess = AccessType.CLOSED;

        try {
            AccessControlList acl = s3Service.getBucketAcl(bucketName);
            Set<GrantAndPermission> grants = acl.getGrants();
            for (GrantAndPermission grant : grants) {
                if (GroupGrantee.ALL_USERS.equals(grant.getGrantee())) {
                    if (Permission.PERMISSION_READ
                            .equals(grant.getPermission())) {
                        spaceAccess = AccessType.OPEN;
                    }
                }
            }
        } catch (S3ServiceException e) {
            String err =
                    "Could not retrieve access control list for S3 bucket "
                            + bucketName + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }

        return spaceAccess;
    }

    /**
     * {@inheritDoc}
     */
    public void setSpaceAccess(String spaceId, AccessType access)
            throws StorageException {
        log.debug("setSpaceAccess(" + spaceId + ")");

        String bucketName = getBucketName(spaceId);
        try {
            AccessControlList bucketAcl = s3Service.getBucketAcl(bucketName);
            if (AccessType.OPEN.equals(access)) {
                // Grants read permissions to all users
                bucketAcl.grantPermission(GroupGrantee.ALL_USERS,
                                          Permission.PERMISSION_READ);
            } else {
                // Revokes all permissions for user groups. This does not remove
                // permissions granted to specific users (such as owner.)
                bucketAcl.revokeAllPermissions(GroupGrantee.ALL_USERS);
                bucketAcl
                        .revokeAllPermissions(GroupGrantee.AUTHENTICATED_USERS);
            }
            S3Bucket bucket = new S3Bucket(bucketName);
            bucket.setAcl(bucketAcl);
            s3Service.putBucketAcl(bucket);

            // Set ACL for all objects contained in the bucket
            S3Object[] objects = s3Service.listObjects(bucket);
            for (S3Object object : objects) {
                object.setAcl(bucketAcl);
                s3Service.putObjectAcl(bucket, object);
            }
        } catch (S3ServiceException e) {
            String err =
                    "Could not set S3 bucket " + bucketName
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

        // Wrap the content to be able to compute a checksum during transfer
        DigestInputStream wrappedContent = wrapStream(content);

        S3Object contentItem = new S3Object(contentId);
        contentItem.setContentType(contentMimeType);
        contentItem.setDataInputStream(wrappedContent);

        if (contentSize > 0) {
            contentItem.setContentLength(contentSize);
        }

        String checksum;
        String bucketName = getBucketName(spaceId);
        try {
            // Set access control to mirror the bucket
            AccessControlList bucketAcl = s3Service.getBucketAcl(bucketName);
            contentItem.setAcl(bucketAcl);

            // Add the object
            s3Service.putObject(bucketName, contentItem);

            // Compare checksum
            checksum =
                    compareChecksum(this, spaceId, contentId, wrappedContent);
        } catch (S3ServiceException e) {
            String err =
                    "Could not add content " + contentId + " with type "
                            + contentMimeType + " and size " + contentSize
                            + " to S3 bucket " + bucketName + " due to error: "
                            + e.getMessage();
            throw new StorageException(err, e);
        }

        // Set default content metadata values
        Map<String, String> contentMetadata = new HashMap<String, String>();
        contentMetadata.put(METADATA_CONTENT_MIMETYPE, contentMimeType);
        setContentMetadata(spaceId, contentId, contentMetadata);

        return checksum;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getContent(String spaceId, String contentId)
            throws StorageException {
        log.debug("getContent(" + spaceId + ", " + contentId + ")");
        String bucketName = getBucketName(spaceId);
        S3Object contentItem = null;
        InputStream content = null;
        try {
            contentItem =
                    s3Service.getObject(new S3Bucket(bucketName), contentId);
            content = contentItem.getDataInputStream();
        } catch (S3ServiceException e) {
            content = null;
            String err =
                    "Could not retrieve content " + contentId
                            + " in S3 bucket " + bucketName + " due to error: "
                            + e.getMessage();
            log.warn(err);
        }
        return content;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteContent(String spaceId, String contentId)
            throws StorageException {
        log.debug("deleteContent(" + spaceId + ", " + contentId + ")");

        String bucketName = getBucketName(spaceId);
        try {
            s3Service.deleteObject(bucketName, contentId);
        } catch (S3ServiceException e) {
            String err =
                    "Could not delete content " + contentId
                            + " from S3 bucket " + bucketName
                            + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
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
        try {
            S3Bucket bucket = new S3Bucket(bucketName);
            S3Object contentItem =
                    s3Service.getObjectDetails(bucket, contentId);
            contentItem.setAcl(s3Service.getObjectAcl(bucket, contentId));
            contentItem.replaceAllMetadata(contentMetadata);

            // Update Content-Type to the new mime type
            if (newMimeType != null && newMimeType != "") {
                contentItem.addMetadata(S3Object.METADATA_HEADER_CONTENT_TYPE,
                                        newMimeType);
            }

            s3Service.updateObjectMetadata(bucketName, contentItem);
        } catch (S3ServiceException e) {
            String err =
                    "Could not update metadata for content " + contentId
                            + " in S3 bucket " + bucketName + " due to error: "
                            + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getContentMetadata(String spaceId,
                                                  String contentId)
            throws StorageException {
        log.debug("getContentMetadata(" + spaceId + ", " + contentId + ")");

        String bucketName = getBucketName(spaceId);

        // Get the content item from S3
        S3Object contentItem = null;
        try {
            contentItem =
                    s3Service.getObjectDetails(new S3Bucket(bucketName),
                                               contentId);
        } catch (S3ServiceException e) {
            String err =
                    "Could not retrieve metadata for content " + contentId
                            + " from S3 bucket " + bucketName
                            + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }

        if (contentItem == null) {
            String err =
                    "No metadata is available for item " + contentId
                            + " in S3 bucket " + bucketName;
            throw new StorageException(err);
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
                contentMetadata
                        .get(S3Object.METADATA_HEADER_LAST_MODIFIED_DATE);
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
