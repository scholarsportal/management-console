
package org.duraspace.ec2computeprovider.mgmt;

import java.io.FileInputStream;

import java.net.URL;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amazonaws.ec2.AmazonEC2Exception;
import com.amazonaws.ec2.model.DescribeInstancesResponse;
import com.amazonaws.ec2.model.DescribeInstancesResult;
import com.amazonaws.ec2.model.Reservation;
import com.amazonaws.ec2.model.RunningInstance;

import org.apache.commons.io.input.AutoCloseInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.util.DateUtil;
import org.duraspace.computeprovider.mgmt.InstanceState;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class EC2InstanceDescriptionTest {

    private EC2InstanceDescription descGood;

    private EC2InstanceDescription descWithError;

    private DescribeInstancesResponse mockResponse;

    private DescribeInstancesResult mockResult;

    private List<Reservation> mockReservations;

    private Reservation mockReservation;

    private List<RunningInstance> mockRunningInstances;

    private RunningInstance mockRunningInstance;

    private com.amazonaws.ec2.model.InstanceState mockState;

    private EC2ComputeProviderProperties props;

    private AmazonEC2Exception e;

    private final String provider = "test-amazon-provider";

    private final String instanceId = "test-instanceId";

    private final InstanceState state = InstanceState.PENDING;

    private final String launch = "2009-02-12T02:03:00.000Z";

    private final String urlSpec = "test.org";

    private final String url = "http://" + urlSpec + ":8080test-app-name";

    private final String exceptionMsg = "test-exception";

    private final String configFilePath =
            "src/test/resources/testEC2Config.properties";

    @Before
    public void setUp() throws Exception {
        buildMockObjects();
        props = new EC2ComputeProviderProperties();
        props
                .loadFromXmlStream(new AutoCloseInputStream(new FileInputStream(configFilePath)));
        e = new AmazonEC2Exception(exceptionMsg);
    }

    private void buildMockObjects() {
        mockResponse = createMock(DescribeInstancesResponse.class);
        mockResult = createMock(DescribeInstancesResult.class);
        mockReservation = createMock(Reservation.class);
        mockRunningInstance = createMock(RunningInstance.class);
        mockState = createMock(com.amazonaws.ec2.model.InstanceState.class);

        expect(mockState.isSetName()).andReturn(true);
        expect(mockState.getName()).andReturn("pending");
        replay(mockState);

        expect(mockRunningInstance.isSetInstanceId()).andReturn(true);
        expect(mockRunningInstance.isSetInstanceState()).andReturn(true);
        expect(mockRunningInstance.isSetLaunchTime()).andReturn(true);
        expect(mockRunningInstance.isSetPublicDnsName()).andReturn(true);

        expect(mockRunningInstance.getInstanceId()).andReturn(instanceId);
        expect(mockRunningInstance.getInstanceState()).andReturn(mockState);
        expect(mockRunningInstance.getInstanceState()).andReturn(mockState);
        expect(mockRunningInstance.getLaunchTime()).andReturn(launch);
        expect(mockRunningInstance.getPublicDnsName()).andReturn(urlSpec);
        replay(mockRunningInstance);

        mockRunningInstances = new ArrayList<RunningInstance>();
        mockRunningInstances.add(mockRunningInstance);
        expect(mockReservation.isSetRunningInstance()).andReturn(true);
        expect(mockReservation.getRunningInstance())
                .andReturn(mockRunningInstances);
        replay(mockReservation);

        mockReservations = new ArrayList<Reservation>();
        mockReservations.add(mockReservation);
        expect(mockResult.isSetReservation()).andReturn(true);
        expect(mockResult.getReservation()).andReturn(mockReservations);
        replay(mockResult);

        expect(mockResponse.isSetDescribeInstancesResult()).andReturn(true);
        expect(mockResponse.getDescribeInstancesResult()).andReturn(mockResult);
        replay(mockResponse);
    }

    @After
    public void tearDown() throws Exception {
        mockResponse = null;
        mockResult = null;
        mockReservation = null;
        mockRunningInstance = null;
        mockState = null;

        props = null;
        e = null;

        descGood = null;
        descWithError = null;
    }

    @Test
    public void testDescription() throws ParseException {
        descGood = new EC2InstanceDescription(mockResponse, props);

        Exception ex = descGood.getException();
        String id = descGood.getInstanceId();
        Date d8 = descGood.getLaunchTime();
        String pvdr = descGood.getProvider();
        InstanceState st = descGood.getState();
        URL u = descGood.getURL();

        assertTrue(ex == null);
        assertNotNull(id);
        assertNotNull(d8);
        assertNotNull(pvdr);
        assertNotNull(st);
        assertNotNull(u);

        assertTrue(id.equals(instanceId));
        assertTrue(d8.equals(DateUtil.convertToDate(launch)));
        assertTrue(pvdr.equals(provider));
        assertTrue(st.equals(state));
        assertTrue("expect: " + url + ", found: " + u, u.toString()
                .equals(url));

    }

    @Test
    public void testDescriptionWithError() {
        descWithError = new EC2InstanceDescription(e);
        descWithError.setProps(props);

        assertTrue(descWithError.hasError());

        Exception ex = descWithError.getException();
        assertNotNull(ex);

        assertTrue(exceptionMsg.equals(ex.getMessage()));
    }

}
