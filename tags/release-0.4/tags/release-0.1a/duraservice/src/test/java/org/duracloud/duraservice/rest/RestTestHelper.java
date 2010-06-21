package org.duracloud.duraservice.rest;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.duraservice.config.DuraServiceConfig;

/**
 * @author Bill Branan
 */
public class RestTestHelper {

    private static String configFileName = "test-duraservice.properties";
    static {
        DuraServiceConfig.setConfigFileName(configFileName);
    }

    private static RestHttpHelper restHelper = new RestHttpHelper();

    private static String baseUrl;

    private static String host = "http://localhost";

    private static String port;
    private static final String defaultPort = "8080";

    private static String webapp = "duraservice";

    private static String initXml = null;

    public static final String SPACE_ACCESS = "OPEN";

    public static HttpResponse initialize() throws Exception {
        String url = getBaseUrl() + "/services";
        if(initXml == null) {
            initXml = buildTestInitXml();
        }
        return restHelper.post(url, initXml, null);
    }

    public static String getBaseUrl() throws Exception {
        if (baseUrl == null) {
            baseUrl = host + ":" + getPort() + "/" + webapp;
        }
        return baseUrl;
    }

    private static String getPort() throws Exception {
        if (port == null) {
            port = DuraServiceConfig.getPort();
        }

        try { // Ensure the port is a valid port value
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            port = defaultPort;
        }

        return port;
    }

    public static String buildTestInitXml() throws Exception {
        String serviceStorageHost = "localhost";
        String serviceStoragePort = getPort();
        String serviceStorageContext = "durastore";
        String serviceStorageSpaceId = "duracloud-service-repository";

        String serviceComputeMgrType = "AMAZON_EC2";
        String serviceComputeMgrImage = "1234";
        String serviceComputeMgrUser = "username";
        String serviceComputeMgrPass = "password";

        StringBuilder xml = new StringBuilder();
        xml.append("<servicesConfig>");
          xml.append("<serviceStorage>");
            xml.append("<host>"+serviceStorageHost+"</host>");
            xml.append("<port>"+serviceStoragePort+"</port>");
            xml.append("<context>"+serviceStorageContext+"</context>");
            xml.append("<spaceId>"+serviceStorageSpaceId+"</spaceId>");
          xml.append("</serviceStorage>");
          xml.append("<serviceCompute>");
            xml.append("<type>"+serviceComputeMgrType+"</type>");
            xml.append("<imageId>"+serviceComputeMgrImage+"</imageId>");
            xml.append("<computeProviderCredential>");
              xml.append("<username>"+serviceComputeMgrUser+"</username>");
              xml.append("<password>"+serviceComputeMgrPass+"</password>");
            xml.append("</computeProviderCredential>");
          xml.append("</serviceCompute>");
        xml.append("</servicesConfig>");
        return xml.toString();
    }

    public static String buildTestServiceConfigXml() throws Exception {
        String prop1Name = "property1";
        String prop1Value = "value1";
        String prop2Name = "property2";
        String prop2Value = "value2";

        StringBuilder xml = new StringBuilder();
        xml.append("<serviceConfig>");
          xml.append("<configItem>");
            xml.append("<name>"+prop1Name+"</name>");
            xml.append("<value>"+prop1Value+"</value>");
          xml.append("</configItem>");
          xml.append("<configItem>");
            xml.append("<name>"+prop2Name+"</name>");
            xml.append("<value>"+prop2Value+"</value>");
          xml.append("</configItem>");
        xml.append("</serviceConfig>");
        return xml.toString();
    }

}
