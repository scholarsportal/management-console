
package org.duraspace.ec2serviceprovider.mgmt;

import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.duraspace.serviceprovider.mgmt.InstanceDescription;

import static junit.framework.Assert.assertNotNull;

public class EC2ServiceImplDriver {

    Log log = LogFactory.getLog(this.getClass());

    private EC2ServiceProviderImpl service;

    private AmazonEC2WrapperImpl ec2Wrapper;

    private final String accessKeyId = "0YMHVZZZ5GP0P7VFJV82";

    private final String secretAccessKey;

    private String instanceId;

    private final String configFilePath =
            "src/test/resources/test-service-props.xml";

    private EC2ServiceProperties props;

    public EC2ServiceImplDriver(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }

    public void setUp() throws Exception {
        props = new EC2ServiceProperties();
        props.load(new FileInputStream(configFilePath));

        ec2Wrapper = new AmazonEC2WrapperImpl();
        ec2Wrapper.setAccessKeyId(accessKeyId);
        ec2Wrapper.setSecretAccessKey(secretAccessKey);
        ec2Wrapper.setProps(props);

        service = new EC2ServiceProviderImpl();
        service.setProps(props);
        service.setService(ec2Wrapper);
    }

    public void testStart() throws Exception {
        instanceId = service.start(props.getImageId());
        log.info("instance id: " + instanceId);

        InstanceDescription desc = null;
        while (!service.isInstanceRunning(instanceId)) {
            desc = service.describeRunningInstance(instanceId);
            log.info("instance state: " + desc.getState().name());
            Thread.sleep(10000);
        }
        desc = service.describeRunningInstance(instanceId);
        assertNotNull(desc);
        log.info("instance state:     " + desc.getState().name());
        log.info("launch time: " + desc.getLaunchTime());
        log.info("provider:    " + desc.getProvider());
        log.info("url:         " + desc.getURL());

        boolean done = false;
        while (!done) {
            Thread.sleep(10000);
            try {
                done = service.isWebappRunning(instanceId);
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
        service.stop(instanceId);
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
        EC2ServiceImplDriver driver = new EC2ServiceImplDriver(secretAccessKey);
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
