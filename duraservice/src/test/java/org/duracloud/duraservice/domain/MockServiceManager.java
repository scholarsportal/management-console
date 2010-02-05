package org.duracloud.duraservice.domain;

import org.apache.commons.httpclient.util.HttpURLConnection;
import org.duracloud.client.ContentStore;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.domain.Content;
import org.duracloud.domain.Space;
import org.duracloud.duraservice.error.NoSuchServiceComputeInstanceException;
import org.duracloud.duraservice.mgmt.ServiceConfigUtil;
import org.duracloud.duraservice.mgmt.ServiceManager;
import org.duracloud.error.ContentStoreException;
import org.duracloud.error.InvalidIdException;
import org.duracloud.serviceconfig.DeploymentOption;
import org.duracloud.serviceconfig.ServiceInfo;
import org.duracloud.serviceconfig.SystemConfig;
import org.duracloud.serviceconfig.user.MultiSelectUserConfig;
import org.duracloud.serviceconfig.user.Option;
import org.duracloud.serviceconfig.user.SingleSelectUserConfig;
import org.duracloud.serviceconfig.user.TextUserConfig;
import org.duracloud.serviceconfig.user.UserConfig;
import org.duracloud.servicesadminclient.ServicesAdminClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Mock Service Manager for testing
 *
 * @author Bill Branan
 */
public class MockServiceManager extends ServiceManager {

    private List<ServiceInfo> serviceList;
    public ServiceInfo service1;
    public ServiceInfo service2;
    public ServiceInfo service3;

    public MockServiceManager() {
        buildService1();
        buildService2();
        buildService3();

        serviceList = new ArrayList<ServiceInfo>();
        serviceList.add(service1);
        serviceList.add(service2);
        serviceList.add(service3);
    }

    private void buildService1() {
        service1 = new ServiceInfo();
        service1.setId(1);
        service1.setContentId("service1.zip");
        service1.setDescription("Service 1 Description");
        service1.setDisplayName("Service 1");
        service1.setUserConfigVersion("1.0");
        service1.setMaxDeploymentsAllowed(-1);

        List<UserConfig> service1UserConfig = new ArrayList<UserConfig>();
        TextUserConfig config1 =
            new TextUserConfig("config1", "Config 1", "Config Value");

        List<Option> config2ops = new ArrayList<Option>();
        Option config2op1 = new Option("Config 2 Option 1", "config2op1", false);
        Option config2op2 = new Option("Config 2 Option 2", "config2op2", false);
        config2ops.add(config2op1);
        config2ops.add(config2op2);
        SingleSelectUserConfig config2 =
            new SingleSelectUserConfig("config2", "Config 2", config2ops);

        List<Option> config3ops = new ArrayList<Option>();
        Option config3op1 = new Option("Config 3 Option 1", "config3op1", false);
        Option config3op2 = new Option("Config 3 Option 2", "config3op2", false);
        Option config3op3 = new Option("Config 3 Option 3", "config3op3", false);
        config3ops.add(config3op1);
        config3ops.add(config3op2);
        config3ops.add(config3op3);
        MultiSelectUserConfig config3 =
            new MultiSelectUserConfig("config3", "Config 3", config3ops);

        service1UserConfig.add(config1);
        service1UserConfig.add(config2);
        service1UserConfig.add(config3);

        service1.setUserConfigs(service1UserConfig);

        List<SystemConfig> systemConfig = new ArrayList<SystemConfig>();
        systemConfig.add(new SystemConfig("sysConfig1", null, "default"));
        service1.setSystemConfigs(systemConfig);

        DeploymentOption depOp = new DeploymentOption();
        depOp.setLocation(DeploymentOption.Location.PRIMARY);
        depOp.setState(DeploymentOption.State.AVAILABLE);
        List<DeploymentOption> depOptions = new ArrayList<DeploymentOption>();
        depOptions.add(depOp);

        service1.setDeploymentOptions(depOptions);
    }

    private void buildService2() {
        service2 = new ServiceInfo();
        service2.setId(2);
        service2.setContentId("service2.zip");
        service2.setDescription("Service 2 Description");
        service2.setDisplayName("Service 2");
        service2.setUserConfigVersion("1.0");
        service2.setMaxDeploymentsAllowed(-1);

        List<UserConfig> service2UserConfig = new ArrayList<UserConfig>();
        TextUserConfig config1 =
            new TextUserConfig("config1", "Config 1", "Config Value");

        List<Option> config2ops = new ArrayList<Option>();
        Option config2op =
            new Option("Stores", ServiceConfigUtil.STORES_VAR, false);
        config2ops.add(config2op);
        SingleSelectUserConfig config2 =
            new SingleSelectUserConfig("config2", "Config 2", config2ops);

        List<Option> config3ops = new ArrayList<Option>();
        Option config3op =
            new Option("Spaces", ServiceConfigUtil.SPACES_VAR, false);
        config3ops.add(config3op);
        MultiSelectUserConfig config3 =
            new MultiSelectUserConfig("config3", "Config 3", config3ops);

        service2UserConfig.add(config1);
        service2UserConfig.add(config2);
        service2UserConfig.add(config3);

        service2.setUserConfigs(service2UserConfig);

        List<SystemConfig> systemConfig = new ArrayList<SystemConfig>();
        systemConfig.add(
            new SystemConfig("sysConfig1", "$DURASTORE-HOST", "default"));

        service2.setSystemConfigs(systemConfig);

        DeploymentOption depOp1 = new DeploymentOption();
        depOp1.setLocation(DeploymentOption.Location.PRIMARY);
        depOp1.setState(DeploymentOption.State.UNAVAILABLE);
        DeploymentOption depOp2 = new DeploymentOption();
        depOp2.setLocation(DeploymentOption.Location.NEW);
        depOp2.setState(DeploymentOption.State.AVAILABLE);
        List<DeploymentOption> depOptions = new ArrayList<DeploymentOption>();
        depOptions.add(depOp1);
        depOptions.add(depOp2);

        service2.setDeploymentOptions(depOptions);
    }

