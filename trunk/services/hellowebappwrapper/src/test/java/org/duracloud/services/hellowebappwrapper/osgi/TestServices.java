package org.duracloud.services.hellowebappwrapper.osgi;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.duracloud.services.ComputeService;
import org.duracloud.services.common.util.BundleHome;
import org.duracloud.services.hellowebappwrapper.HelloWebappWrapper;
import org.duracloud.services.webapputil.WebAppUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Andrew Woods
 *         Date: Dec 10, 2009
 */
public class TestServices extends AbstractDuracloudOSGiTestBasePax {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final int MAX_TRIES = 10;
    private String BINARIES_FILE_NAME = "apache-tomcat-6.0.20.zip";

    private HelloWebappWrapper helloWebappWrapper;
    private WebAppUtil webappUtil;

    @Before
    public void setUp() throws Exception {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        populateWebappUtilResources();
    }

    private void populateWebappUtilResources() throws Exception {
        File serviceWork = getWebappUtil().getWorkDir();
        File tomcatBinaries = new File(getResourceDir(), BINARIES_FILE_NAME);

        FileUtils.copyFileToDirectory(tomcatBinaries, serviceWork);
    }

    private String getResourceDir() {
        String baseDir = System.getProperty(BASE_DIR_PROP);
        org.junit.Assert.assertNotNull(baseDir);

        return baseDir + File.separator + "src/test/resources/";
    }

    @After
    public void tearDown() throws Exception {
        String helloServiceId = getHelloWebappWrapper().getServiceId();
        String utilServiceId = getWebappUtil().getServiceId();

        BundleHome bundleHome = getHelloWebappWrapper().getBundleHome();

        FileUtils.deleteDirectory(bundleHome.getServiceWork(helloServiceId));
        FileUtils.deleteDirectory(bundleHome.getServiceWork(utilServiceId));
    }

    @Test
    public void testHelloWebappWrapper() throws Exception {
        log.debug("testing HelloWebappWrapper");

        HelloWebappWrapperTester tester = new HelloWebappWrapperTester(
            getHelloWebappWrapper());
        tester.testHelloWebappWrapper();

    }

    protected Object getService(String serviceInterface) throws Exception {
        return getService(serviceInterface, null);
    }

    private Object getService(String serviceInterface, String filter)
        throws Exception {
        ServiceReference[] refs = getBundleContext().getServiceReferences(
            serviceInterface,
            filter);

        int count = 0;
        while ((refs == null || refs.length == 0) && count < MAX_TRIES) {
            count++;
            log.debug("Trying to find service: '" + serviceInterface + "'");
            Thread.sleep(1000);
            refs = getBundleContext().getServiceReferences(serviceInterface,
                                                           filter);
        }
        Assert.assertNotNull("service not found: " + serviceInterface, refs[0]);
        log.debug(getPropsText(refs[0]));
        return getBundleContext().getService(refs[0]);
    }

    private String getPropsText(ServiceReference ref) {
        StringBuilder sb = new StringBuilder("properties:");
        for (String key : ref.getPropertyKeys()) {
            sb.append("\tprop: [" + key);
            sb.append(":" + ref.getProperty(key) + "]\n");
        }
        return sb.toString();
    }

    public HelloWebappWrapper getHelloWebappWrapper() throws Exception {
        if (helloWebappWrapper == null) {
            helloWebappWrapper = (HelloWebappWrapper) getService(ComputeService.class.getName(),
                                                                 "(duraService=hellowebappwrapper)");
        }
        Assert.assertNotNull(helloWebappWrapper);
        return helloWebappWrapper;
    }

    public WebAppUtil getWebappUtil() throws Exception {
        if (webappUtil == null) {
            webappUtil = (WebAppUtil) getService(WebAppUtil.class.getName());
        }
        Assert.assertNotNull(webappUtil);
        return webappUtil;
    }

}