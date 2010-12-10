/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.error.DBUninitializedException;
import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Andrew Woods
 *         Date: Dec 10, 2010
 */
public class InitControllerTest {

    private InitController controller;
    private DuracloudRepoMgr repoMgr;

    @Before
    public void setUp() throws Exception {
        repoMgr = EasyMock.createMock(DuracloudRepoMgr.class);
        controller = new InitController();
        controller.setRepoMgr(repoMgr);
    }

    @After
    public void tearDown() {
        EasyMock.verify(repoMgr);
    }

    @Test
    public void testInitialize() throws Exception {
        repoMgr.initialize(EasyMock.isA(InputStream.class));
        EasyMock.expectLastCall();
        EasyMock.replay(repoMgr);

        ResponseEntity<String> response = controller.initialize(inputStream());
        Assert.assertNotNull(response);

        HttpStatus statusCode = response.getStatusCode();
        Assert.assertEquals(HttpStatus.OK, statusCode);
    }

    @Test
    public void testInitializeBad() throws Exception {
        repoMgr.initialize(EasyMock.isA(InputStream.class));
        EasyMock.expectLastCall().andThrow(new DBUninitializedException(
            "canned-exception"));
        EasyMock.replay(repoMgr);

        ResponseEntity<String> response = controller.initialize(inputStream());
        Assert.assertNotNull(response);

        HttpStatus statusCode = response.getStatusCode();
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);
    }

    private InputStream inputStream() {
        byte[] text = "junk".getBytes();
        return new AutoCloseInputStream(new ByteArrayInputStream(text));
    }

}
