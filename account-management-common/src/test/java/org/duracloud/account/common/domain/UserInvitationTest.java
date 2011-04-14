/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 3/18/11
 */
public class UserInvitationTest {

    private UserInvitation invitation;

    private final int id = 7;
    private final int acctId = 9;
    private final String userEmail = "a@g.com";
    private final int expirationDays = 10;
    private final String redemptionCode = "a-redemption-code";

    @Before
    public void setUp() throws Exception {
        invitation = new UserInvitation(id,
                                        acctId,
                                        userEmail,
                                        expirationDays,
                                        redemptionCode);
    }

    @After
    public void tearDown() throws Exception {
        invitation = null;
    }

    @Test
    public void testGetRedemptionCode() throws Exception {
        Assert.assertEquals(redemptionCode, invitation.getRedemptionCode());
    }

    @Test
    public void testGetRedemptionURL() throws Exception {
        String host = AmaEndpoint.getHost();
        String port = AmaEndpoint.getPort();
        String ctxt = AmaEndpoint.getCtxt();
        String pcol = port.equals("443") ? "https" : "http";

        verifyUrl(pcol, host, port, ctxt);

        String newHost = "junkhost";
        String newPort = "443";
        String newCtxt = "abc";
        String newPcol = "https";
        AmaEndpoint.initialize(newHost, newPort, newCtxt);

        verifyUrl(newPcol, newHost, newPort, newCtxt);
    }

    private void verifyUrl(String pcol, String host, String port, String ctxt) {
        String url = invitation.getRedemptionURL();
        Assert.assertNotNull(url);

        Assert.assertEquals(
            pcol + "://" + host + ":" + port + "/" + ctxt + "/users/redeem/" +
                redemptionCode, url);
    }
}