    private void buildService3() {
        service3 = new ServiceInfo();
        service3.setId(3);
        service3.setContentId("service3.zip");
        service3.setDescription("Service 3 Description");
        service3.setDisplayName("Service 3");
        service3.setUserConfigVersion("1.0");
        service3.setMaxDeploymentsAllowed(-1);

        List<UserConfig> service3UserConfig = new ArrayList<UserConfig>();
        service3.setUserConfigs(service3UserConfig);

        DeploymentOption depOp1 = new DeploymentOption();
        depOp1.setLocation(DeploymentOption.Location.PRIMARY);
        depOp1.setState(DeploymentOption.State.UNAVAILABLE);
        DeploymentOption depOp2 = new DeploymentOption();
        depOp2.setLocation(DeploymentOption.Location.NEW);
        depOp2.setState(DeploymentOption.State.UNAVAILABLE);
        DeploymentOption depOp3 = new DeploymentOption();
        depOp3.setLocation(DeploymentOption.Location.EXISTING);
        depOp3.setState(DeploymentOption.State.UNAVAILABLE);
        List<DeploymentOption> depOptions = new ArrayList<DeploymentOption>();
        depOptions.add(depOp1);
        depOptions.add(depOp2);
        depOptions.add(depOp3);

        service3.setDeploymentOptions(depOptions);
    }

    @Override
    protected void initializeServicesList()
    throws ContentStoreException {
        ContentStore mockStore = new MockContentStore();
        setServiceContentStore(mockStore);       
        setServicesList(serviceList);
    }

    @Override
    protected ServiceComputeInstance getServiceComputeInstanceByHostName(String hostName)
        throws NoSuchServiceComputeInstanceException {
        if(hostName.equals(primaryHost)) {
            return new ServiceComputeInstance(primaryHost,
                                              PRIMARY_HOST_DISPLAY,
                                              new MockServicesAdminClient());
        } else {
            return super.getServiceComputeInstanceByHostName(hostName);
        }
    }

    @Override
    protected void refreshServicesList() {
        // Do nothing
    }

    private class MockContentStore implements ContentStore {

        public Space getSpace(String spaceId,
                              String prefix,
                              long maxResults,
                              String marker)
            throws ContentStoreException {
            Space mockSpace = new Space();
            mockSpace.addContentId("service1.zip");
            mockSpace.addContentId("service2.zip");
            return mockSpace;
        }

        public Iterator<String> getSpaceContents(String spaceId)
            throws ContentStoreException {
            // Auto-generated method stub
            return null;
        }

        public Iterator<String> getSpaceContents(String spaceId, String prefix)
            throws ContentStoreException {
            // Auto-generated method stub
            return null;
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

        public void validateSpaceId(String spaceId) throws InvalidIdException {
            // Auto-generated method stub

        }

        public void validateContentId(String contentId)
            throws InvalidIdException {
            // Auto-generated method stub
        }
    }

    private class MockServicesAdminClient extends ServicesAdminClient {

        RestHttpHelper.HttpResponse response = new RestHttpHelper().
            new HttpResponse(HttpURLConnection.HTTP_OK, null, null, null);

        @Override
        public HttpResponse postServiceBundle(String fileName,
                                              InputStream stream,
                                              long length) throws Exception {
            return response;
        }

        @Override
        public HttpResponse postServiceBundle(File file) throws Exception {
            return response;
        }

        @Override
        public HttpResponse deleteServiceBundle(String bundleId)
            throws Exception {
            return response;
        }

        @Override
        public HttpResponse postServiceConfig(String configId,
                                              Map<String, String> config)
            throws Exception {
            return response;
        }

        @Override
        public HttpResponse startServiceBundle(String bundleId)
            throws Exception {
            return response;
        }

        @Override
        public HttpResponse stopServiceBundle(String bundleId)
            throws Exception {
            return response;
        }

        @Override
        public boolean isServiceDeployed(String bundleId) throws Exception {
            return true;
        }

        @Override
        public Map<String, String> getServiceProps(String serviceId) {
            return new HashMap<String, String>();
        }
    }
}
