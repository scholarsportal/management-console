/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.compute;

import com.amazonaws.services.ec2.AmazonEC2Client;
import junit.framework.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;

/**
 * @author: Bill Branan
 * Date: 2/9/11
 */
public class AmazonComputeConnectorTest {

    @Test
    public void testGetAmazonEC2Client() throws Exception {
        AmazonEC2Client client =
            AmazonComputeConnector.getAmazonEC2Client("abc", "123");
        assertNotNull(client);

        AmazonEC2Client client2 =
            AmazonComputeConnector.getAmazonEC2Client("abc", "123");
        assertNotNull(client2);

        assertEquals(client, client2);
        assertSame(client, client2);

        AmazonEC2Client client3 =
            AmazonComputeConnector.getAmazonEC2Client("abc", "456");
        assertNotNull(client3);

        Assert.assertFalse(client.equals(client3));

        AmazonEC2Client client4 =
            AmazonComputeConnector.getAmazonEC2Client("def", "123");
        assertNotNull(client4);

        Assert.assertFalse(client.equals(client4));
    }

}
