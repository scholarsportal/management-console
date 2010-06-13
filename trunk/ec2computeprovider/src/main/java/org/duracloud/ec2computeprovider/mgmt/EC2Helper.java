/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.ec2computeprovider.mgmt;

import java.util.NoSuchElementException;

import com.amazonaws.ec2.model.DescribeInstancesResponse;
import com.amazonaws.ec2.model.DescribeInstancesResult;
import com.amazonaws.ec2.model.Reservation;
import com.amazonaws.ec2.model.RunInstancesResponse;
import com.amazonaws.ec2.model.RunInstancesResult;
import com.amazonaws.ec2.model.RunningInstance;
import com.amazonaws.ec2.model.TerminateInstancesResponse;
import com.amazonaws.ec2.model.TerminateInstancesResult;
import com.amazonaws.ec2.model.TerminatingInstance;

public class EC2Helper {

    protected static RunningInstance getFirstRunningInstance(RunInstancesResponse response) {
        if (response.isSetRunInstancesResult()) {
            RunInstancesResult result = response.getRunInstancesResult();
            if (result.isSetReservation()) {
                Reservation reservation = result.getReservation();
                if (reservation.isSetRunningInstance()) {
                    return reservation.getRunningInstance().get(0);
                }
            }
        }
        throw new NoSuchElementException("RunningInstance not found.");
    }

    protected static RunningInstance getFirstRunningInstance(DescribeInstancesResponse response) {
        if (response.isSetDescribeInstancesResult()) {
            DescribeInstancesResult result =
                    response.getDescribeInstancesResult();
            if (result.isSetReservation()) {
                Reservation reservation = result.getReservation().get(0);
                if (reservation.isSetRunningInstance()) {
                    return reservation.getRunningInstance().get(0);
                }
            }
        }
        throw new NoSuchElementException("RunningInstance not found.");
    }

    protected static TerminatingInstance getFirstStoppingInstance(TerminateInstancesResponse response) {
        if (response.isSetTerminateInstancesResult()) {
            TerminateInstancesResult result =
                    response.getTerminateInstancesResult();
            if (result.isSetTerminatingInstance()) {
                return result.getTerminatingInstance().get(0);
            }
        }
        throw new NoSuchElementException("TerminatingInstance not found.");
    }
}
