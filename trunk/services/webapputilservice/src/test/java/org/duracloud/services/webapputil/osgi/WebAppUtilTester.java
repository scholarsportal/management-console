package org.duracloud.services.webapputil.osgi;

import org.duracloud.services.webapputil.WebAppUtil;
import org.junit.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Andrew Woods
 *         Date: Dec 7, 2009
 */
public class WebAppUtilTester extends WebAppUtilTestBase {

    private WebAppUtil webappUtil;
    private String SERVICE_ID = "howdy";
    private String WAR_FILE_NAME = "hellowebapp-1.0.0.war";

    private final static String BASE_DIR_PROP = "base.dir";
    private final static String sep = File.separator;

    public WebAppUtilTester(WebAppUtil webappUtil) {
        this.webappUtil = webappUtil;
    }

    public void testWebAppUtil() {
        try {
            doTest();
        } catch (Throwable e) {
            // do nothing
        } finally {
            doTearDown(webappUtil);
        }
    }

    private void doTest() throws Exception {
        super.war = getWar();
        super.url = webappUtil.deploy(SERVICE_ID, war);
        verifyDeployment(url, true);

        webappUtil.unDeploy(url);
        verifyDeployment(url, false);
    }


    protected InputStream getWar() throws FileNotFoundException {
        String baseDir = System.getProperty(BASE_DIR_PROP);
        Assert.assertNotNull(baseDir);

        String resourceDir = baseDir + sep + "src/test/resources/";
        File zipBagFile = new File(resourceDir + WAR_FILE_NAME);

        return new FileInputStream(zipBagFile);
    }
}
