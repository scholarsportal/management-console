package org.duracloud.durastore.test;

import org.duracloud.storage.error.StorageException;
import org.duracloud.storage.provider.StorageProvider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Storage Provider implementation used for system testing 
 *
 * @author: Bill Branan
 * Date: Oct 7, 2009
 */
public class TestStorageProvider implements StorageProvider {

    private static int getSpaceContentsAttempts = 0;

    public Iterator<String> getSpaces() {
        throw new StorageException("getSpaces is not retried",
                                   StorageException.NO_RETRY);
    }

    public Iterator<String> getSpaceContents(String spaceId) {
        // spaceId indicates the number of tries until success
        int attemptsBeforeSuccess = Integer.valueOf(spaceId);
        if(getSpaceContentsAttempts < attemptsBeforeSuccess) {
            getSpaceContentsAttempts++;
            throw new StorageException(attemptsBeforeSuccess +
                                       " calls required before success.",
                                       StorageException.RETRY);
        } else {
            List<String> retries = new ArrayList<String>();
            retries.add(String.valueOf(getSpaceContentsAttempts));
            getSpaceContentsAttempts = 0;
            return retries.iterator();
        }
    }

    public void createSpace(String spaceId) {
        // Default method body
    }

    public void deleteSpace(String spaceId) {
        // Default method body
    }

    public Map<String, String> getSpaceMetadata(String spaceId) {
        // Default method body
        return null;
    }

    public void setSpaceMetadata(String spaceId,
                                 Map<String, String> spaceMetadata) {
        // Default method body
    }

    public AccessType getSpaceAccess(String spaceId) {
        // Default method body
        return AccessType.OPEN;
    }

    public void setSpaceAccess(String spaceId, AccessType access) {
        // Default method body
    }

    public String addContent(String spaceId, String contentId,
                             String contentMimeType, long contentSize,
                             InputStream content) {
        // Default method body
        return null;
    }

    public InputStream getContent(String spaceId, String contentId) {
        // Default method body
        return null;
    }

    public void deleteContent(String spaceId, String contentId) {
        // Default method body
    }

    public void setContentMetadata(String spaceId, String contentId,
                                   Map<String, String> contentMetadata) {
        // Default method body
    }

    public Map<String, String> getContentMetadata(String spaceId,
                                                  String contentId) {
        // Default method body
        return null;
    }
}
