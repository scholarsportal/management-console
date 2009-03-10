
package org.duraspace.ec2serviceprovider.mgmt;

import java.io.FileInputStream;

import org.apache.commons.io.input.AutoCloseInputStream;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.serviceprovider.mgmt.InstanceDescription;

import static junit.framework.Assert.assertNotNull;

public class EC2ServiceProviderDriver {

    protected final Logger log = Logger.getLogger(getClass());

    private EC2ServiceProvider service;

    private final String accessKeyId = "0YMHVZZZ5GP0P7VFJV82";

    private final String secretAccessKey;

    private Credential credential;

    private String instanceId;

    private final String configFilePath =
            "src/test/resources/test-service-props.xml";

    private EC2ServiceProviderProperties props;

    public EC2ServiceProviderDriver(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }

    public void setUp() throws Exception {
        props = new EC2ServiceProviderProperties();
        props
                .load(new AutoCloseInputStream(new FileInputStream(configFilePath)));

        service = new EC2ServiceProvider();

        credential = new Credential();
        credential.setUsername(accessKeyId);
        credential.setPassword(secretAccessKey);

    }

    public void testStart() throws Exception {
        instanceId = service.start(credential, props);

        log.info("instance id: " + instanceId);

        InstanceDescription desc = null;
        while (!service.isInstanceRunning(credential, instanceId, props)) {
            desc =
                    service.describeRunningInstance(credential,
                                                    instanceId,
                                                    props);
            log.info("instance state: " + desc.getState().name());
            Thread.sleep(10000);
        }
        desc = service.describeRunningInstance(credential, instanceId, props);
        assertNotNull(desc);
        log.info("instance state:     " + desc.getState().name());
        log.info("launch time: " + desc.getLaunchTime());
        log.info("provider:    " + desc.getProvider());
        log.info("url:         " + desc.getURL());

        boolean done = false;
        while (!done) {
            Thread.sleep(10000);
            try {
                done = service.isWebappRunning(credential, instanceId, props);
                log.info("webapp state: " + done);
            } catch (Exception e) {
                e.printStackTrace();
                done = false;
            }
        }
        log.info("webapp is available at: \n\t" + desc.getURL());

    }

    public void testStop() throws Exception {
        log.info("stopping instance: " + instanceId);
        service.stop(credential, instanceId, props);
    }

    private static void usageAndQuit() {
        StringBuilder sb = new StringBuilder("Usage:\n");
        sb.append("\tOne argument is required: secretAccessKey\n\n");
        sb.append("\tjava EC2ServiceImplDriver secretAccessKey");
        System.err.println(sb.toString());
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 1) usageAndQuit();

        String secretAccessKey = args[0];
        EC2ServiceProviderDriver driver =
                new EC2ServiceProviderDriver(secretAccessKey);
        try {
            driver.setUp();

            driver.testStart();

            System.out.println();
            System.out.println("Press any key to terminate instance: "
                    + driver.instanceId);
            System.in.read();

            driver.testStop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
