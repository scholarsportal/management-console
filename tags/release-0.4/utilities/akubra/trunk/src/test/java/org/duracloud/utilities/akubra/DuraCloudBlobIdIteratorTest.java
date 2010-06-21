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

    @Test
    public void withoutPrefix() {
        compare(urisFor(allIds));
    }

    void compare(Set<URI> expected) {
        Set<String> ids = new HashSet<String>();
        for (String id: allIds) {
            ids.add(id);
        }
        Iterator<URI> uriIter = new DuraCloudBlobIdIterator(uriPrefix,
                                                            ids.iterator());
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
