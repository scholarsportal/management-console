package org.duracloud.duraservice.domain;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.util.HttpURLConnection;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.domain.Content;
import org.duracloud.domain.Space;
import org.duracloud.servicesadminclient.ServicesAdminClient;


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
    protected ServicesAdminClient getServicesAdmin(String instanceHost)
    throws ServiceException {
        return new MockServiceUploadClient();
    }

    private class MockContentStore implements ContentStore {


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

		@Override
		public String addContent(String spaceId, String contentId,
				InputStream content, long contentSize, String contentMimeType,
				Map<String, String> contentMetadata)
				throws ContentStoreException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void createSpace(String spaceId,
				Map<String, String> spaceMetadata) throws ContentStoreException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void deleteContent(String spaceId, String contentId)
				throws ContentStoreException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void deleteSpace(String spaceId) throws ContentStoreException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getBaseURL() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, String> getContentMetadata(String spaceId,
				String contentId) throws ContentStoreException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public AccessType getSpaceAccess(String spaceId)
				throws ContentStoreException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, String> getSpaceMetadata(String spaceId)
				throws ContentStoreException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<String> getSpaces() throws ContentStoreException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getStorageProviderType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getStoreId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setContentMetadata(String spaceId, String contentId,
				Map<String, String> contentMetadata)
				throws ContentStoreException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setSpaceAccess(String spaceId, AccessType spaceAccess)
				throws ContentStoreException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setSpaceMetadata(String spaceId,
				Map<String, String> spaceMetadata) throws ContentStoreException {
			// TODO Auto-generated method stub
			
		}
    }

    private class MockServiceUploadClient extends ServicesAdminClient {

        RestHttpHelper.HttpResponse response =
            new RestHttpHelper().
            new HttpResponse(HttpURLConnection.HTTP_OK, null, null, null);

        @Override
        public HttpResponse postServiceBundle(String fileName,
                                              InputStream stream, long length)
                throws Exception {
            return response;
        }

        @Override
        public HttpResponse postServiceBundle(File file) throws Exception {
            return response;
        }

        @Override
        public HttpResponse deleteServiceBundle(String bundleId) throws Exception {
            return response;
        }

        @Override
        public void postServiceConfig(String configId, Map<String, String> config)
        throws Exception {

        }

        @Override
        public HttpResponse startServiceBundle(String bundleId) throws Exception {
            return response;
        }

        @Override
        public HttpResponse stopServiceBundle(String bundleId) throws Exception {
            return response;        
        }
    }
}
