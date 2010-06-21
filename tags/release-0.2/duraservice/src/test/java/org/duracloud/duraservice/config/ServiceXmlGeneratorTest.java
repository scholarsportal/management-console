package org.duracloud.duraservice.config;

import org.duracloud.common.util.ApplicationConfig;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.Properties;

public class ServiceXmlGeneratorTest {

    @Test
    public void testGenerate() throws Exception {
        TestConfig config = new TestConfig();
        String targetDir = config.getTargetDir();
        URI targetDirUri = new URI(targetDir);
        File targetDirFile = new File(targetDirUri);

        ServiceXmlGenerator xmlGenerator = new ServiceXmlGenerator();
        xmlGenerator.generateServiceXml(targetDirFile.getAbsolutePath());
    }

    private class TestConfig extends ApplicationConfig {
        private String propName = "test-duraservice.properties";

        private Properties getProps() throws Exception {
            return getPropsFromResource(propName);
        }

        public String getTargetDir() throws Exception {
            return getProps().getProperty("targetdir");
        }
    }
}