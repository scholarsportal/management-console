package org.duracloud.utilities.akubra;

import java.net.URI;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for DuraCloudBlobIdIterator.
 *
 * @author Chris Wilper
 */
public class DuraCloudBlobIdIteratorTest {

    static final String uriPrefix = "urn:test:";

    static final String[] allIds     = new String[] { "foo", "bar", "baz", "qux" };
    static final String[] fPrefixIds = new String[] { "foo" };
    static final String[] bPrefixIds = new String[] { "bar", "baz" };

    @Test
    public void withoutPrefix() {
        compare(null, urisFor(allIds));
    }

    @Test
    void fPrefix() {
        compare("f", urisFor(fPrefixIds));
    }

    @Test
    void bPrefix() {
        compare("b", urisFor(bPrefixIds));
    }

    void compare(String idPrefix, Set<URI> expected) {
        String filterPrefix = null;
        if (idPrefix != null) filterPrefix = uriPrefix + idPrefix;
        Set<String> ids = new HashSet<String>();
        for (String id: allIds) {
            ids.add(id);
        }
        Iterator<URI> uriIter = new DuraCloudBlobIdIterator(uriPrefix,
                                                            ids.iterator(),
                                                            filterPrefix);
        Set<URI> got = new HashSet<URI>();
        while (uriIter.hasNext()) {
            got.add(uriIter.next());
        }
        assertEquals(got, expected);
    }

    static Set<URI> urisFor(String[] ids) {
        Set<URI> set = new HashSet<URI>();
        for (String id: ids) {
            set.add(URI.create(uriPrefix + id));
        }
        return set;
    }
}
