package org.duracloud.duraservice.config;

import org.apache.commons.io.FileUtils;
import org.duracloud.duraservice.mgmt.ServiceConfigUtil;
import org.duracloud.serviceconfig.DeploymentOption;
import org.duracloud.serviceconfig.ServiceInfo;
import org.duracloud.serviceconfig.ServicesConfigDocument;
import org.duracloud.serviceconfig.SystemConfig;
import org.duracloud.serviceconfig.user.MultiSelectUserConfig;
import org.duracloud.serviceconfig.user.Option;
import org.duracloud.serviceconfig.user.SingleSelectUserConfig;
import org.duracloud.serviceconfig.user.TextUserConfig;
import org.duracloud.serviceconfig.user.UserConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Bill Branan
 * Date: Nov 20, 2009
 */
public class ServiceXmlGenerator {

    private static final String SERVICES_XML_FILE_NAME =
        "duracloud-service-repository.xml";

    private List<ServiceInfo> buildServiceList() {
        List<ServiceInfo> servicesList = new ArrayList<ServiceInfo>();
        servicesList.add(buildHelloService());
        servicesList.add(buildReplicationService());
        servicesList.add(buildImageMagickService());
        return servicesList;
    }

    private ServiceInfo buildHelloService() {
        ServiceInfo helloService = new ServiceInfo();
        helloService.setId(0);
        helloService.setContentId("helloservice-1.0.0.jar");
        String desc = "The Hello service acts as a simple test case " +
                      "for service deployment.";
        helloService.setDescription(desc);
        helloService.setDisplayName("Hello Service");
        helloService.setUserConfigVersion("1.0");
        helloService.setServiceVersion("1.0.0");
        helloService.setMaxDeploymentsAllowed(-1);

        helloService.setDeploymentOptions(getSimpleDeploymentOptions());

        return helloService;
    }

    private ServiceInfo buildReplicationService() {
        ServiceInfo repService = new ServiceInfo();
        repService.setId(1);
        repService.setContentId("replicationservice-1.0.0.zip");
        String desc = "The Replication service provides a simple mechanism " +
            "for synchronizing your content between two storage providers. A " +
            "running replication service will listen for updates which occur " +
            "in one store and duplicate those activities in another store.";
        repService.setDescription(desc);
        repService.setDisplayName("Replication Service");
        repService.setUserConfigVersion("1.0");
        repService.setServiceVersion("1.0.0");
        repService.setMaxDeploymentsAllowed(-1);

        // User Configs
        List<UserConfig> repServiceUserConfig = new ArrayList<UserConfig>();

        // Store Options (from/to)
        List<Option> storeOptions = new ArrayList<Option>();
        Option stores =
            new Option("Stores", ServiceConfigUtil.STORES_VAR, false);
        storeOptions.add(stores);

        SingleSelectUserConfig fromStoreId =
            new SingleSelectUserConfig("fromStoreId",
                                       "Replicate from this store",
                                       storeOptions);

        SingleSelectUserConfig toStoreId =
            new SingleSelectUserConfig("toStoreId",
                                       "Replicate to this store",
                                       storeOptions);

        // Replication Type
        List<Option> repTypeOptions = new ArrayList<Option>();
        Option repType1 =
            new Option("Sync Current Content", "1", false);
        Option repType2 =
            new Option("Replicate on Update", "2", false);
        Option repType3 =
            new Option("Sync Current Content then Replicate On Update",
                       "3",
                       false);
        repTypeOptions.add(repType1);
        repTypeOptions.add(repType2);
        repTypeOptions.add(repType3);

        SingleSelectUserConfig repType =
            new SingleSelectUserConfig("replicationType",
                                       "Replicataion Style",
                                       repTypeOptions);

        // Replicate spaces filter
        List<Option> spaceOptions = new ArrayList<Option>();
        Option spaces =
            new Option("Spaces", ServiceConfigUtil.SPACES_VAR, false);
        spaceOptions.add(spaces);

        MultiSelectUserConfig repSpaces =
            new MultiSelectUserConfig("replicateSpaces",
                                      "Only replicate content in these spaces",
                                      spaceOptions);

        // Mime type filter
        TextUserConfig repMimeTypes =
            new TextUserConfig("replicateMimetypes",
                               "Only replicate content with these MIME " +
                                   "types (separate with commas)", "");

        repServiceUserConfig.add(fromStoreId);
        repServiceUserConfig.add(toStoreId);
        repServiceUserConfig.add(repType);
        repServiceUserConfig.add(repSpaces);
        repServiceUserConfig.add(repMimeTypes);

        repService.setUserConfigs(repServiceUserConfig);

        // System Configs
        List<SystemConfig> systemConfig = new ArrayList<SystemConfig>();

        SystemConfig host =
            new SystemConfig("host", "$DURASTORE-HOST", "localhost");
        SystemConfig port =
            new SystemConfig("port", "$DURASTORE-PORT", "8080");
        SystemConfig context =
            new SystemConfig("context", "$DURASTORE-CONTEXT", "durastore");
        SystemConfig brokerURL =
            new SystemConfig("brokerURL",
                             "$MESSAGE-BROKER-URL",
                             "tcp://localhost:61617");

        systemConfig.add(host);
        systemConfig.add(port);
        systemConfig.add(context);
        systemConfig.add(brokerURL);

        repService.setSystemConfigs(systemConfig);

        repService.setDeploymentOptions(getSimpleDeploymentOptions());

        return repService;
    }

