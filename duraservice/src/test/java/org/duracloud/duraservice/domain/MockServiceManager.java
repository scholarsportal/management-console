package org.duracloud.duraservice.domain;

import org.apache.commons.httpclient.util.HttpURLConnection;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.domain.Content;
import org.duracloud.domain.Space;
import org.duracloud.serviceconfig.MultiSelectUserConfig;
import org.duracloud.serviceconfig.Option;
import org.duracloud.serviceconfig.ServiceInfo;
import org.duracloud.serviceconfig.SingleSelectUserConfig;
import org.duracloud.serviceconfig.TextUserConfig;
import org.duracloud.serviceconfig.UserConfig;
import org.duracloud.servicesadminclient.ServicesAdminClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Mock Service Manager for testing
 *
 * @author Bill Branan
 */
public class MockServiceManager extends ServiceManager {

    public static ServiceInfo service1;

    public MockServiceManager() {
        buildService1();

    }

    private void buildService1() {
        service1 = new ServiceInfo();
        service1.setId("1");
        service1.setContentId("service1-1.0.0.zip");
        service1.setDescription("Service 1 Description");
        service1.setDisplayName("Service 1");

        List<UserConfig> service1UserConfig = new ArrayList<UserConfig>();
        TextUserConfig config1 =
            new TextUserConfig("config1", "Config 1", true);

        List<Option> config2ops = new ArrayList<Option>();
        Option config2op1 = new Option("Config 2 Option 1", "config2op1", false);
        Option config2op2 = new Option("Config 2 Option 2", "config2op2", false);
        config2ops.add(config2op1);
        config2ops.add(config2op2);
        SingleSelectUserConfig config2 =
            new SingleSelectUserConfig("config2", "Config 2", true, config2ops);

        List<Option> config3ops = new ArrayList<Option>();
        Option config3op1 = new Option("Config 3 Option 1", "config3op1", false);
        Option config3op2 = new Option("Config 3 Option 2", "config3op2", false);
        Option config3op3 = new Option("Config 3 Option 3", "config3op3", false);
        config3ops.add(config3op1);
        config3ops.add(config3op2);
        config3ops.add(config3op3);
        MultiSelectUserConfig config3 =
            new MultiSelectUserConfig("config3", "Config 3", true, config3ops);

        service1UserConfig.add(config1);
        service1UserConfig.add(config2);
        service1UserConfig.add(config3);

        service1.setUserConfigs(service1UserConfig);
    }

    @Override
    protected void initializeServicesList(ServiceStore serviceStore)
    throws ContentStoreException {
        ContentStore mockStore = new MockContentStore();
        Space space = mockStore.getSpace(serviceStore.getSpaceId());
        setServiceContentStore(mockStore);
        setServicesList(space.getContentIds());
    }

    @Override
    protected ServicesAdminClient getServicesAdmin(String instanceHost)
    throws ServiceException {
        return new MockServiceUploadClient();
    }

    private class MockContentStore implements ContentStore {

        public Space getSpace(String spaceId) throws ContentStoreException {
            Space mockSpace = new Space();
            mockSpace.addContentId(SERVICE_PACKAGE_1);
            mockSpace.addContentId(SERVICE_PACKAGE_2);
            mockSpace.addContentId(SERVICE_PACKAGE_3);
            return mockSpace;
        }

        public Space getSpace(String spaceId,
                              String prefix,
                              String marker,
                              Integer maxResults) throws ContentStoreException {
            Space space = getSpace(spaceId);
            if(maxResults == null){
                maxResults = 0;
            }

            List<String> ids = space.getContentIds();
            
            int beginIndex = 0;
            
            if(marker != null && marker.length() > 0){
                beginIndex = ids.indexOf(marker)+1;
            }

            if(beginIndex == ids.size()){
                ids.clear();
            }else if(maxResults == 0){
                space.setContentIds(ids.subList(beginIndex, ids.size()));
            }else{
                int lastIndex = Math.min(beginIndex+maxResults, ids.size());
                space.setContentIds(ids.subList(beginIndex, lastIndex));
            }
            return space;
        }
        
        public Content getContent(String spaceId, String contentId) {
            Content mockContent = new Content();
            mockContent.setStream(new ByteArrayInputStream("servicePackage".getBytes()));
            return mockContent;
        }

        public String addContent(String spaceId,
                                 String contentId,
                                 InputStream content,
                                 long contentSize,
                                 String contentMimeType,
                                 Map<String, String> contentMetadata)
            throws ContentStoreException {
            // Auto-generated method stub
            return null;
        }

        public void createSpace(String spaceId,
                                Map<String, String> spaceMetadata)
            throws ContentStoreException {
            // Auto-generated method stub
        }

        public void deleteContent(String spaceId, String contentId)
            throws ContentStoreException {
            // Auto-generated method stub
        }

        public void deleteSpace(String spaceId) throws ContentStoreException {
            // Auto-generated method stub
        }

        public String getBaseURL() {
            // Auto-generated method stub
            return null;
        }

        public Map<String, String> getContentMetadata(String spaceId,
                                                      String contentId)
            throws ContentStoreException {
            // Auto-generated method stub
            return null;
        }

        public AccessType getSpaceAccess(String spaceId)
            throws ContentStoreException {
            // Auto-generated method stub
            return null;
        }

        public Map<String, String> getSpaceMetadata(String spaceId)
            throws ContentStoreException {
            // Auto-generated method stub
            return null;
        }

        public List<String> getSpaces() throws ContentStoreException {
            // Auto-generated method stub
            return null;
        }

        public String getStorageProviderType() {
            // Auto-generated method stub
            return null;
        }

        public String getStoreId() {
            // Auto-generated method stub
            return null;
        }

        public void setContentMetadata(String spaceId,
                                       String contentId,
                                       Map<String, String> contentMetadata)
            throws ContentStoreException {
            // Auto-generated method stub
        }

        public void setSpaceAccess(String spaceId, AccessType spaceAccess)
            throws ContentStoreException {
            // Auto-generated method stub
        }

        public void setSpaceMetadata(String spaceId,
                                     Map<String, String> spaceMetadata)
            throws ContentStoreException {
            // Auto-generated method stub
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

        @Override
        public boolean isServiceDeployed(String bundleId) throws Exception {
            return true;
        }
    }
}
