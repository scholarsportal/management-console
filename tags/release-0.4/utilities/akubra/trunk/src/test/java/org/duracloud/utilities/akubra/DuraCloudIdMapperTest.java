package org.duracloud.utilities.akubra;

import java.net.URI;

import org.akubraproject.map.IdMapper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for DuraCloudIdMapper.
 *
 * @author Chris Wilper
 */
public class DuraCloudIdMapperTest {

    private static final URI spaceURL = URI.create("http://host/test-context/test-space");

    private static final String pathPrefix = "prefix/";

    private static final URI id1 = URI.create("info:test/1");
    private static final URI id2 = URI.create("info:test/2?");
    private static final URI id3 = URI.create("info:test/3~");

    private static final String enc1 = "info:test/1";
    private static final String enc2 = "info:test/2~3F";
    private static final String enc3 = "info:test/3~7E";

    @Test
    public void internalizeWithPrefix() {
        DuraCloudIdMapper mapper = new DuraCloudIdMapper(spaceURL, pathPrefix);
        iTest(mapper, id1, enc1, true);
        iTest(mapper, id2, enc2, true);
        iTest(mapper, id3, enc3, true);
    }

    @Test
    public void internalizeWithoutPrefix() {
        DuraCloudIdMapper mapper = new DuraCloudIdMapper(spaceURL, null);
        iTest(mapper, id1, enc1, false);
        iTest(mapper, id2, enc2, false);
        iTest(mapper, id3, enc3, false);
    }

    @Test
    public void externalizeWithPrefix() {
        DuraCloudIdMapper mapper = new DuraCloudIdMapper(spaceURL, pathPrefix);
        eTest(mapper, id1, enc1, true);
        eTest(mapper, id2, enc2, true);
        eTest(mapper, id3, enc3, true);
    }

    @Test
    public void externalizeWithoutPrefix() {
        DuraCloudIdMapper mapper = new DuraCloudIdMapper(spaceURL, null);
        eTest(mapper, id1, enc1, false);
        eTest(mapper, id2, enc2, false);
        eTest(mapper, id3, enc3, false);
    }

    private void iTest(IdMapper mapper, URI externalId, String encodedId, boolean withPrefix) {
        String prefix = "";
        if (withPrefix) prefix = pathPrefix;
        assertEquals(mapper.getInternalId(externalId),
                     URI.create(spaceURL + "/" + prefix + encodedId));
    }

    private void eTest(IdMapper mapper, URI externalId, String encodedId, boolean withPrefix) {
        String prefix = "";
        if (withPrefix) prefix = pathPrefix;
        assertEquals(mapper.getExternalId(URI.create(spaceURL + "/" + prefix + encodedId)),
                     externalId);
    }

}
