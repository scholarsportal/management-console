package org.duracloud.aitsync;

import junit.framework.Assert;

import org.duracloud.aitsync.ArchiveItConfig;
import org.duracloud.aitsync.ArchiveItConfigMarshaller;
import org.junit.Test;

public class ArchiveItConfigMarshallerTest {

    @Test
    public void testMarshall() {
        ArchiveItConfig config1 = new ArchiveItConfig();
        String xmlString = ArchiveItConfigMarshaller.marshall(config1);
        ArchiveItConfig config2 = ArchiveItConfigMarshaller.unmarshall(xmlString);
        Assert.assertEquals(config1.getDuracloudHost(), config2.getDuracloudHost());
    }

}
