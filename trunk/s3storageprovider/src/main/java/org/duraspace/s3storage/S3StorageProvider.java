package org.duraspace.s3storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.duraspace.storage.StorageException;
import org.duraspace.storage.StorageProvider;
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

/**
 * Provides content storage backed by Amazon's Simple Storage Service.
 *
 * @author Bill Branan
 */
public class S3StorageProvider implements StorageProvider {

    private final Log log = LogFactory.getLog(this.getClass());
    private static final String SPACE_METADATA_SUFFIX = "-space-metadata";

    private String accessKeyId = null;
    private S3Service s3Service = null;

    public S3StorageProvider(String accessKey, String secretKey)
    throws StorageException {
        accessKeyId = accessKey;
        AWSCredentials awsCredentials =
            new AWSCredentials(accessKey, secretKey);

        try {
            s3Service = new RestS3Service(awsCredentials);
        } catch(S3ServiceException e) {
            String err = "Could not create connection to S3 due to error: " +
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
            S3Bucket[] buckets = s3Service.listAllBuckets();

            List<String> spaces = new ArrayList<String>();
            for(S3Bucket bucket : buckets) {
                spaces.add(getSpaceId(bucket.getName()));
            }
            return spaces;
        } catch(S3ServiceException e) {
            String err = "Could not retrieve list of S3 buckets due to error: " +
                         e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getSpaceContents(String spaceId)
    throws StorageException {
        String bucketName = getBucketName(spaceId);
        String bucketMetadata = bucketName+SPACE_METADATA_SUFFIX;
        List<String> spaceContents = getCompleteSpaceContents(spaceId);
        spaceContents.remove(bucketMetadata);
        return spaceContents;
    }

    private List<String> getCompleteSpaceContents(String spaceId)
    throws StorageException {
        String bucketName = getBucketName(spaceId);
        try {
            S3Bucket bucket = s3Service.getBucket(bucketName);
            S3Object[] objects = s3Service.listObjects(bucket);

            List<String> contentItems = new ArrayList<String>();
            for(S3Object object : objects) {
                contentItems.add(object.getKey());
            }
            return contentItems;
        } catch(S3ServiceException e) {
            String err = "Could not get contents of S3 bucket " + bucketName +
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
            // TODO: Convert to using getOrCreateBucket() with JetS3t 0.7.x
            s3Service.createBucket(bucketName);
        } catch(S3ServiceException e) {
            String err = "Could not create S3 bucket with name " + bucketName +
                         " due to error: " + e.getMessage();
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

        String bucketName = getBucketName(spaceId);
        try {
            s3Service.deleteBucket(bucketName);
        } catch(S3ServiceException e) {
            String err = "Could not delete S3 bucket with name " + bucketName +
                         " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Properties getSpaceMetadata(String spaceId)
    throws StorageException {
        // Space metadata is stored as a content item
        String bucketName = getBucketName(spaceId);
        InputStream is = getContent(spaceId, bucketName+SPACE_METADATA_SUFFIX);

        Properties spaceProps = null;
        if(is != null) {
            try {
                spaceProps = new Properties();
                spaceProps.loadFromXML(is);
                is.close();
            } catch(Exception e) {
                String err = "Could not read metadata for space " + spaceId +
                             " due to error: " + e.getMessage();
                throw new StorageException(err, e);
            }
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

        try {
            spaceMetadata.storeToXML(os, "Metadata for " + spaceId);
        } catch (IOException e) {
            String err = "Could not set metadata for space " + spaceId +
                         " due to error: " + e.getMessage();
            throw new StorageException(err);
        }

        String bucketName = getBucketName(spaceId);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        addContent(spaceId,
                   bucketName+SPACE_METADATA_SUFFIX,
                   "text/xml",
                   is.available(),
                   is);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public AccessType getSpaceAccess(String spaceId)
    throws StorageException {
        String bucketName = getBucketName(spaceId);
        AccessType spaceAccess = AccessType.CLOSED;

        try {
            AccessControlList acl = s3Service.getBucketAcl(bucketName);
            Set<GrantAndPermission> grants = acl.getGrants();
            for(GrantAndPermission grant : grants) {
                if(GroupGrantee.ALL_USERS.equals(grant.getGrantee())) {
                    if(Permission.PERMISSION_READ.equals(grant.getPermission())) {
                        spaceAccess = AccessType.OPEN;
                    }
                }
            }
        } catch(S3ServiceException e) {
            String err = "Could not retrieve access control list for S3 bucket " +
                         bucketName + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }

        return spaceAccess;
    }

    /**
     * {@inheritDoc}
     */
    public void setSpaceAccess(String spaceId,
                               AccessType access)
    throws StorageException {
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
                bucketAcl.revokeAllPermissions(GroupGrantee.AUTHENTICATED_USERS);
            }
            S3Bucket bucket = s3Service.getBucket(bucketName);
            bucket.setAcl(bucketAcl);
            s3Service.putBucketAcl(bucket);

            // Set ACL for all objects contained in the bucket
            S3Object[] objects = s3Service.listObjects(bucket);
            for(S3Object object : objects) {
                object.setAcl(bucketAcl);
                s3Service.putObjectAcl(bucket, object);
            }
        } catch(S3ServiceException e) {
            String err = "Could not set S3 bucket " + bucketName + " ACL to access type " +
                         access.toString() + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addContent(String spaceId,
                           String contentId,
                           String contentMimeType,
                           int contentSize,
                           InputStream content)
    throws StorageException {
        S3Object contentItem = new S3Object(contentId);
        contentItem.setContentType(contentMimeType);
        contentItem.setDataInputStream(content);

        if(contentSize > 0) {
            contentItem.setContentLength(contentSize);
        }

        String bucketName = getBucketName(spaceId);
        try {
            AccessControlList bucketAcl = s3Service.getBucketAcl(bucketName);
            contentItem.setAcl(bucketAcl);
            s3Service.putObject(bucketName, contentItem);
        } catch(S3ServiceException e) {
            String err = "Could not add content " + contentId + " with type " +
                         contentMimeType + " and size " + contentSize + " to S3 bucket "
                         + bucketName + " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getContent(String spaceId,
                                  String contentId)
    throws StorageException {
        String bucketName = getBucketName(spaceId);
        S3Object contentItem = null;
        InputStream content = null;
        try {
            contentItem =
                s3Service.getObject(s3Service.getBucket(bucketName), contentId);
            content = contentItem.getDataInputStream();
        } catch(S3ServiceException e) {
            content = null;
            String err = "Could not retrieve content " + contentId +
                         " in S3 bucket " + bucketName + " due to error: " +
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
            s3Service.deleteObject(bucketName, contentId);
        } catch(S3ServiceException e) {
            String err = "Could not delete content " + contentId +
                         " from S3 bucket " + bucketName +
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
        // Convert from Properties to a Map
        HashMap<String, String> metadataMap = new HashMap<String, String>();
        Enumeration<?> metadataKeys = contentMetadata.propertyNames();
        while(metadataKeys.hasMoreElements()) {
            String key = (String)metadataKeys.nextElement();
            String value = contentMetadata.getProperty(key);
            if(value != null) {
                metadataMap.put(key, value);
            }
        }

        // Get the object and replace its metadata
        String bucketName = getBucketName(spaceId);
        try {
            S3Object contentItem = s3Service.
                getObjectDetails(s3Service.getBucket(bucketName), contentId);
            contentItem.addAllMetadata(metadataMap);
            s3Service.updateObjectMetadata(bucketName, contentItem);
        } catch(S3ServiceException e) {
            String err = "Could not update metadata for content " +
                         contentId + " in S3 bucket " + bucketName +
                         " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Properties getContentMetadata(String spaceId,
                                         String contentId)
    throws StorageException {
        String bucketName = getBucketName(spaceId);

        // Get the content item from S3
        S3Object contentItem = null;
        try {
            contentItem = s3Service.
                getObjectDetails(s3Service.getBucket(bucketName), contentId);
        } catch(S3ServiceException e) {
            String err = "Could not retrieve metadata for content " +
                         contentId + " from S3 bucket " + bucketName +
                         " due to error: " + e.getMessage();
            throw new StorageException(err, e);
        }

        if(contentItem == null) {
            String err = "No metadata is available for item " + contentId +
                         " in S3 bucket " + bucketName;
            throw new StorageException(err);
        }

        // Convert from Map to Properties
        Map<String, String> metadataMap = contentItem.getMetadataMap();
        Properties metadata = new Properties();
        Iterator<String> metadataKeys = metadataMap.keySet().iterator();
        while(metadataKeys.hasNext()) {
            String key = metadataKeys.next();
            metadata.put(key, metadataMap.get(key));
        }
        return metadata;
    }

    /**
     * Converts a provided space ID into a valid S3 bucket name.
     *
     * @param spaceId
     * @return
     */
    protected String getBucketName(String spaceId) {
        String bucketName = accessKeyId + "." + spaceId;
        bucketName = bucketName.toLowerCase();
        bucketName = bucketName.replaceAll("[^a-z0-9-.]", "-");
        bucketName = bucketName.replaceAll("-[.]", "-");
        bucketName = bucketName.replaceAll("[.]-", ".");
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
        if(spaceId.startsWith(accessKeyId.toLowerCase())) {
            spaceId = spaceId.substring(accessKeyId.length() + 1);
        }
        return spaceId;
    }

}
