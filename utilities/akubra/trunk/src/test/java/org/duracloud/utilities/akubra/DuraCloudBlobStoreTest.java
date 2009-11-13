package org.duracloud.utilities.akubra;

import java.io.IOException;

import java.net.URI;

import javax.transaction.Transaction;

import org.duracloud.client.ContentStore;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

/**
 * Unit tests for DuraCloudBlobStore.
 *
 * @author Chris Wilper
 */
public class DuraCloudBlobStoreTest {

    @Test
    public void openNonTransactionalConnection() throws IOException {
        createInstance().openConnection(null, null);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void openTransactionalConnection() throws IOException {
        createInstance().openConnection(EasyMock.createMock(Transaction.class),
                                        null);
    }

    private static DuraCloudBlobStore createInstance() throws IOException {
        ContentStore contentStore = EasyMock.createMock(ContentStore.class);
        return new DuraCloudBlobStore(URI.create("urn:test"),
                                      contentStore,
                                      "test");
    }

}
