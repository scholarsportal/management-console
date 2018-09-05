/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.duracloud.account.db.model.DuracloudMill;
import org.duracloud.account.db.repo.DuracloudMillRepo;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Bernstein
 */
@RunWith(EasyMockRunner.class)
public class DuracloudMillConfigServiceImplTest extends EasyMockSupport {

    private static final String host = "host";
    private static final Integer port = 100;
    private static final String name = "name";
    private static final String username = "username";
    private static final String password = "password";
    private static final String auditQueue = "auditQueue";
    private static final String auditLogSpaceId = "auditLogSpaceId";

    @Mock
    private DuracloudMillRepo repo;

    @TestSubject
    private DuracloudMillConfigServiceImpl subject = new DuracloudMillConfigServiceImpl();

    @Test
    public void testGetNoPreviousSettings() {
        expect(repo.findAll()).andReturn(new ArrayList<DuracloudMill>());
        replayAll();

        DuracloudMill mill = subject.get();
        assertTrue(mill == null);
    }

    @Test
    public void testGetPreviouslySavedSettings() {
        expect(repo.findAll()).andReturn(Arrays.asList(new DuracloudMill()));
        replayAll();

        DuracloudMill mill = subject.get();
        assertNotNull(mill);
    }

    @Test
    public void testSetNoPreviousSettings() {
        expect(repo.findAll()).andReturn(new ArrayList<DuracloudMill>());
        Capture<DuracloudMill> saveCapture = Capture.newInstance();
        expect(repo.save(EasyMock.capture(saveCapture))).andReturn(null);
        replayAll();

        subject.set(host, port, name, username, password, auditQueue, auditLogSpaceId);
        DuracloudMill savedMill = saveCapture.getValue();
        assertEquals(host, savedMill.getDbHost());
        assertEquals(port, savedMill.getDbPort());
        assertEquals(name, savedMill.getDbName());
        assertEquals(username, savedMill.getDbUsername());
        assertEquals(password, savedMill.getDbPassword());
        assertEquals(auditQueue, savedMill.getAuditQueue());
        assertEquals(auditLogSpaceId, savedMill.getAuditLogSpaceId());
    }

    @Test
    public void testSet() {
        DuracloudMill entity = createMock(DuracloudMill.class);
        expect(repo.findAll()).andReturn(Arrays.asList(entity));
        expect(repo.save(entity)).andReturn(entity);

        entity.setDbHost(host);
        expectLastCall();
        entity.setDbPort(port);
        expectLastCall();
        entity.setDbName(name);
        expectLastCall();
        entity.setDbUsername(username);
        expectLastCall();
        entity.setDbPassword(password);
        expectLastCall();

        entity.setAuditQueue(auditQueue);
        expectLastCall();
        entity.setAuditLogSpaceId(auditLogSpaceId);
        expectLastCall();
        replayAll();

        subject.set(host, port, name, username, password, auditQueue, auditLogSpaceId);
    }

    @After
    public void tearDown() {
        verifyAll();
    }

}
