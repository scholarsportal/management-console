/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.hadoop.util.impl;

import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.model.DescribeJobFlowsRequest;
import com.amazonaws.services.elasticmapreduce.model.DescribeJobFlowsResult;
import com.amazonaws.services.elasticmapreduce.model.JobFlowDetail;
import com.amazonaws.services.elasticmapreduce.model.JobFlowExecutionStatusDetail;
import org.duracloud.account.monitor.hadoop.domain.HadoopServiceInfo;
import org.duracloud.account.monitor.hadoop.util.HadoopUtil;
import org.duracloud.account.monitor.hadoop.util.impl.HadoopUtilImpl;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: 7/11/11
 */
public class HadoopUtilImplTest {

    private HadoopUtilImpl hadoopUtil;

    private AmazonElasticMapReduce mapReduce;

    private static final String FLOW_NAME = "flow-name";

    @Before
    public void setUp() throws Exception {
        mapReduce = EasyMock.createMock("AmazonElasticMapReduce",
                                        AmazonElasticMapReduce.class);

        hadoopUtil = new HadoopUtilImpl(mapReduce);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(mapReduce);
    }

    private void replayMocks() {
        EasyMock.replay(mapReduce);
    }

    @Test
    public void testGetServicesCreatedAfter() throws Exception {
        doTestGetServicesCreatedAfter(true);
    }

    @Test
    public void testGetServicesCreatedAfterError() throws Exception {
        doTestGetServicesCreatedAfter(false);
    }

    public void doTestGetServicesCreatedAfter(boolean valid) throws Exception {
        createMockExpectations(valid);
        replayMocks();

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        HadoopUtil.STATE state = HadoopUtil.STATE.RUNNING;
        Set<HadoopServiceInfo> services = hadoopUtil.getServicesCreatedAfter(
            date,
            state);
        Assert.assertNotNull(services);

        int numServices = valid ? 1 : 0;
        Assert.assertEquals(numServices, services.size());

        if (valid) {
            HadoopServiceInfo info = services.iterator().next();
            Assert.assertNotNull(info);
            Assert.assertEquals(HadoopUtil.STATE.RUNNING, info.getState());

            String text = info.toString();
            Assert.assertNotNull(text);
            Assert.assertTrue(text, text.contains(FLOW_NAME));
        }
    }

    private void createMockExpectations(boolean valid) {
        DescribeJobFlowsResult result = new DescribeJobFlowsResult();

        JobFlowDetail flowDetail = new JobFlowDetail().withName(FLOW_NAME);

        JobFlowExecutionStatusDetail statusDetail = new JobFlowExecutionStatusDetail(
            "BOOTSTRAPPING",
            null);
        flowDetail.setExecutionStatusDetail(statusDetail);

        Collection<JobFlowDetail> flowDetails = new ArrayList<JobFlowDetail>();
        flowDetails.add(flowDetail);
        result.setJobFlows(flowDetails);

        if (!valid) {
            result = null;
        }
        EasyMock.expect(mapReduce.describeJobFlows(EasyMock.<DescribeJobFlowsRequest>anyObject()))
            .andReturn(result);
    }

}
