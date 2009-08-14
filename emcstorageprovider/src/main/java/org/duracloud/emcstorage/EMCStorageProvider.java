package org.duracloud.emcstorage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.security.DigestInputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.emc.esu.api.Acl;
import com.emc.esu.api.DownloadHelper;
import com.emc.esu.api.EsuApi;
import com.emc.esu.api.EsuException;
import com.emc.esu.api.Grant;
import com.emc.esu.api.Grantee;
import com.emc.esu.api.Identifier;
import com.emc.esu.api.Metadata;
import com.emc.esu.api.MetadataList;
import com.emc.esu.api.MetadataTag;
import com.emc.esu.api.MetadataTags;
import com.emc.esu.api.ObjectId;
import com.emc.esu.api.Permission;
import com.emc.esu.api.UploadHelper;
import com.emc.esu.api.rest.EsuRestApi;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import org.duracloud.common.util.ChecksumUtil;
import org.duracloud.common.util.ExceptionUtil;
import org.duracloud.storage.domain.StorageException;
import org.duracloud.storage.provider.StorageProvider;

import static org.duracloud.storage.util.StorageProviderUtil.compareChecksum;
import static org.duracloud.storage.util.StorageProviderUtil.contains;
import static org.duracloud.storage.util.StorageProviderUtil.wrapStream;

/**
 * Provides content storage backed by EMC's Storage Utility.
 */
