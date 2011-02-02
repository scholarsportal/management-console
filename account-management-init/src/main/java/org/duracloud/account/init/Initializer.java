/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.init;

import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.account.security.domain.InitUserCredential;
import org.duracloud.appconfig.domain.AppConfig;
import org.duracloud.appconfig.domain.Application;
import org.duracloud.appconfig.domain.BaseConfig;
import org.duracloud.appconfig.support.ApplicationWithConfig;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.model.Credential;
import org.duracloud.common.web.RestHttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Andrew Woods
 *         Date: Jan 28, 2011
 */
public class Initializer extends BaseConfig {

    private final Logger log = LoggerFactory.getLogger(Initializer.class);

    public static final String QUALIFIER = "app";

    private ApplicationWithConfig amaWithConfig;

    private static final String amaKey = "ama";

    protected static final String hostKey = "host";
    protected static final String portKey = "port";
    protected static final String contextKey = "context";

    private String amaHost;
    private String amaPort;
    private String amaContext;


    public Initializer(File propsFile) throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(propsFile));

        Map<String, String> props = new HashMap<String, String>();
        for (String key : p.stringPropertyNames()) {
            props.put(key, p.get(key).toString());
        }
        this.load(props);
    }

    /**
     * This method sets the configuration of the AMA from the provided props.
     * Note: this method is called by the constructor, so generally is should
     * not be needed publicly.
     *
     * @param props
     */
    public void load(Map<String, String> props) {
        super.load(props);

        createAmaApplication();
        amaWithConfig.getConfig().load(props);
    }

    private void createAmaApplication() {
        if (amaEndpointLoad()) {
            Credential credential = new InitUserCredential();
            Application ama = new Application(amaHost,
                                              amaPort,
                                              amaContext,
                                              credential.getUsername(),
                                              credential.getPassword());
            amaWithConfig = new ApplicationWithConfig(amaKey);
            amaWithConfig.setApplication(ama);
            amaWithConfig.setConfig(new AmaConfig());

        } else {
            String msg = "ama endpoint !loaded";
            log.warn(msg);
            throw new DuraCloudRuntimeException(msg);
        }
    }

    private boolean amaEndpointLoad() {
        return null != amaHost && null != amaPort && null != amaContext;
    }

    protected String getQualifier() {
        return QUALIFIER;
    }

    protected void loadProperty(String key, String value) {
        String prefix = getPrefix(key);
        String suffix = getSuffix(key);

        if (prefix.equalsIgnoreCase(AmaConfig.QUALIFIER)) {
            loadAma(suffix, value);

        } else {
            String msg = "unknown key: " + key + " (" + value + ")";
            log.error(msg);
            throw new DuraCloudRuntimeException(msg);
        }
    }

    private void loadAma(String key, String value) {
        String prefix = getPrefix(key);
        if (prefix.equalsIgnoreCase(hostKey)) {
            this.amaHost = value;

        } else if (prefix.equalsIgnoreCase(portKey)) {
            this.amaPort = value;

        } else if (prefix.equalsIgnoreCase(contextKey)) {
            this.amaContext = value;

        } else {
            String msg = "unknown key: " + key + " (" + value + ")";
            log.error(msg);
            throw new DuraCloudRuntimeException(msg);
        }
    }

    /**
     * This method initializes the AMA based on the loaded configuration.
     *
     * @return
     */
    public RestHttpHelper.HttpResponse initialize() {
        RestHttpHelper.HttpResponse response = null;

        Application app = amaWithConfig.getApplication();
        AppConfig config = amaWithConfig.getConfig();

        response = app.initialize(config);
        validate(response, amaWithConfig.getName());

        return response;
    }

    private void validate(RestHttpHelper.HttpResponse response, String name) {
        if (null == response || response.getStatusCode() != 200) {
            String body = null;
            try {
                body = response.getResponseBody();
            } catch (IOException e) {
            } finally {
                StringBuilder msg = new StringBuilder("error initializing ");
                msg.append(name);
                msg.append(" (" + response.getStatusCode() + ")");
                if (null != body) {
                    msg.append("\n");
                    msg.append(body);
                }
                log.error(msg.toString());
                throw new DuraCloudRuntimeException(msg.toString());
            }
        }
    }

    /**
     * This method writes the configuration files for the AMA to the provided
     * directory.
     *
     * @param dir
     */
    public void outputXml(File dir) {
        String name = amaWithConfig.getName();
        AppConfig config = amaWithConfig.getConfig();
        write(new File(dir, name + "-init.xml"), config.asXml());
    }

    private void write(File file, String xml) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(xml);
            bw.close();

        } catch (IOException e) {
            String msg = "error writing init xml: " + file.getPath();
            log.error(msg, e);
            throw new DuraCloudRuntimeException(msg, e);
        }
    }

    public Application getAma() {
        return amaWithConfig.getApplication();
    }

}
