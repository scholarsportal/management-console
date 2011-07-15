/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.hadoop.util.impl;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient;
import com.amazonaws.services.elasticmapreduce.model.DescribeJobFlowsRequest;
import com.amazonaws.services.elasticmapreduce.model.DescribeJobFlowsResult;
import com.amazonaws.services.elasticmapreduce.model.JobFlowDetail;
import com.amazonaws.services.elasticmapreduce.model.JobFlowExecutionStatusDetail;
import org.duracloud.account.monitor.hadoop.domain.HadoopServiceInfo;
import org.duracloud.account.monitor.error.HadoopNotActivatedException;
import org.duracloud.account.monitor.hadoop.util.HadoopUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: 7/11/11
 */
public class HadoopUtilImpl implements HadoopUtil {

    private AmazonElasticMapReduce mapReduce;

    public HadoopUtilImpl(String accessKey, String secretKey) {
        this(new AmazonElasticMapReduceClient(new BasicAWSCredentials(accessKey,
                                                                      secretKey)));
    }

    public HadoopUtilImpl(AmazonElasticMapReduce mapReduce) {
        this.mapReduce = mapReduce;
    }

    @Override
    public Set<HadoopServiceInfo> getServicesCreatedAfter(Date date,
                                                          STATE state) {

        DescribeJobFlowsRequest request = new DescribeJobFlowsRequest();
        request.setCreatedAfter(date);
        request.setJobFlowStates(state.asJobFlowStates());

        DescribeJobFlowsResult result = mapReduce.describeJobFlows(request);

        Set<HadoopServiceInfo> services = new HashSet<HadoopServiceInfo>();
        if (null == result) {
            return services;
        }

        List<JobFlowDetail> flows = result.getJobFlows();
        if (null != flows && flows.size() > 0) {

            for (JobFlowDetail flow : flows) {
                services.add(getServiceInfo(flow));
            }
        }
        return services;
    }

    private HadoopServiceInfo getServiceInfo(JobFlowDetail flow) {
        String flowName = flow.getName();
        Date flowStartTime = null;
        Date flowStopTime = null;
        String flowState = null;

        JobFlowExecutionStatusDetail detail = flow.getExecutionStatusDetail();
        if (null != detail) {
            flowStartTime = detail.getStartDateTime();
            flowStopTime = detail.getEndDateTime();
            flowState = detail.getState();
        }

        return new HadoopServiceInfo(flowName,
                                     flowState,
                                     flowStartTime,
                                     flowStopTime);
    }

    @Override
    public void verifyActivated() throws HadoopNotActivatedException {
        try {
            getServicesCreatedAfter(new Date(), HadoopUtil.STATE.COMPLETED);

        } catch (Exception e) {
            throw new HadoopNotActivatedException(e.getMessage(), e);
        }
    }
}
