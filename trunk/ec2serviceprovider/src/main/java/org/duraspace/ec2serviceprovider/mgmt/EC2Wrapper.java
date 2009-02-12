package org.duraspace.ec2serviceprovider.mgmt;

import com.amazonaws.ec2.AmazonEC2;


public interface EC2Wrapper {

    public AmazonEC2 getService();

}
