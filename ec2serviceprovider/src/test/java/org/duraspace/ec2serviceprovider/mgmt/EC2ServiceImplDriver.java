
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

    private final String secretAccessKey = "!!!**!!!-- SUPPLY IT --!!!**!!!";

    private String instanceId;

    private final String configFilePath =
            "src/test/resources/test-service-props.xml";

    private EC2ServiceProperties props;

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
            log.info("not running yet: " + desc.getState().name());
            Thread.sleep(10000);
        }
        desc = service.describeRunningInstance(instanceId);
        assertNotNull(desc);
        log.info("running:     " + desc.getState().name());
        log.info("launch time: " + desc.getLaunchTime());
        log.info("provider:    " + desc.getProvider());
        log.info("url:         " + desc.getURL());

        boolean done = false;
        while (!done) {
            try {
                done = service.isWebappRunning(instanceId);
                log.info("app is up? :" + done);
            } catch (Exception e) {
                e.printStackTrace();
                done = false;
            }
            Thread.sleep(10000);
        }
        log.info("app is up at: " + desc.getURL());

    }

    public void testStop() throws Exception {
        log.info("stopping instance: " + instanceId);
        service.stop(instanceId);
    }

    public static void main(String[] args) {
        try {
            EC2ServiceImplDriver driver = new EC2ServiceImplDriver();
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
