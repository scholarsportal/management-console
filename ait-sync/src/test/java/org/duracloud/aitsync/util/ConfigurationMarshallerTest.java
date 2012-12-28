package org.duracloud.aitsync.util;

import org.duracloud.aitsync.domain.Configuration;
import org.junit.Assert;
import org.junit.Test;
/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public class ConfigurationMarshallerTest {

    @Test
    public void testMarshall() {
        Configuration config1 = new Configuration();
        String xmlString = ConfigurationMarshaller.marshall(config1);
        Configuration config2 = ConfigurationMarshaller.unmarshall(xmlString);
        Assert.assertEquals(config1.getDuracloudHost(), config2.getDuracloudHost());
    }

}
