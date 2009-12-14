package org.duracloud.services.hellowebappwrapper.osgi;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.duracloud.services.ComputeService;
import org.duracloud.services.hellowebappwrapper.HelloWebappWrapper;
import static org.duracloud.services.hellowebappwrapper.osgi.AbstractDuracloudOSGiTestBasePax.BASE_DIR_PROP;
import org.junit.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * @author Andrew Woods
 *         Date: Dec 10, 2009
 */
public class HelloWebappWrapperTester {

    private String warName = "hellowebapp-1.0.0.war";
    private String context = FilenameUtils.getBaseName(warName);
    private int port = 18080;

    private String urlOrig = "http://example.org";
    private String urlRunning = "http://127.\\d.\\d.1:" + port + "/" + context;
    private HelloWebappWrapper wrapper;

    public HelloWebappWrapperTester(HelloWebappWrapper wrapper)
        throws IOException {
        this.wrapper = wrapper;

        // set up war to deploy
        File war = getWar();
        File workDir = wrapper.getBundleHome().getWork();
        String serviceId = wrapper.getServiceId();
        File serviceWorkDir = new File(workDir, serviceId);
        serviceWorkDir.mkdirs();

        FileUtils.copyFileToDirectory(war, serviceWorkDir);
    }

      protected File getWar() throws FileNotFoundException {
        String baseDir = System.getProperty(BASE_DIR_PROP);
        Assert.assertNotNull(baseDir);

        String resourceDir = baseDir + File.separator + "src/test/resources/";
        return new File(resourceDir, warName);
    }

    protected void testHelloWebappWrapper() throws Exception {
        Throwable error = null;
        try {
            doTest();
        } catch (Throwable e) {
            error = e;
        } finally {
            doTearDown();
        }

        String msg = (error == null ? "no error" : error.getMessage());
        Assert.assertNull(msg, error);
    }

    private void doTearDown() {
        try {
            wrapper.stop();
        } catch (Exception e) {
            // do nothing.
        }
    }

    private void doTest() throws Exception {
        verifyURL(urlOrig);

        ComputeService.ServiceStatus status = wrapper.getServiceStatus();
        Assert.assertNotNull(status);
        Assert.assertEquals(ComputeService.ServiceStatus.INSTALLED, status);

        wrapper.start();
        status = wrapper.getServiceStatus();
        Assert.assertEquals(ComputeService.ServiceStatus.STARTED, status);

        verifyURL(urlRunning);

        wrapper.stop();
        status = wrapper.getServiceStatus();
        Assert.assertEquals(ComputeService.ServiceStatus.STOPPED, status);

        verifyURL(urlOrig);
    }

    private void verifyURL(String expectedURL) {
        Map<String, String> props = wrapper.getServiceProps();
        Assert.assertNotNull(props);

        String urlProp = props.get("url");
        Assert.assertNotNull(urlProp);
        Assert.assertTrue(urlProp.matches(expectedURL));
    }
}