public class EMCStorageProvider
        implements StorageProvider {

    private final Logger log = Logger.getLogger(EMCStorageProvider.class);

    private static final String SPACE_ROOT_TAG_NAME = "emc-space-root-tag";

    private static final String EMC_CONTENT_SIZE = "size";

    private static final String EMC_CONTENT_MODIFIED_DATE = "mtime";

    private static final String EMC_CREATION_DATE_NAME = "ctime";

    private static final String ESU_HOST = "accesspoint.emccis.com";

    private static final int ESU_PORT = 80;

    private EsuApi emcService = null;

    public EMCStorageProvider(String uid, String sharedSecret)
            throws StorageException {
        emcService = new EsuRestApi(ESU_HOST, ESU_PORT, uid, sharedSecret);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getSpaces() throws StorageException {
        List<String> spaces = new ArrayList<String>();
        for (Identifier objId : getSpaceObjects()) {
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

    private List<Identifier> getSpaceObjects() throws StorageException {
        List<Identifier> objs = null;
        try {
            objs = emcService.listObjects(spaceRootTag());
        } catch (EsuException e) {
            doThrow("Unable to find any spaces", e);
        }
        return objs;
    }

    private String getSpaceNameForSpaceObject(Identifier objId)
            throws StorageException {
        MetadataTags tags = new MetadataTags();
        tags.addTag(spaceRootTag());

        String id = null;
        try {
            // There should only be one element in the userMetadata.
            MetadataList userMetadata = emcService.getUserMetadata(objId, tags);
            id = userMetadata.iterator().next().getValue();
        } catch (Exception e) {
            doThrow("Unable to find spaceRootTag for space: " + objId, e);
        }
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getSpaceContents(String spaceId)
            throws StorageException {

        List<String> contentNames = new ArrayList<String>();
        for (Identifier objId : getCompleteSpaceContents(spaceId)) {
            contentNames.add(getContentNameForContentObject(objId));
        }

        return contentNames.iterator();
    }

    private List<Identifier> getCompleteSpaceContents(String spaceId)
            throws StorageException {
        List<Identifier> entries = null;
        try {
            entries = emcService.listObjects(currentSpaceTag(spaceId));
        } catch (EsuException e) {
            doThrow("Unable to list objs with current space tag: " + spaceId, e);
        }
        return entries;
    }

    private String getContentNameForContentObject(Identifier objId)
            throws StorageException {
        MetadataTags tags = new MetadataTags();
        tags.addTag(contentNameTag());

        String name = null;
        try {
            // There should only be one 'content-name-tag'.
            MetadataList userMetadata = emcService.getUserMetadata(objId, tags);
            name = userMetadata.iterator().next().getValue();
        } catch (Exception e) {
            doThrow("Unable to find contentNameTag for obj: " + objId, e);
        }
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void createSpace(String spaceId) throws StorageException {
        log.debug("Trying to create space for: " + spaceId);

        throwIfSpaceExists(spaceId);
        Identifier objId = createSpaceObject(spaceId);

        log.debug("\t...space created with id: " + objId);
    }

    private void throwIfSpaceExists(String spaceId) throws StorageException {
        Iterator<String> spaces = null;
        try {
            spaces = getSpaces();
        } catch (Exception e) {
        }
        if (spaces != null && contains(spaces, spaceId)) {
            throw new StorageException("Space already exists: " + spaceId);
        }
    }

    private Identifier createSpaceObject(String spaceId)
            throws StorageException {
        Acl acl = null;
        byte[] data = null;
        String mimeType = null;
        MetadataList metadataList = createRequiredRootMetadata(spaceId);

        ObjectId id = null;
        try {
            // This object only serves the purpose of representing the
            //  existence of a 'space' with id: spaceId.
            id = emcService.createObject(acl, metadataList, data, mimeType);
        } catch (EsuException e) {
            doThrow("Could not create EMC space with spaceId " + spaceId, e);
        }
        return id;
    }

    private MetadataList createRequiredRootMetadata(String spaceId) {
        MetadataList metadataList = new MetadataList();
        metadataList.addMetadata(new Metadata(SPACE_ROOT_TAG_NAME,
                                              spaceId,
                                              true));

        metadataList.addMetadata(new Metadata(METADATA_SPACE_NAME,
                                              spaceId,
                                              true));
        return metadataList;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteSpace(String spaceId) throws StorageException {
        log.debug("Deleting space: " + spaceId);

        deleteSpaceContents(spaceId);
        deleteObject(getRootId(spaceId));
    }

    private void deleteSpaceContents(String spaceId) throws StorageException {
        List<Identifier> contentIds = new ArrayList<Identifier>();
        try {
            contentIds = getCompleteSpaceContents(spaceId);
        } catch (StorageException e) {
            log.info(e.getMessage());
        }

        for (Identifier objId : contentIds) {
            deleteObject(objId);
        }
    }

    private void deleteObject(Identifier objId) throws StorageException {
        try {
            emcService.deleteObject(objId);
        } catch (Exception e) {
            doThrow("Unable to delete object: " + objId, e);
        }
        log.debug("Deleted: " + objId);
    }

    protected Identifier getRootId(String spaceId) throws StorageException {
        Identifier rootId = null;
        for (Identifier objId : getSpaceObjects()) {
            if (spaceId.equals(getSpaceNameForSpaceObject(objId))) {
                rootId = objId;
            }
        }

        if (rootId == null) {
            String err = "ERROR: Unable to find rootId for space: " + spaceId;
            log.debug(err);
            throw new StorageException(err);
        }

        log.debug("Found rootId for spaceId: " + rootId + ", " + spaceId);
        return rootId;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getSpaceMetadata(String spaceId)
            throws StorageException {
        ObjectId rootObjId = (ObjectId) getRootId(spaceId);

        // Get existing user metadata.
        Map<String, String> spaceMetadata = getExistingUserMetadata(rootObjId);

        // Over-write managed metadata.
        spaceMetadata.put(METADATA_SPACE_CREATED, getCreationDate(rootObjId));
        spaceMetadata.put(METADATA_SPACE_COUNT, getContentObjCount(spaceId));
        spaceMetadata.put(METADATA_SPACE_NAME, spaceId);
        spaceMetadata.put(METADATA_SPACE_ACCESS,
                          doGetSpaceAccess(rootObjId).toString());

        return spaceMetadata;
    }

    private String getCreationDate(Identifier id) {
        String creationDate = "unknown-creation-date";
        try {
            MetadataList sysMetadata = emcService.getSystemMetadata(id, null);
            for (Metadata sysMd : sysMetadata) {
                if (EMC_CREATION_DATE_NAME.equals(sysMd.getName())) {
                    creationDate = sysMd.getValue();
                }
            }
        } catch (EsuException e) {
            log.warn("Creation date not found: " + id + ", " + e.getMessage());
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
    public void setSpaceMetadata(String spaceId, Map<String, String> spaceMetadata)
            throws StorageException {
        Identifier rootObjId = getRootId(spaceId);

        // Do not overwrite space root tag
        spaceMetadata.remove(SPACE_ROOT_TAG_NAME);

        // Remove volatile metadata.
        clearDisposableRootMetadata(rootObjId);

        // Start with required metadata.
        MetadataList metadatas = createRequiredRootMetadata(spaceId);

        // Start adding arg user metadata.
        final boolean isIndexed = false;
        Iterator<String> keys = spaceMetadata.keySet().iterator();
        while (keys != null && keys.hasNext()) {
            String key = keys.next();
            String val = spaceMetadata.get(key);
            metadatas.addMetadata(new Metadata(key, val, isIndexed));
        }

        // The actual setting.
        try {
            emcService.setUserMetadata(rootObjId, metadatas);
        } catch (EsuException e) {
            doThrow("Unable to setUserMetadata: " + rootObjId, e);
        }
    }

    private void clearDisposableRootMetadata(Identifier objId) {
        try {
            MetadataTags existingTags = emcService.listUserMetadataTags(objId);
            MetadataTags disposableTags = getSpaceTagsToRemove(existingTags);
            emcService.deleteUserMetadata(objId, disposableTags);
        } catch (Exception e) {
            log.warn("Clearing root metadata:" + objId + ", " + e.getMessage());
        }
    }

    private MetadataTags getSpaceTagsToRemove(MetadataTags existingTags) {
        MetadataTags tags = new MetadataTags();
        for (MetadataTag tag : existingTags) {
            String tagName = tag.getName();
            if (!tagName.equals(SPACE_ROOT_TAG_NAME)
                    && !tagName.equals(METADATA_SPACE_NAME)) {
                tags.addTag(tag);
            }
        }
        return tags;
    }

    /**
     * {@inheritDoc}
     */
    public AccessType getSpaceAccess(String spaceId) throws StorageException {
        return doGetSpaceAccess(getRootId(spaceId));
    }

    private AccessType doGetSpaceAccess(Identifier spaceObjId) {
        AccessType spaceAccess = AccessType.CLOSED;
        for (Grant grant : getAcl(spaceObjId)) {
            if (Grantee.OTHER.equals(grant.getGrantee())) {
                if (!Permission.NONE.equals(grant.getPermission())) {
                    spaceAccess = AccessType.OPEN;
                }
            }
        }
        return spaceAccess;
    }

    private Acl getAcl(Identifier objId) {
        Acl acl = new Acl();
        try {
            acl = emcService.getAcl(objId);
        } catch (Exception e) {
            log.warn("Acl not found: " + objId + ", " + e.getMessage());
        }
        log.debug("ACL for: " + objId + ", " + acl);
        return acl;
    }

    /**
     * {@inheritDoc}
     */
    public void setSpaceAccess(String spaceId, AccessType access)
            throws StorageException {
        // Default is 'closed'.
        String permission = Permission.NONE;
        if (AccessType.OPEN.equals(access)) {
            permission = Permission.READ;
        }

        Identifier rootObjId = getRootId(spaceId);

        Acl newAcl = new Acl();
        for (Grant grant : getAcl(rootObjId)) {
            Grant g = grant;
            if (isGroup(grant)) {
                g = new Grant(grant.getGrantee(), permission);
            }
            newAcl.addGrant(g);
        }

        // Set ACL for root.
        setObjectAcl(rootObjId, newAcl);

        // Set ACL for all objects contained in the space.
        try {
            for (Identifier id : getCompleteSpaceContents(spaceId)) {
                setObjectAcl(id, newAcl);
            }
        } catch (StorageException e) {
            log.info("No entries found for space: " + spaceId);
        }

    }

    private void setObjectAcl(Identifier objId, Acl newAcl)
            throws StorageException {
        try {
            emcService.setAcl(objId, newAcl);
        } catch (Exception e) {
            doThrow("Unable to set acl: " + objId, e);
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
                             InputStream content) throws StorageException {
        log.debug("Adding Content: [" + spaceId + ":" + contentId + "], size:"
                + contentSize + "-bytes");

        // Set access control to mirror the bucket
        Acl acl = getAcl(getRootId(spaceId));

        MetadataList metadataList =
                createRequiredContentMetadata(spaceId, contentId, mimeType);

        UploadHelper helper = new UploadHelper(emcService);
        boolean closeStream = true;

        // Determine if object already exists.
        ObjectId objId = null;
        try {
            objId = getContentObjId(spaceId, contentId);
        } catch (Exception e) {
        }

        // Wrap the content to be able to compute a checksum during transfer
        DigestInputStream wrappedContent = wrapStream(content);

        // Add new object.
        if (objId == null) {
            helper.createObject(wrappedContent, acl, metadataList, closeStream);
        }
        // Update existing object.
        else {
            helper.updateObject(objId, wrappedContent, acl, metadataList, closeStream);
        }

        // Compare checksum
        return compareChecksum(this, spaceId, contentId, wrappedContent);
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

        metadataList.addMetadata(new Metadata(METADATA_CONTENT_NAME,
                                              contentId,
                                              isIndexed));
        metadataList.addMetadata(new Metadata(METADATA_SPACE_NAME,
                                              spaceId,
                                              isIndexed));

        if (mimeType != null) {
            metadataList.addMetadata(new Metadata(METADATA_CONTENT_MIMETYPE,
                                                  mimeType,
                                                  isIndexed));
        }
        return metadataList;
    }

    protected ObjectId getContentObjId(String spaceId, String contentId)
            throws StorageException {
        // FIXME: should not have to loop through all content to find contentId.
        //        EsuApi.queryObjects(String xquery) ?

        ObjectId contentObjId = null;
        for (Identifier objId : getCompleteSpaceContents(spaceId)) {
            if (contentId.equals(getContentNameForContentObject(objId))) {
                contentObjId = (ObjectId) objId;
            }
        }

        if (contentObjId == null) {
            String err =
                    "Unable to find content object for: [" + spaceId + ":"
                            + contentId + "]";
            log.debug(err);
            throw new StorageException(err);
        }

        return contentObjId;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getContent(String spaceId, String contentId)
            throws StorageException {
        return doGetContent(getContentObjId(spaceId, contentId));
    }

    private InputStream doGetContent(ObjectId contentObjId) {
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
    public void deleteContent(String spaceId, String contentId)
            throws StorageException {
        log.debug("Deleting content: " + spaceId + ", " + contentId);
        try {
            emcService.deleteObject(getContentObjId(spaceId, contentId));
        } catch (Exception e) {
            doThrow("Unable to delete: [" + spaceId + ":" + contentId + "]", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setContentMetadata(String spaceId,
                                   String contentId,
                                   Map<String, String> contentMetadata)
            throws StorageException {
        ObjectId objId = getContentObjId(spaceId, contentId);

        // Remove existing user metadata.
        clearDisposableContentMetadata(objId);

        // Start with required metadata.
        MetadataList metadatas =
                createRequiredContentMetadata(spaceId, contentId);

        // Start adding arg user metadata.
        final boolean isIndexed = false;
        Iterator<String> keys = contentMetadata.keySet().iterator();
        while (keys != null && keys.hasNext()) {
            String key = keys.next();
            String val = contentMetadata.get(key);
            metadatas.addMetadata(new Metadata(key, val, isIndexed));
        }

        try {
            emcService.setUserMetadata(objId, metadatas);
        } catch (Exception e) {
            doThrow("Setting userMetadata: " + spaceId + ":" + contentId, e);
        }
    }

    private void clearDisposableContentMetadata(ObjectId objId) {
        try {
            MetadataTags existingTags = emcService.listUserMetadataTags(objId);
            MetadataTags disposableTags = getContentTagsToRemove(existingTags);
            emcService.deleteUserMetadata(objId, disposableTags);
        } catch (Exception e) {
            log.warn("Unable to clear userMetadata: " + objId);
        }
    }

    private MetadataTags getContentTagsToRemove(MetadataTags existingTags) {
        MetadataTags tags = new MetadataTags();
        for (MetadataTag tag : existingTags) {
            if (!tag.getName().equals(METADATA_CONTENT_MIMETYPE)) {
                tags.addTag(tag);
            }
        }
        return tags;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getContentMetadata(String spaceId, String contentId)
            throws StorageException {
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
        return metadata;
    }

    private Map<String, String> getExistingUserMetadata(ObjectId objId) {
        Map<String, String> metadata = new HashMap<String, String>();
        MetadataList metadatas = null;
        try {
            metadatas = emcService.getUserMetadata(objId, null);
        } catch (Exception e) {
            log.info("Unable to get userMetadata: " + e.getMessage());
        }

        if (metadatas != null) {
            for (Metadata md : metadatas) {
                metadata.put(md.getName(), md.getValue());
            }
        }
        return metadata;
    }

    private Map<String, String> generateManagedContentMetadata(ObjectId objId) {
        MetadataList sysMd = null;
        try {
            sysMd = emcService.getSystemMetadata(objId, null);
        } catch (Exception e) {
            log.warn("Unable to get sysMetadata: " + objId);
        }

        // Content size
        String size = null;
        Metadata foundSize = sysMd.getMetadata(EMC_CONTENT_SIZE);
        if (sysMd != null && foundSize != null) {
            size = foundSize.getValue();
        }

        // Modified date
        String modifiedDate = null;
        Metadata foundDate = sysMd.getMetadata(EMC_CONTENT_MODIFIED_DATE);
        if (sysMd != null && foundDate != null) {
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

    private MetadataTag currentSpaceTag(String spaceId) {
        return new MetadataTag(spaceId, true);
    }

    private MetadataTag spaceRootTag() {
        return new MetadataTag(SPACE_ROOT_TAG_NAME, true);
    }

    private MetadataTag contentNameTag() {
        return new MetadataTag(METADATA_CONTENT_NAME, true);
    }

    private void doThrow(String msg, Exception e) throws StorageException {
        String err = msg + " : " + e.getMessage();
        log.error(err);
        log.debug(ExceptionUtil.getStackTraceAsString(e));
        throw new StorageException(err, e);
    }
}
