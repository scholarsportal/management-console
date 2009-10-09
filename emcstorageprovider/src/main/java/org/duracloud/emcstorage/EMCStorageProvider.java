
package org.duracloud.emcstorage;

import com.emc.esu.api.*;
import com.emc.esu.api.rest.EsuRestApi;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.duracloud.common.util.ChecksumUtil;
import org.duracloud.storage.error.StorageException;
import static org.duracloud.storage.error.StorageException.NO_RETRY;
import static org.duracloud.storage.error.StorageException.RETRY;
import org.duracloud.storage.provider.StorageProvider;
import org.duracloud.storage.util.StorageProviderUtil;
import static org.duracloud.storage.util.StorageProviderUtil.contains;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides content storage backed by EMC's Storage Utility.
 *
 * @author Andrew Woods
 */
public class EMCStorageProvider
        implements StorageProvider {

    private final Logger log = Logger.getLogger(EMCStorageProvider.class);

    private static final String SPACE_ROOT_TAG_NAME = "emc-space-root-tag";

    private static final String EMC_CONTENT_SIZE = "size";

    private static final String EMC_CONTENT_MODIFIED_DATE = "mtime";

    private static final String EMC_CREATION_DATE_NAME = "ctime";

    protected static final String ESU_HOST = "accesspoint.emccis.com";

    protected static final int ESU_PORT = 80;

    private EsuApi emcService = null;

    public EMCStorageProvider(String uid, String sharedSecret) {
        emcService = new EsuRestApi(ESU_HOST, ESU_PORT, uid, sharedSecret);
    }

    public EMCStorageProvider(EsuApi esuApi) {
        emcService = esuApi;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getSpaces() {
        log.debug("getSpaces()");

        List<String> spaces = new ArrayList<String>();
        List<Identifier> spaceObjects = getSpaceObjects();
        for (Identifier objId : spaceObjects) {
            spaces.add(getSpaceNameForSpaceObject(objId));
        }

        if (log.isDebugEnabled()) {
            log.debug("Spaces found:");
            for (String space : spaces) {
                log.debug("\t-> " + space);
            }
        }
        return spaces.iterator();
    }

    private List<Identifier> getSpaceObjects() {
        try {
            return emcService.listObjects(spaceRootTag());
        } catch (EsuException e) {
            String err = "Unable to find any spaces: " + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    private String getSpaceNameForSpaceObject(Identifier objId) {
        MetadataTags tags = new MetadataTags();
        tags.addTag(spaceRootTag());

        try {
            // There should only be one element in the filtered userMetadata.
            MetadataList userMetadata = emcService.getUserMetadata(objId, tags);
            return userMetadata.iterator().next().getValue();

        } catch (Exception e) {
            String err = "Unable to find spaceRootTag for space: "
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

        List<String> contentNames = new ArrayList<String>();
        List<Identifier> spaceContents = getCompleteSpaceContents(spaceId);
        for (Identifier objId : spaceContents) {
            contentNames.add(getContentNameForContentObject(objId, spaceId));
        }

        return contentNames.iterator();
    }

    private List<Identifier> getCompleteSpaceContents(String spaceId) {
        try {
            return emcService.listObjects(currentSpaceTag(spaceId));

        } catch (EsuException e) {
            String err = "Unable to list objs with current space tag: "
                    + spaceId + ", due to error: " + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    private String getContentNameForContentObject(Identifier objId,
                                                  String spaceId) {
        MetadataTags tags = new MetadataTags();
        String name = null;
        MetadataList userMetadata = emcService.getUserMetadata(objId, tags);
        for (Metadata md : userMetadata) {
            if (spaceId.equals(md.getName())) {
                name = md.getValue();
                break;
            }
        }

        log.debug("content name found for objId: " + name + ", for: " + objId);
        return name;
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
        try {
            return contains(getSpaces(), spaceId);            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void createSpace(String spaceId) {
        log.debug("createSpace(" + spaceId + ")");
        throwIfSpaceExists(spaceId);

        MetadataList metadataList = createRequiredRootMetadata(spaceId);
        Identifier objId = createSpaceObject(metadataList);

        log.debug("\t...space created with id: " + objId);
    }

    private MetadataList createRequiredRootMetadata(String spaceId) {
        MetadataList metadataList = new MetadataList();
        metadataList.addMetadata(new Metadata(SPACE_ROOT_TAG_NAME,
                                              spaceId,
                                              true));
        return metadataList;
    }

    private Identifier createSpaceObject(MetadataList metadataList) {
        Acl acl = null;
        byte[] data = null;
        String mimeType = null;

        try {
            // This object only serves the purpose of representing the
            //  existence of a 'space' with id: spaceId.
            return emcService.createObject(acl, metadataList, data, mimeType);
        } catch (EsuException e) {
            String err = "Could not create EMC space with spaceId "
                    + metadataList.getMetadata(SPACE_ROOT_TAG_NAME).getValue()
                    + ", due to error: " + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteSpace(String spaceId) {
        log.debug("Deleting space: " + spaceId);
        throwIfSpaceNotExist(spaceId);

        deleteSpaceContents(spaceId);
        deleteObject(getRootId(spaceId));
    }

    private void deleteSpaceContents(String spaceId) {
        List<Identifier> contentIds = getCompleteSpaceContents(spaceId);
        for (Identifier objId : contentIds) {
            deleteObject(objId);
        }
    }

    private void deleteObject(Identifier objId) {
        log.debug("deleteObject(" + objId + ")");

        try {
            emcService.deleteObject(objId);

        } catch (Exception e) {
            String err = "Unable to delete object: " + objId
                    + ", due to error: " + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    protected Identifier getRootId(String spaceId) {
        Identifier rootId = null;
        List<Identifier> spaceObjects = getSpaceObjects();
        for (Identifier objId : spaceObjects) {
            if (spaceId.equals(getSpaceNameForSpaceObject(objId))) {
                rootId = objId;
                break;
            }
        }

        if (rootId == null) {
            String err = "ERROR: Unable to find rootId for space: " + spaceId;
            log.debug(err);
            throw new StorageException(err, RETRY);
        }

        log.debug("Found rootId [" + rootId + "] for spaceId [" + spaceId + "]");
        return rootId;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getSpaceMetadata(String spaceId) {
        log.debug("getSpaceMetadata(" + spaceId + ")");

        throwIfSpaceNotExist(spaceId);

        ObjectId rootObjId = (ObjectId) getRootId(spaceId);
        Map<String, String> spaceMetadata = getExistingUserMetadata(rootObjId);

        // Over-write managed metadata.
        spaceMetadata.put(METADATA_SPACE_CREATED, getCreationDate(rootObjId));
        spaceMetadata.put(METADATA_SPACE_COUNT, getContentObjCount(spaceId));
        spaceMetadata.put(METADATA_SPACE_ACCESS, doGetSpaceAccess(rootObjId)
                .toString());

        return spaceMetadata;
    }

    private String getCreationDate(Identifier id) {
        String creationDate = "unknown-creation-date";

        MetadataList sysMetadata = getSystemMetadata(id);
        for (Metadata sysMd : sysMetadata) {
            if (EMC_CREATION_DATE_NAME.equals(sysMd.getName())) {
                creationDate = sysMd.getValue();
                break;
            }
        }
        return creationDate;
    }

    private String getContentObjCount(String spaceId) {
        List<Identifier> contentIds = new ArrayList<Identifier>();
        try {
            contentIds = getCompleteSpaceContents(spaceId);
        } catch (Exception e) {
            log.info("Obj-count not found: " + spaceId + ", " + e.getMessage());
        }
        return String.valueOf(contentIds.size());
    }

    /**
     * {@inheritDoc}
     */
    public void setSpaceMetadata(String spaceId,
                                 Map<String, String> spaceMetadata) {
        log.debug("setSpaceMetadata(" + spaceId + ")");

        throwIfSpaceNotExist(spaceId);

        // Remove volatile metadata.
        Identifier rootObjId = getRootId(spaceId);
        MetadataTags existingTags = listUserMetadataTags(rootObjId);
        MetadataTags disposableTags = getSpaceTagsToRemove(existingTags);
        if (disposableTags.count() > 0) {
            deleteUserMetadata(rootObjId, disposableTags);
        }

        // Start with required metadata.
        MetadataList metadatas = createRequiredRootMetadata(spaceId);

        // Do not overwrite space root tag
        spaceMetadata.remove(SPACE_ROOT_TAG_NAME);

        // Start adding arg user metadata.
        final boolean isIndexed = false;
        Set<String> keys = spaceMetadata.keySet();
        for (String key : keys) {
            String val = spaceMetadata.get(key);
            metadatas.addMetadata(new Metadata(key, val, isIndexed));
        }

        // The actual setting.
        setUserMetadata(rootObjId, metadatas);
    }

    private MetadataTags listUserMetadataTags(Identifier objId) {
        try {
            return emcService.listUserMetadataTags(objId);
        } catch (Exception e) {
            String err = "Error listing user metadata for :" + objId + ", "
                    + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    private MetadataTags getSpaceTagsToRemove(MetadataTags existingTags) {
        MetadataTags tags = new MetadataTags();
        for (MetadataTag tag : existingTags) {
            String tagName = tag.getName();
            if (!tagName.equals(SPACE_ROOT_TAG_NAME)) {
                tags.addTag(tag);
            }
        }
        return tags;
    }

    private void deleteUserMetadata(Identifier objId,
                                    MetadataTags disposableTags) {
        try {
            emcService.deleteUserMetadata(objId, disposableTags);
        } catch (Exception e) {
            String err = "Error deleting user metadata for :" + objId + ", "
                    + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    private void setUserMetadata(Identifier rootObjId, MetadataList metadatas) {
        try {
            emcService.setUserMetadata(rootObjId, metadatas);
        } catch (Exception e) {
            String err = "Error setting user metadata: " + rootObjId + ", "
                    + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public AccessType getSpaceAccess(String spaceId) {
        log.debug("getSpaceAccess(" + spaceId + ")");
        throwIfSpaceNotExist(spaceId);

        return doGetSpaceAccess(getRootId(spaceId));
    }

    private AccessType doGetSpaceAccess(Identifier spaceObjId) {
        AccessType spaceAccess = AccessType.CLOSED;
        Acl acl = getAcl(spaceObjId);
        for (Grant grant : acl) {
            if (Grantee.OTHER.equals(grant.getGrantee())) {
                if (!Permission.NONE.equals(grant.getPermission())) {
                    spaceAccess = AccessType.OPEN;
                }
            }
        }
        return spaceAccess;
    }

    private Acl getAcl(Identifier objId) {
        try {
            return emcService.getAcl(objId);
        } catch (Exception e) {
            String err = "Error finding Acl: " + objId + ", " + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setSpaceAccess(String spaceId, AccessType access) {
        log.debug("setSpaceAccess(" + spaceId + ")");

        throwIfSpaceNotExist(spaceId);

        // Default is 'closed'.
        String permission = Permission.NONE;
        if (AccessType.OPEN.equals(access)) {
            permission = Permission.READ;
        }

        Identifier rootObjId = getRootId(spaceId);
        Acl newAcl = new Acl();
        Acl oldAcl = getAcl(rootObjId);
        for (Grant grant : oldAcl) {
            Grant g = grant;
            if (isGroup(grant)) {
                g = new Grant(grant.getGrantee(), permission);
            }
            newAcl.addGrant(g);
        }

        // Set ACL for root.
        setObjectAcl(rootObjId, newAcl);

        // Set ACL for all objects contained in the space.
        List<Identifier> spaceContents = getCompleteSpaceContents(spaceId);
        for (Identifier id : spaceContents) {
            setObjectAcl(id, newAcl);
        }
    }

    private void setObjectAcl(Identifier objId, Acl newAcl) {
        try {
            emcService.setAcl(objId, newAcl);
        } catch (Exception e) {
            String err = "Error setting acl: " + objId + ", due to: "
                    + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    private boolean isGroup(Grant grant) {
        return Grantee.GRANT_TYPE.GROUP.equals(grant.getGrantee().getType());
    }

    /**
     * {@inheritDoc}
     */
    public String addContent(String spaceId,
                             String contentId,
                             String mimeType,
                             long contentSize,
                             InputStream content) {
        try {
            return doAddContent(spaceId, contentId, mimeType, contentSize, content);
        } catch (StorageException e) {
            throw new StorageException(e, NO_RETRY);
        }
    }

    public String doAddContent(String spaceId,
                               String contentId,
                               String mimeType,
                               long contentSize,
                               InputStream content) {
        log.debug("addContent(" + spaceId + ", " + contentId + ", "
                + mimeType + ", " + contentSize + ")");

        throwIfSpaceNotExist(spaceId);

        // Set access control to mirror the bucket
        Acl acl = getAcl(getRootId(spaceId));

        MetadataList metadataList =
                createRequiredContentMetadata(spaceId, contentId, mimeType);

        // Determine if object already exists.
        ObjectId objId = null;
        try {
            objId = getContentObjId(spaceId, contentId);
        } catch (Exception e) {
            // do nothing
        }

        // Wrap the content to be able to compute a checksum during transfer
        DigestInputStream wrappedContent = StorageProviderUtil.wrapStream(content);

        UploadHelper helper = new UploadHelper(emcService);
        boolean closeStream = true;

        // Add new object.
        if (objId == null) {
            helper.createObject(wrappedContent, acl, metadataList, closeStream);
        }
        // Update existing object.
        else {
            helper.updateObject(objId,
                                wrappedContent,
                                acl,
                                metadataList,
                                closeStream);
        }

        // Compare checksum
        return StorageProviderUtil.compareChecksum(this,
                                                   spaceId,
                                                   contentId,
                                                   wrappedContent);
    }

    private MetadataList createRequiredContentMetadata(String spaceId,
                                                       String contentId) {
        return createRequiredContentMetadata(spaceId, contentId, null);
    }

    private MetadataList createRequiredContentMetadata(String spaceId,
                                                       String contentId,
                                                       String mimeType) {
        boolean isIndexed = true;
        MetadataList metadataList = new MetadataList();
        metadataList.addMetadata(new Metadata(spaceId, contentId, isIndexed));

        if (mimeType != null) {
            metadataList.addMetadata(new Metadata(METADATA_CONTENT_MIMETYPE,
                                                  mimeType,
                                                  isIndexed));
        } else {
            metadataList.addMetadata(new Metadata(METADATA_CONTENT_MIMETYPE,
                                                  DEFAULT_MIMETYPE,
                                                  isIndexed));
        }
        return metadataList;
    }

    protected ObjectId getContentObjId(String spaceId, String contentId) {
        // FIXME: should not have to loop through all content to find contentId.
        //        EsuApi.queryObjects(String xquery) ?

        ObjectId contentObjId = null;
        List<Identifier> spaceContents = getCompleteSpaceContents(spaceId);
        for (Identifier objId : spaceContents) {
            if (contentId
                    .equals(getContentNameForContentObject(objId, spaceId))) {
                contentObjId = (ObjectId) objId;
            }
        }

        if (contentObjId == null) {
            String err = "Unable to find content object for: [" + spaceId + ":"
                    + contentId + "]";
            throw new StorageException(err, RETRY);
        }

        return contentObjId;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getContent(String spaceId, String contentId) {
        log.debug("getContent(" + spaceId + ", " + contentId + ")");
        throwIfSpaceNotExist(spaceId);

        return doGetContent(getContentObjId(spaceId, contentId));
    }

    private InputStream doGetContent(ObjectId contentObjId) {
        log.debug("doGetContent(" + contentObjId + ")");

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = null;
        boolean close = false;

        DownloadHelper helper = new DownloadHelper(emcService, buffer);
        helper.readObject(contentObjId, outStream, close);

        while (!helper.isComplete() && !helper.isFailed()) {
            log.debug("blocking...");
        }

        return new ByteArrayInputStream(outStream.toByteArray());
    }

    /**
     * {@inheritDoc}
     */
    public void deleteContent(String spaceId, String contentId) {
        log.debug("Deleting content: " + spaceId + ", " + contentId);
        throwIfSpaceNotExist(spaceId);

        try {
            emcService.deleteObject(getContentObjId(spaceId, contentId));
        } catch (Exception e) {
            String err = "Error deleting: [" + spaceId + ":" + contentId + "], " +
                    "due to error: " + e.getMessage();
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

        ObjectId objId = getContentObjId(spaceId, contentId);

        // Remove existing user metadata.
        MetadataTags existingTags = listUserMetadataTags(objId);
        deleteUserMetadata(objId, existingTags);

        // Start with required metadata.
        MetadataList metadatas =
                createRequiredContentMetadata(spaceId, contentId);

        // Start adding arg user metadata.
        final boolean isIndexed = false;
        Set<String> keys = contentMetadata.keySet();
        for (String key : keys) {
            String val = contentMetadata.get(key);
            metadatas.addMetadata(new Metadata(key, val, isIndexed));
        }

        setUserMetadata(objId, metadatas);
    }

    private MetadataTags listUserMetadataTags(ObjectId objId) {
        try {
            return emcService.listUserMetadataTags(objId);
        } catch (Exception e) {
            String err = "Error listing user metadata tags for: " + objId +
                    ", due to error: " + e.getMessage();
            throw new StorageException(err, e, RETRY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getContentMetadata(String spaceId,
                                                  String contentId) {
        log.debug("getContentMetadata(" + spaceId + ", " + contentId + ")");

        throwIfSpaceNotExist(spaceId);

        ObjectId objId = getContentObjId(spaceId, contentId);

        if (log.isDebugEnabled()) {
            for (Metadata md : emcService.getSystemMetadata(objId, null)) {
                log.debug("System-metadata: " + md.toString());
            }
            for (Metadata md : emcService.getUserMetadata(objId, null)) {
                log.debug("User-metadata:" + md.toString());
            }
        }

        Map<String, String> metadata = getExistingUserMetadata(objId);
        metadata.putAll(generateManagedContentMetadata(objId));

        // Normalize metadata keys to lowercase.
        Map<String, String> resultMap = new HashMap<String, String>();
        Set<String> keys = metadata.keySet();
        for (String key : keys) {
            String val = metadata.get(key);
            resultMap.put(key.toLowerCase(), val);
        }

        return resultMap;
    }

    private Map<String, String> getExistingUserMetadata(ObjectId objId) {
        Map<String, String> metadata = new HashMap<String, String>();
        MetadataList existingMetadata = null;
        try {
            existingMetadata = emcService.getUserMetadata(objId, null);
        } catch (Exception e) {
            log.warn("Unable to get userMetadata: " + e.getMessage());
        }

        if (existingMetadata != null) {
            for (Metadata md : existingMetadata) {
                metadata.put(md.getName(), md.getValue());
            }
        }
        return metadata;
    }

    private Map<String, String> generateManagedContentMetadata(ObjectId objId) {
        MetadataList sysMd = getSystemMetadata(objId);

        // Content size
        String size = null;
        Metadata foundSize = sysMd.getMetadata(EMC_CONTENT_SIZE);
        if (foundSize != null) {
            size = foundSize.getValue();
        }

        // Modified date
        String modifiedDate = null;
        Metadata foundDate = sysMd.getMetadata(EMC_CONTENT_MODIFIED_DATE);
        if (foundDate != null) {
            modifiedDate = foundDate.getValue();
        }

        // Checksum
        ChecksumUtil cksumUtil = new ChecksumUtil(ChecksumUtil.Algorithm.MD5);
        String cksum = cksumUtil.generateChecksum(doGetContent(objId));

        Map<String, String> metadata = new HashMap<String, String>();
        if (StringUtils.isNotBlank(size)) {
            metadata.put(METADATA_CONTENT_SIZE, size);
        }
        if (StringUtils.isNotBlank(cksum)) {
            metadata.put(METADATA_CONTENT_CHECKSUM, cksum);
        }
        if (StringUtils.isNotBlank(modifiedDate)) {
            metadata.put(METADATA_CONTENT_MODIFIED, modifiedDate);
        }
        return metadata;
    }

    private MetadataList getSystemMetadata(Identifier objId) {
        try {
            return emcService.getSystemMetadata(objId, null);
        } catch (Exception e) {
            String err = "Error getting system metadata for " + objId + ", "
                    + "due to error: " + e.getMessage();
            throw new StorageException(err, RETRY);
        }
    }

    private MetadataTag currentSpaceTag(String spaceId) {
        return new MetadataTag(spaceId, true);
    }

    private MetadataTag spaceRootTag() {
        return new MetadataTag(SPACE_ROOT_TAG_NAME, true);
    }

}
