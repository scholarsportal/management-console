package org.duracloud.aitsync.controller;

import org.duracloud.aitsync.domain.StatusSummary;
import org.duracloud.aitsync.manager.SyncManager;
import org.duracloud.aitsync.manager.SyncManager.State;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Daniel Bernstein
 * @created 12/17/2012
 * 
 */

public class SyncControllerTest {
    private SyncController syncController;
    private SyncManager syncManager;

    @Before
    public void setUp() throws Exception {
        this.syncManager = EasyMock.createMock(SyncManager.class);
        syncController = new SyncController(this.syncManager);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(this.syncManager);
    }

    @Test
    public void testStart() {
        this.syncManager.start();
        EasyMock.expectLastCall();
        replay();
        ModelAndView m = syncController.start();
        Assert.assertNotNull(m);
    }

    private void replay() {
        EasyMock.replay(this.syncManager);
    }

    @Test
    public void testStop() {
        this.syncManager.stop();
        EasyMock.expectLastCall();
        replay();

        ModelAndView m = syncController.stop();
        Assert.assertNotNull(m);
    }

    @Test
    public void testPause() {
        this.syncManager.pause();
        EasyMock.expectLastCall();
        replay();

        ModelAndView m = syncController.pause();
        Assert.assertNotNull(m);
    }
    
    @Test
    public void testResume() {
        this.syncManager.resume();
        EasyMock.expectLastCall();
        replay();

        ModelAndView m = syncController.resume();
        Assert.assertNotNull(m);
    }
    
    @Test
    public void testStatus() {
        EasyMock.expect(this.syncManager.getState()).andReturn(State.RUNNING);
        replay();

        ModelAndView m = syncController.status();
        Assert.assertNotNull(m);
        StatusSummary summary = (StatusSummary) m.getModel().get("status");
        Assert.assertNotNull(summary);
    }

}
