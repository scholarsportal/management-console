package org.duracloud.services.webapputil;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.services.webapputil.error.WebAppDeployerException;
import org.duracloud.services.webapputil.tomcat.TomcatInstance;
import org.duracloud.services.webapputil.tomcat.TomcatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.MalformedURLException;

/**
 * This class abstracts the details of managing appservers used to host
 * DuraCloud services that run outside of an osgi-container.
 *
 * @author Andrew Woods
 *         Date: Nov 30, 2009
 */
public class WebAppUtil {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private int nextPort;
    private String baseInstallDir;
    private TomcatUtil tomcatUtil;

    private static final String AMAZON_HOST_QUERY = "http://169.254.169.254/2009-08-15//meta-data/public-ipv4";

    /**
     * This method deploys the arg war to a newly created appserver under the
     * arg serviceId context.
     *
     * @param serviceId is the name of the context of deployed webapp
     * @param war       to be deployed
     * @return URL of deployed webapp
     */
    public URL deploy(String serviceId, InputStream war) {
        int port = getNextPort();
        File installDir = getInstallDir(serviceId, port);

        TomcatInstance tomcat = getTomcatUtil().installTomcat(installDir, port);
        tomcat.start();
        tomcat.deploy(serviceId, war);

        return buildURL(port, serviceId);
    }

    private File getInstallDir(String serviceId, int port) {
        String dirName = serviceId + "-" + port;
        File installDir = new File(getBaseInstallDir(), dirName);
        if (!installDir.exists() && !installDir.mkdirs()) {
            throw new WebAppDeployerException("Error creating: " + installDir);
        }

        return installDir;
    }

    private URL buildURL(int port, String contextPath) {
        String url = getBaseURL(port) + "/" + contextPath;
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            String msg = "Error building URL for: " + contextPath + ", " + port;
            log.error(msg);
            throw new WebAppDeployerException(msg, e);
        }
    }

    private String getBaseURL(int port) {
        return "http://" + getHost() + ":" + port;
    }

    private String getHost() {
        String host = getAmazonHost();
        if (null != host) {
            return host;
        }

        host = getLocalHost();
        if (null != host) {
            return host;
        }

        throw new WebAppDeployerException("Unable to find host ip.");
    }

    private String getAmazonHost() {
        String host = null;
        RestHttpHelper httpHelper = new RestHttpHelper();
        try {
            RestHttpHelper.HttpResponse response = httpHelper.get(
                AMAZON_HOST_QUERY);
            if (null != response && response.getStatusCode() == 200) {
                host = response.getResponseBody();
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return host;
    }

    private String getLocalHost() {
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn(e.getMessage());
        }
        return host;
    }

    /**
     * This method undeploys the webapp currently deployed at the arg URL.
     *
     * @param url of the currently deployed webapp
     */
    public void unDeploy(URL url) {
        String context = getContext(url);
        int port = getPort(url);

        File installDir = getInstallDir(context, port);
        File catalinaHome = getTomcatUtil().getCatalinaHome(installDir);

        TomcatInstance tomcatInstance = new TomcatInstance(catalinaHome, port);
        tomcatInstance.unDeploy(context);
        tomcatInstance.stop();

        getTomcatUtil().unInstallTomcat(tomcatInstance);
    }

    private String getContext(URL url) {
        String context = url.getPath();
        if (null == context || context.length() == 0) {
            throw new WebAppDeployerException(
                "Context not found in url: " + url.toString());
        }
        return context;
    }

    private int getPort(URL url) {
        int port = url.getPort();
        if (port == -1) {
            throw new WebAppDeployerException(
                "Port not found in url: " + url.toString());
        }
        return port;
    }

    private int getNextPort() {
        return nextPort++;
    }

    public void setNextPort(int nextPort) {
        this.nextPort = nextPort;
    }

    private String getBaseInstallDir() {
        return baseInstallDir;
    }

    public void setBaseInstallDir(String baseInstallDir) {
        this.baseInstallDir = baseInstallDir;
    }

    private TomcatUtil getTomcatUtil() {
        return tomcatUtil;
    }

    public void setTomcatUtil(TomcatUtil tomcatUtil) {
        this.tomcatUtil = tomcatUtil;
    }

}
