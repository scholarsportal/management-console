package org.duracloud.duraservice.config;

import org.apache.commons.io.FileUtils;
import org.duracloud.duraservice.mgmt.ServiceConfigUtil;
import org.duracloud.serviceconfig.DeploymentOption;
import org.duracloud.serviceconfig.ServiceInfo;
import org.duracloud.serviceconfig.ServicesConfigDocument;
import org.duracloud.serviceconfig.SystemConfig;
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
        servicesList.add(buildWebappUtilService());
        servicesList.add(buildHelloWebappWrapper());
        servicesList.add(buildJ2kService());
        servicesList.add(buildImageConversionService());
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

        /* These features have not been implemented as part of the service yet.         
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
        */

        repServiceUserConfig.add(fromStoreId);
        repServiceUserConfig.add(toStoreId);
        /*
        repServiceUserConfig.add(repType);
        repServiceUserConfig.add(repSpaces);
        repServiceUserConfig.add(repMimeTypes);
        */

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
        imService.setMaxDeploymentsAllowed(1);

        imService.setDeploymentOptions(getSimpleDeploymentOptions());

        return imService;
    }

    private ServiceInfo buildWebappUtilService() {
        ServiceInfo hellowebappService = new ServiceInfo();
        hellowebappService.setId(3);
        hellowebappService.setContentId("webapputilservice-1.0.0.zip");
        String desc = "The Web App Utility service coordinates the " +
            "(de)installation and startup/shutdown of Apache Tomcat instances" +
            " that are created to run web application services that deployed " +
            "externally to the hosting OSGi container.";
        hellowebappService.setDescription(desc);
        hellowebappService.setDisplayName("Web App Utility Service");
        hellowebappService.setUserConfigVersion("1.0");
        hellowebappService.setServiceVersion("1.0.0");
        hellowebappService.setMaxDeploymentsAllowed(1);

        hellowebappService.setDeploymentOptions(getSimpleDeploymentOptions());

        return hellowebappService;
    }
    
    private ServiceInfo buildHelloWebappWrapper() {
        ServiceInfo hellowebapp = new ServiceInfo();
        hellowebapp.setId(4);
        hellowebapp.setContentId("hellowebappwrapper-1.0.0.zip");
        String desc = "The HelloWebApp wrapper deploys a simple web " +
            "application which prints a pleasant greeting.";
        hellowebapp.setDescription(desc);
        hellowebapp.setDisplayName("Hello WebApp Wrapper");
        hellowebapp.setUserConfigVersion("1.0");
        hellowebapp.setServiceVersion("1.0.0");
        hellowebapp.setMaxDeploymentsAllowed(1);

        hellowebapp.setDeploymentOptions(getSimpleDeploymentOptions());

        return hellowebapp;
    }

    private ServiceInfo buildJ2kService() {
        ServiceInfo j2kService = new ServiceInfo();
        j2kService.setId(5);
        j2kService.setContentId("j2kservice-1.0.0.zip");
        String desc =
            "The J2K service deploys an instance of the Adore Djatoka web " +
                "application which provides for serving and viewing JPEG2000 " +
                "images.";
        j2kService.setDescription(desc);
        j2kService.setDisplayName("J2K Service");
        j2kService.setUserConfigVersion("1.0");
        j2kService.setServiceVersion("1.0.0");
        j2kService.setMaxDeploymentsAllowed(1);

        j2kService.setDeploymentOptions(getSimpleDeploymentOptions());

        return j2kService;
    }

    private ServiceInfo buildImageConversionService() {
        ServiceInfo icService = new ServiceInfo();
        icService.setId(6);
        icService.setContentId("imageconversionservice-1.0.0.zip");
        String desc = "The Image Conversion service provides a simple way to " +
            "convert image files from one format to another. A space is " +
            "selected from which image files will be read and converted to " +
            "the chosen format. The converted image files will be stored in " +
            "the destination space along with a file which details the " +
            "results of the conversion process. Note that the ImageMagick " +
            "service must be deployed prior to using the Image Conversion " +
            "service";
        icService.setDescription(desc);
        icService.setDisplayName("Image Conversion Service");
        icService.setUserConfigVersion("1.0");
        icService.setServiceVersion("1.0.0");
        icService.setMaxDeploymentsAllowed(-1);

        // User Configs
        List<UserConfig> icServiceUserConfig = new ArrayList<UserConfig>();

        // Source space
        List<Option> spaceOptions = new ArrayList<Option>();
        Option spaces =
            new Option("Spaces", ServiceConfigUtil.SPACES_VAR, false);
        spaceOptions.add(spaces);

        SingleSelectUserConfig sourceSpace =
            new SingleSelectUserConfig("sourceSpaceId",
                                       "Source Space",
                                       spaceOptions);

        TextUserConfig destSpace =
            new TextUserConfig("destSpaceId",
                               "Destination Space, a space which does not " +
                                   "already exist will be created.", "");

        // To Format
        List<Option> toFormatOptions = new ArrayList<Option>();
        Option gif =  new Option("GIF", "gif", false);
        Option jpg =  new Option("JPG", "jpg", false);
        Option png =  new Option("PNG", "png", false);
        Option tiff =  new Option("TIFF", "tiff", false);
        Option jp2 =  new Option("JP2", "jp2", false);
        Option bmp =  new Option("BMP", "bmp", false);
        Option pdf =  new Option("PDF", "pdf", false);
        Option psd =  new Option("PSD", "psd", false);
        toFormatOptions.add(gif);
        toFormatOptions.add(jpg);
        toFormatOptions.add(png);
        toFormatOptions.add(tiff);
        toFormatOptions.add(jp2);
        toFormatOptions.add(bmp);
        toFormatOptions.add(pdf);
        toFormatOptions.add(psd);

        SingleSelectUserConfig toFormat =
            new SingleSelectUserConfig("toFormat",
                                       "Destination Format",
                                       toFormatOptions);

        // Name Prefix
        TextUserConfig namePrefix =
            new TextUserConfig("namePrefix",
                               "Source file name prefix, only files " +
                               "beginning with this value will be converted.",
                               "");

        // Name Suffix
        TextUserConfig nameSuffix =
            new TextUserConfig("nameSuffix",
                               "Source file name suffix, only files ending " +
                               "with this value will be converted.",
                               "");

        icServiceUserConfig.add(sourceSpace);
        icServiceUserConfig.add(destSpace);
        icServiceUserConfig.add(toFormat);
        icServiceUserConfig.add(namePrefix);
        icServiceUserConfig.add(nameSuffix);

        icService.setUserConfigs(icServiceUserConfig);

        // System Configs
        List<SystemConfig> systemConfig = new ArrayList<SystemConfig>();

        SystemConfig host =
            new SystemConfig("duraStoreHost", "$DURASTORE-HOST", "localhost");
        SystemConfig port =
            new SystemConfig("duraStorePort", "$DURASTORE-PORT", "8080");
        SystemConfig context =
            new SystemConfig("duraStoreContext", "$DURASTORE-CONTEXT", "durastore");

        systemConfig.add(host);
        systemConfig.add(port);
        systemConfig.add(context);

        icService.setSystemConfigs(systemConfig);

        icService.setDeploymentOptions(getSimpleDeploymentOptions());

        return icService;
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
