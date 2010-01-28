package org.duracloud.utilities.akubra;

import java.io.IOException;

import java.net.URI;

import javax.transaction.Transaction;

import org.duracloud.client.ContentStore;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for DuraCloudBlobStore.
 *
 * @author Chris Wilper
 */
public class DuraCloudBlobStoreTest {

    @Test
    public void openNonTransactionalConnection() throws IOException {
        getInstance().openConnection(null, null);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void openTransactionalConnection() throws IOException {
        getInstance().openConnection(EasyMock.createMock(Transaction.class),
                                        null);
    }

    @Test
    public void spaceURLDefaultPort() {
        assertEquals(parse("http://host/test-context/test-space"),
                     new String[] { "host", "80", "test-context", "test-space" });
    }

    @Test
    public void spaceURLSpecificPort() {
        assertEquals(parse("http://host:8080/test-context/test-space"),
                     new String[] { "host", "8080", "test-context", "test-space" });
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void spaceURLWrongScheme() {
        parse("ftp://host/test-context/test-space");
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void spaceURLTrailingSlash() {
        parse("http://host/test-context/test-space/");
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void spaceURLTooManySegments() {
        parse("http://host/test-context/test-space/foo");
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void spaceURLTooFewSegments() {
        parse("http://host/test-context");
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void spaceURLBadSpaceId() {
        parse("http://host/test-context/bad-spaceid-");
    }

    private static String[] parse(String spaceURL) {
        return DuraCloudBlobStore.parseSpaceURL(URI.create(spaceURL));
    }

    private static DuraCloudBlobStore getInstance() throws IOException {
        return new DuraCloudBlobStore(EasyMock.createMock(ContentStore.class),
                                      "test-space");
    }

}
