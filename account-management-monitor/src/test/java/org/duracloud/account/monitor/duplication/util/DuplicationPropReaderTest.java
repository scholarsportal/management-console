/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.duplication.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

/**
 * @author Bill Branan
 * Date: 4/19/13
 */
public class DuplicationPropReaderTest {

    /*
     * Tests the conversion of properties to a map.
     *
     * Properties provided in the test:
     * duplication.1=abc.duracloud.org
     * duplication.2=def.duracloud.org
     * duplication.3.host=xyz.duracloud.org
     * duplication.3.spaces=space-1,space-2,space-3
     *
     * Expected Map values:
     * abc.duracloud.org = ALL
     * def.duracloud.org = ALL
     * xyz.duracloud.org = space-1,space-2,space-3
     */
    @Test
    public void testReadDupProps() throws Exception {
        StringBuilder propBuilder = new StringBuilder();
        propBuilder.append("duplication.1=abc.duracloud.org\n");
        propBuilder.append("duplication.2=def.duracloud.org\n");
        propBuilder.append("duplication.3.host=xyz.duracloud.org\n");
        propBuilder.append("duplication.3.spaces=space-1,space-2,space-3\n");

        Properties props = new Properties();
        props.load(new StringReader(propBuilder.toString()));

        DuplicationPropReader propReader = new DuplicationPropReader();

        Map<String, String> dupHosts = propReader.readDupProps(props);
        assertNotNull(dupHosts);
        assertEquals(3, dupHosts.size());
        assertEquals(DuplicationPropReader.ALL_SPACES,
                     dupHosts.get("abc.duracloud.org"));
        assertEquals(DuplicationPropReader.ALL_SPACES,
                     dupHosts.get("def.duracloud.org"));
        assertEquals("space-1,space-2,space-3",
                     dupHosts.get("xyz.duracloud.org"));
    }

}
