/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.compute;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import org.duracloud.account.compute.error.EC2ConnectionException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Bill Branan
 * Date: Feb 7, 2011
 */
public class AmazonComputeConnector {

    private static Map<String, AmazonEC2Client> ec2Clients =
        new HashMap<String, AmazonEC2Client>();

    public static AmazonEC2Client getAmazonEC2Client(String accessKey,
                                                     String secretKey) {
        AmazonEC2Client client = ec2Clients.get(key(accessKey, secretKey));
        if (null == client) {
            client = newEC2Client(accessKey, secretKey);
            ec2Clients.put(key(accessKey, secretKey), client);
        }
        return client;
    }

    private static String key(String accessKey, String secretKey) {
        return accessKey + secretKey;
    }

    private static AmazonEC2Client newEC2Client(String accessKey,
                                               String secretKey) {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey,
                                                                     secretKey);
        try {
            return new AmazonEC2Client(awsCredentials);
        } catch (AmazonServiceException e) {
            String err = "Could not create connection to Amazon EC2 due " +
                         "to error: " + e.getMessage();
            throw new EC2ConnectionException(err, e);
        }
    }

}
