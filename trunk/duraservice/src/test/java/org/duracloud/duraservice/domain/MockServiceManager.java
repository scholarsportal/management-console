package org.duracloud.duraservice.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Map;

import org.apache.commons.httpclient.util.HttpURLConnection;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.domain.Content;
import org.duracloud.domain.Space;
import org.duracloud.servicesutil.client.ServiceUploadClient;


/**
 * Mock Service Manager for testing
 *
 * @author Bill Branan
 */
public class MockServiceManager
        extends ServiceManager {

    public static final String SERVICE_PACKAGE_1 = "servicePackage1";
    public static final String SERVICE_PACKAGE_2 = "servicePackage2";
    public static final String SERVICE_PACKAGE_3 = "servicePackage3";

    @Override
    protected void initializeServicesList(ServiceStore serviceStore)
    throws ContentStoreException {
        ContentStore mockStore = new MockContentStore();
        Space space = mockStore.getSpace(serviceStore.getSpaceId());
        setContentStore(mockStore);
        setServicesList(space.getContentIds());
    }

    @Override
    protected ServiceUploadClient getServicesAdmin(String instanceHost)
    throws ServiceException {
        return new MockServiceUploadClient();
    }

    private class MockContentStore extends ContentStore {

        public MockContentStore() {
            super(null, null, null);
        }

        @Override
        public Space getSpace(String spaceId) throws ContentStoreException {
            Space mockSpace = new Space();
            mockSpace.addContentId(SERVICE_PACKAGE_1);
            mockSpace.addContentId(SERVICE_PACKAGE_2);
            mockSpace.addContentId(SERVICE_PACKAGE_3);
            return mockSpace;
        }

        @Override
        public Content getContent(String spaceId, String contentId) {
            Content mockContent = new Content();
            mockContent.setStream(new ByteArrayInputStream("servicePackage".getBytes()));
            return mockContent;
        }
    }

    private class MockServiceUploadClient extends ServiceUploadClient {

        RestHttpHelper.HttpResponse response =
            new RestHttpHelper().
            new HttpResponse(HttpURLConnection.HTTP_OK, null, null, null);

        public HttpResponse postServiceBundle(InputStream stream) throws Exception {
            return response;
        }

        public HttpResponse deleteServiceBundle(String bundleId) throws Exception {
            return response;
        }

        public void postServiceConfig(String configId, Map<String, String> config)
        throws Exception {

        }
    }
}
