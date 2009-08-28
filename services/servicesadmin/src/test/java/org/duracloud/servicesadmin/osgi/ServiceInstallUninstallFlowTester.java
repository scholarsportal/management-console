
package org.duracloud.servicesadmin.osgi;

import java.io.File;

import java.util.List;

import org.apache.commons.httpclient.util.HttpURLConnection;

import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.services.ComputeService;
import org.duracloud.servicesutil.beans.ComputeServiceBean;
import org.duracloud.servicesutil.client.ServiceUploadClient;
import org.duracloud.servicesutil.util.ServiceSerializer;
import org.duracloud.servicesutil.util.XMLServiceSerializerImpl;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ServiceInstallUninstallFlowTester {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final BundleContext bundleContext;

    private final File testBundle;

    private final ServiceUploadClient client;

    private ServiceSerializer serializer;

    public ServiceInstallUninstallFlowTester(BundleContext bundleContext,
                                             File testBundle,
                                             ServiceUploadClient client) {
        Assert.assertNotNull(bundleContext);
        Assert.assertNotNull(testBundle);
        Assert.assertTrue(testBundle.exists());
        Assert.assertNotNull(client);

        this.bundleContext = bundleContext;
        this.testBundle = testBundle;
        this.client = client;
    }

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

        if (log.isDebugEnabled()) {
            log.debug(AbstractServicesAdminOSGiTestBasePax
                    .inspectBundlesText(bundleContext));
        }

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
                getClient().deleteServiceBundle(testBundle.getName());
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
        boolean testServiceFound = false;
        for (ComputeServiceBean bean : beans) {
            String serviceDesc = bean.getServiceName();
            log.debug("dura-service: " + serviceDesc);

            if (!testServiceFound) {
                testServiceFound =
                        TestServiceAdminWepApp.testServiceFound(serviceDesc);
            }
        }
        assertEquals(exists, testServiceFound);

    }

    private void verifyTestServiceIsInstalled(boolean exists) throws Exception {
        ComputeService testService = null;
        try {
            testService = TestServiceAdminWepApp.getTestService(bundleContext);
        } catch (Exception e) {
        }

        boolean found = (null != testService);
        assertEquals(exists, found);

        if (exists) {
            assertNotNull(testService.describe());
        }
    }

    private File getTestBundleFile() {
        return testBundle;
    }

    private ServiceUploadClient getClient() {
        return client;
    }

    private ServiceSerializer getSerializer() {
        if (serializer == null) {
            serializer = new XMLServiceSerializerImpl();
        }
        return serializer;
    }
}
