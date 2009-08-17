
package org.duracloud.servicesadmin.osgi;

import java.io.File;

import java.util.List;

import org.apache.commons.httpclient.util.HttpURLConnection;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.services.ComputeService;
import org.duracloud.servicesutil.beans.ComputeServiceBean;
import org.duracloud.servicesutil.client.ServiceUploadClient;
import org.duracloud.servicesutil.util.ServiceSerializer;
import org.duracloud.servicesutil.util.XMLServiceSerializerImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ServiceInstallUninstallFlowTester {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final BundleContext bundleContext;

    //    private ServiceInstaller installer;

    private ServiceUploadClient client;

    private ServiceSerializer serializer;

    //    private final ComputeService hello;

    private final static String TEST_SERVICE = "HelloService";

    private final static String TEST_BUNDLE_FILE_NAME =
            "helloservice-1.0.0.jar";

    private final String BASE_URL = "http://localhost:8089/servicesadmin-1.0.0";

    public ServiceInstallUninstallFlowTester(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    //    protected void setUp() {
    //        try {
    //            deleteTestBundle(getBundleHome());
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
    //
    //    protected void tearDown() {
    //        try {
    //            deleteTestBundle(getBundleHome());
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }

    public void testNewServiceFlow() throws Exception {
        // Allow tomcat to come up.
        Thread.sleep(5000);

        // check new service does not exist
        verifyTestServiceIsListed(false);
        verifyTestServiceIsInstalled(false);

        // install service
        installTestBundle();

        // Allow test-service to come up.
        Thread.sleep(5000);

        // check new service exists and available in container
        verifyTestServiceIsListed(true);
        verifyTestServiceIsInstalled(true);

        // uninstall service
        uninstallTestBundle();

        // Allow test-service to go down.
        Thread.sleep(5000);

        // check new service does not exist
        verifyTestServiceIsListed(false);
        verifyTestServiceIsInstalled(false);
    }

    protected void installTestBundle() throws Exception {
        HttpResponse response =
                getClient().postServiceBundle(getTestBundleFile());
        Assert.assertNotNull(response);

        int statusCode = response.getStatusCode();
        Assert.assertEquals(HttpURLConnection.HTTP_OK, statusCode);
    }

    private void uninstallTestBundle() throws Exception {
        HttpResponse response =
                getClient().deleteServiceBundle(TEST_BUNDLE_FILE_NAME);
        assertNotNull(response);

        int statusCode = response.getStatusCode();
        assertEquals(HttpURLConnection.HTTP_OK, statusCode);
    }

    private void verifyTestServiceIsListed(boolean exists) throws Exception {
        HttpResponse response = getClient().getServiceListing();
        assertNotNull(response);

        int statusCode = response.getStatusCode();
        assertEquals(HttpURLConnection.HTTP_OK, statusCode);

        String body = response.getResponseBody();
        assertNotNull(body);

        List<ComputeServiceBean> beans = getSerializer().deserializeList(body);
        boolean helloServiceFound = false;
        for (ComputeServiceBean bean : beans) {
            String serviceDesc = bean.getServiceName();
            log.debug("dura-service: " + serviceDesc);

            if (!helloServiceFound) {
                helloServiceFound = serviceFound(serviceDesc, TEST_SERVICE);
            }
        }
        assertEquals(exists, helloServiceFound);

    }

    private void verifyTestServiceIsInstalled(boolean exists) throws Exception {
        ComputeService hello = null;
        try {
            hello =
                    (ComputeService) getService(ComputeService.class.getName(),
                                                "(duraKey=helloVal)");
        } catch (Exception e) {
        }

        boolean found = (null != hello);
        assertEquals(exists, found);

        if (exists) {
            assertNotNull(hello.describe());
        }
    }

    //    private String getBundleHome() throws Exception {
    //        String home = getInstaller().getBundleHome();
    //        assertNotNull(home);
    //        log.debug("serviceadmin bundle-home: '" + home + "'");
    //        return home;
    //    }
    //
    //    private ServiceInstaller getInstaller() throws Exception {
    //        if (installer == null) {
    //            installer =
    //                    (ServiceInstaller) getService(ServiceInstaller.class
    //                            .getName());
    //        }
    //        assertNotNull(installer);
    //        return installer;
    //    }
    //
    //    private Object getService(String serviceInterface) throws Exception {
    //        return getService(serviceInterface, null);
    //    }

    private Object getService(String serviceInterface, String filter)
            throws Exception {
        ServiceReference[] refs =
                bundleContext.getServiceReferences(serviceInterface, filter);

        if (refs == null || refs.length == 0) {
            String msg = "Unable to find service: " + serviceInterface;
            log.warn(msg);
            throw new Exception(msg);
        }

        return bundleContext.getService(refs[0]);
    }

    private boolean serviceFound(String serviceName, String targetName) {
        return serviceName.contains(targetName);
    }

    private File getTestBundleFile() {
        File bundle = new File("src/test/resources/" + TEST_BUNDLE_FILE_NAME);
        Assert.assertTrue(bundle.exists());

        return bundle;
    }

    //    private void deleteTestBundle(String home) {
    //        File file = new File(home + File.separator + TEST_BUNDLE_FILE_NAME);
    //        file.delete();
    //    }

    private ServiceUploadClient getClient() {
        if (client == null) {
            client = new ServiceUploadClient();
            client.setRester(new RestHttpHelper());
            client.setBaseURL(BASE_URL);
        }
        return client;
    }

    public ServiceSerializer getSerializer() {
        if (serializer == null) {
            serializer = new XMLServiceSerializerImpl();
        }
        return serializer;
    }
}
