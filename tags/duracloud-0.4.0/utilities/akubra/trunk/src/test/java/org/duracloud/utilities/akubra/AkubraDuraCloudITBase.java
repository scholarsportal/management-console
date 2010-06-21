package org.duracloud.utilities.akubra;

import java.io.IOException;

import java.net.URI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.akubraproject.Blob;
import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.testng.annotations.BeforeClass;

/**
 * Base class for Akubra-DuraCloud Integration Tests.
 *
 * @author Chris Wilper
 */
public abstract class AkubraDuraCloudITBase {

    static URI spaceURL;
    static BlobStore store;
    static BlobStoreConnection connection;

    @BeforeClass
    public static void initStore() throws IOException {
        String url = System.getProperty("spaceURL");
        if (url == null) {
            throw new Error("System property, spaceURL, is not defined");
        }
        spaceURL = URI.create(url);
        store = new DuraCloudBlobStore(spaceURL, true);
        Map<String, String> hints = new HashMap<String, String>();
        hints.put(DuraCloudBlobStore.READ_AFTER_WRITE, "true");
        connection = store.openConnection(null, hints);
        clear();
    }

    // gets all blobIds in the test store, filtered by prefix, if given
    static Set<URI> list(String filterPrefix) throws IOException {
        Set<URI> set = new HashSet<URI>();
        Iterator<URI> iter = connection.listBlobIds(filterPrefix);
        while (iter.hasNext()) {
            set.add(iter.next());
        }
        return set;
    }

    // deletes all blobs in the test store, returning deletedCount
    static int clear() throws IOException {
        int deletedCount = 0;
        for (URI blobId: list(null)) {
            connection.getBlob(blobId, null).delete();
            deletedCount++;
        }
        return deletedCount;
    }

    // gets a blob from the test store, by contentId
    static Blob getBlob(String contentId, Map<String, String> hints)
            throws IOException {
        return connection.getBlob(URI.create(spaceURL + "/" + contentId), hints);
    }

}
