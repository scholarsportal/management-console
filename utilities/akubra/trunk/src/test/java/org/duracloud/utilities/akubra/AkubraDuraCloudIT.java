package org.duracloud.utilities.akubra;

import java.io.IOException;
import java.io.OutputStream;

import java.net.URI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import org.akubraproject.Blob;
import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Akubra-DuraCloud Integration Tests.
 *
 * @author Chris Wilper
 */
public class AkubraDuraCloudIT {

    // test value for content
    private static final String VALUE = "value";
    private static final String VALUE2 = "value2";

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

    @Test(expectedExceptions=IOException.class)
    public void nonExistingSpace() throws IOException {
        new DuraCloudBlobStore(URI.create(spaceURL + "-nonexistingspace"), true);
    }

    @Test
    public void isEmpty() throws IOException {
        assertTrue(list(null).isEmpty());
    }

    @Test
    public void createReadUpdateDelete() throws IOException {
        Blob blob = getBlob("createReadUpdateDelete");
        putContent(blob, VALUE, false);
        assertEquals(getContent(blob), VALUE);
        putContent(blob, VALUE2, true);
        assertEquals(getContent(blob), VALUE2);
        blob.delete();
    }

    @Test
    public void createEmpty() throws IOException {
        Blob blob = getBlob("createEmpty");
        putContent(blob, "", false);
        assertEquals(getContent(blob), "");
        blob.delete();
    }

    // gets all blobIds in the test store, filtered by prefix, if given
    private static Set<URI> list(String filterPrefix) throws IOException {
        Set<URI> set = new HashSet<URI>();
        Iterator<URI> iter = connection.listBlobIds(filterPrefix);
        while (iter.hasNext()) {
            set.add(iter.next());
        }
        return set;
    }

    // deletes all blobs in the test store, returning deletedCount
    private static int clear() throws IOException {
        int deletedCount = 0;
        for (URI blobId: list(null)) {
            getBlob(blobId).delete();
            deletedCount++;
        }
        return deletedCount;
    }

    // writes the content of a blob as the given string
    private static void putContent(Blob blob, String content, boolean overwrite)
            throws IOException {
        OutputStream out = blob.openOutputStream(-1, overwrite);
        out.write(content.getBytes("UTF-8"));
        out.close();
    }

    // reads the content of a blob as a string
    private static String getContent(Blob blob) throws IOException {
        return IOUtils.toString(blob.openInputStream());
    }

    // gets a blob from the test store, by blobId
    private static Blob getBlob(URI blobId) throws IOException {
        return connection.getBlob(blobId, null);
    }

    // gets a blob from the test store, by contentId
    private static Blob getBlob(String contentId) throws IOException {
        return getBlob(URI.create(spaceURL + "/" + contentId));
    }

}
