/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.init;

import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.appconfig.ApplicationInitializer;
import org.duracloud.appconfig.domain.Application;
import org.duracloud.appconfig.domain.DuradminConfig;
import org.duracloud.appconfig.domain.DuraserviceConfig;
import org.duracloud.appconfig.domain.DurastoreConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * @author Andrew Woods
 *         Date: Feb 1, 2011
 */
public class InitializerTest {

    private String amaHost = "amaHost";
    private String amaPort = "amaPort";
    private String amaContext = "amaContext";

    private String username = "username";
    private String password = "password";

    @Test
    public void testLoad() throws Exception {
        Properties props = createProps();
        File propsFile = File.createTempFile("app-init-", ".props");
        FileOutputStream output = new FileOutputStream(propsFile);
        props.store(output, "no comments");
        output.close();

        Initializer config = new Initializer(propsFile);
        verifyApplicationInitializer(config);
    }

    private Properties createProps() {
        Properties props = new Properties();

        String dot = ".";
        String app = Initializer.QUALIFIER + dot;
        String appAma = app + AmaConfig.QUALIFIER + dot;

        String host = Initializer.hostKey;
        String port = Initializer.portKey;
        String context = Initializer.contextKey;

        props.put(appAma + host, amaHost);
        props.put(appAma + port, amaPort);
        props.put(appAma + context, amaContext);

        String ama = AmaConfig.QUALIFIER + dot;
        String user = ama + AmaConfig.awsUsernameKey;
        String pass = ama + AmaConfig.awsPasswordKey;

        props.put(user, username);
        props.put(pass, password);
        return props;
    }

    private void verifyApplicationInitializer(Initializer config) {
        Application ama = config.getAma();
        Assert.assertNotNull(ama);

        String host = ama.getHost();
        String port = ama.getPort();
        String context = ama.getContext();

        Assert.assertNotNull(host);
        Assert.assertNotNull(port);
        Assert.assertNotNull(context);

        Assert.assertEquals(amaHost, host);
        Assert.assertEquals(amaPort, port);
        Assert.assertEquals(amaContext, context);
    }

}
