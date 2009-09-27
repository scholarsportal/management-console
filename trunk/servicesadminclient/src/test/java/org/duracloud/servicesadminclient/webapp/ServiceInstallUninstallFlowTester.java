package org.duracloud.servicesadminclient.webapp;

import java.io.File;

import java.util.List;

import org.apache.commons.httpclient.util.HttpURLConnection;

import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.services.beans.ComputeServiceBean;
import org.duracloud.services.util.ServiceSerializer;
import org.duracloud.services.util.XMLServiceSerializerImpl;
import org.duracloud.servicesadminclient.ServicesAdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ServiceInstallUninstallFlowTester {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final File testBundle;

    private final ServicesAdminClient client;

    private ServiceSerializer serializer;

    public ServiceInstallUninstallFlowTester(File testBundle,
                                             ServicesAdminClient client) {
        Assert.assertNotNull(testBundle);
        Assert.assertTrue(testBundle.exists());
        Assert.assertNotNull(client);

        this.testBundle = testBundle;
        this.client = client;
    }

    public void testNewServiceFlow() throws Exception {
        // Allow tomcat to come up.
        Thread.sleep(5000);

        // check new service does not exist
        verifyTestServiceIsListed(false);

        // install service
        installTestBundle();

        // Allow test-service to come up.
        Thread.sleep(5000);

        // check new service exists and available in container
        verifyTestServiceIsListed(true);

        // uninstall service
        uninstallTestBundle();

        // Allow test-service to go down.
        Thread.sleep(5000);

        // check new service does not exist
        verifyTestServiceIsListed(false);
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

    private File getTestBundleFile() {
        return testBundle;
    }

    private ServicesAdminClient getClient() {
        return client;
    }

    private ServiceSerializer getSerializer() {
        if (serializer == null) {
            serializer = new XMLServiceSerializerImpl();
        }
        return serializer;
    }
}
