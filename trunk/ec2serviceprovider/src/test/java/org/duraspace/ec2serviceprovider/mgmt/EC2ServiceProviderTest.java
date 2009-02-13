
package org.duraspace.ec2serviceprovider.mgmt;

import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.ec2.AmazonEC2;
import com.amazonaws.ec2.AmazonEC2Exception;
import com.amazonaws.ec2.model.Reservation;
import com.amazonaws.ec2.model.RunInstancesRequest;
import com.amazonaws.ec2.model.RunInstancesResponse;
import com.amazonaws.ec2.model.RunInstancesResult;
import com.amazonaws.ec2.model.RunningInstance;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.serviceprovider.mgmt.InstanceDescription;
import org.easymock.EasyMock;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class EC2ServiceProviderTest {

    private EC2ServiceProvider serviceProvider;

    private AmazonEC2 mockEC2;

    private RunInstancesRequest mockRunRequest;

    private RunInstancesResponse mockRunResponse;
    private RunInstancesResult mockRunResult;
    private Reservation mockReservation;
    private List<RunningInstance> mockRunningInstances;
    private RunningInstance mockRunningInstance;

    private final String imageId = "test-image-id";

    private final String instanceId = "test-instance-id";

    private final String configFilePath =
            "src/test/resources/test-service-props.xml";

    private EC2ServiceProviderProperties props;

    @Before
    public void setUp() throws Exception {
        props = new EC2ServiceProviderProperties();
        props.load(new FileInputStream(configFilePath));
        serviceProvider = new EC2ServiceProvider();
        serviceProvider.setProps(props);
    }

    private void buildMockStartServiceWrapper() throws AmazonEC2Exception {
        mockEC2 = org.easymock.EasyMock.createMock(AmazonEC2.class);
        mockRunRequest = createMock(RunInstancesRequest.class);
        mockRunResponse = createMock(RunInstancesResponse.class);
        mockRunResult = createMock(RunInstancesResult.class);
        mockReservation = createMock(Reservation.class);
        mockRunningInstance = createMock(RunningInstance.class);

        expect(mockRunningInstance.getInstanceId()).andReturn(instanceId);
        replay(mockRunningInstance);
        mockRunningInstances = new ArrayList<RunningInstance>();
        mockRunningInstances.add(mockRunningInstance);

        expect(mockReservation.isSetRunningInstance()).andReturn(true);
        expect(mockReservation.getRunningInstance()).andReturn(mockRunningInstances);
        replay(mockReservation);

        expect(mockRunResult.isSetReservation()).andReturn(true);
        expect(mockRunResult.getReservation()).andReturn(mockReservation);
        replay(mockRunResult);

        expect(mockRunResponse.isSetRunInstancesResult()).andReturn(true);
        expect(mockRunResponse.getRunInstancesResult()).andReturn(mockRunResult);
        replay(mockRunResponse);

        replay(mockRunRequest);

        org.easymock.EasyMock.expect(mockEC2.runInstances(EasyMock
                .not(EasyMock.eq(mockRunRequest)))).andReturn(mockRunResponse);
        org.easymock.EasyMock.replay(mockEC2);

    }

    @After
    public void tearDown() throws Exception {
        props = null;
        serviceProvider = null;
        mockEC2 = null;
        mockRunRequest = null;
        mockRunResponse = null;
        mockRunResult = null;
        mockReservation = null;
        mockRunningInstance = null;
    }

    @Test
    public void testStart() throws Exception {
        buildMockStartServiceWrapper();
        serviceProvider.setEC2(mockEC2);

        String instId = serviceProvider.start(imageId);
        assertNotNull(instId);
        assertTrue(instId.equals(instanceId));
    }

//    @Test
    public void testStop() throws Exception {
        serviceProvider.stop(instanceId);
    }

//    @Test
    public void testDescribeRunningInstance() throws Exception {
        InstanceDescription instDesc =
                serviceProvider.describeRunningInstance(instanceId);
        assertNotNull(instDesc);
    }

//    @Test
    public void testIsInstanceRunning() throws Exception {
        boolean r = serviceProvider.isInstanceRunning(instanceId);
        assertTrue(r);
    }

//    @Test
    public void testIsWebappRunning() throws Exception {
        boolean r = serviceProvider.isWebappRunning(instanceId);
        assertTrue(r);
    }

}
