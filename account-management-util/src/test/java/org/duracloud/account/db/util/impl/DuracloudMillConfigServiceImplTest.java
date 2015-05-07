/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;

import org.duracloud.account.db.model.DuracloudMill;
import org.duracloud.account.db.repo.DuracloudMillRepo;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * 
 * @author Daniel Bernstein
 *
 */
@RunWith(EasyMockRunner.class)
public class DuracloudMillConfigServiceImplTest extends EasyMockSupport{

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
    public void testSet() {
        String host = "host";
        Integer port = 100;
        String name = "name";
        String username = "username";
        String password = "password";
        String auditQueue = "auditQueue";
        String auditLogSpaceId = "auditLogSpaceId";
        
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
    public void tearDown(){
        verifyAll();
    }


}