    private ServiceInfo buildImageMagickService() {
        ServiceInfo imService = new ServiceInfo();
        imService.setId(2);
        imService.setContentId("imagemagickservice-1.0.0.zip");
        String desc = "The ImageMagick service deploys the ImageMagick " +
            "application which allows other services to take advantage of " +
            "its features.";
        imService.setDescription(desc);
        imService.setDisplayName("ImageMagick Service");
        imService.setUserConfigVersion("1.0");
        imService.setServiceVersion("1.0.0");
        imService.setMaxDeploymentsAllowed(-1);

        imService.setDeploymentOptions(getSimpleDeploymentOptions());

        return imService;
    }

    private List<DeploymentOption> getSimpleDeploymentOptions() {
        // Deployment Options
        DeploymentOption depPrimary = new DeploymentOption();
        depPrimary.setLocation(DeploymentOption.Location.PRIMARY);
        depPrimary.setState(DeploymentOption.State.AVAILABLE);

        DeploymentOption depNew = new DeploymentOption();
        depNew.setLocation(DeploymentOption.Location.NEW);
        depNew.setState(DeploymentOption.State.UNAVAILABLE);

        DeploymentOption depExisting = new DeploymentOption();
        depExisting.setLocation(DeploymentOption.Location.EXISTING);
        depExisting.setState(DeploymentOption.State.UNAVAILABLE);

        List<DeploymentOption> depOptions = new ArrayList<DeploymentOption>();
        depOptions.add(depPrimary);
        depOptions.add(depNew);
        depOptions.add(depExisting);

        return depOptions;
    }

    private String getServicesListAsXml() {
        List<ServiceInfo> services = buildServiceList();
        ServicesConfigDocument configDoc = new ServicesConfigDocument();
        return configDoc.getServiceListAsXML(services);
    }

    /**
     * Creates service xml and writes it to a file in the directory
     * indicated by dirPath.
     *
     * @param dirPath the full path of the directory in which to write the file
     * @throws IOException
     */
    public void generateServiceXml(String dirPath) throws IOException {
        String filePath = dirPath + File.separator + SERVICES_XML_FILE_NAME;
        System.out.println("Writing Services Xml File to: " + filePath);
        File servicesXmlFile = new File(filePath);
        FileUtils.writeStringToFile(servicesXmlFile,
                                    getServicesListAsXml(),
                                    "UTF-8");
    }
    
    public static void main(String[] args) throws Exception {
        String currentDir = new File(".").getCanonicalPath();
        ServiceXmlGenerator xmlGenerator = new ServiceXmlGenerator();
        xmlGenerator.generateServiceXml(currentDir);
    }

}
