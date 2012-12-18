package org.duracloud.aitsync;

import junit.framework.Assert;

import org.junit.After;
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
    
    @Before
    public void setUp() throws Exception {
        syncController = new SyncController(new RestUtilsImpl());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testStart() {
        ModelAndView m = syncController.start();
        Assert.assertNotNull(m);
    }

    @Test
    public void testStop() {
        ModelAndView m = syncController.stop();
        Assert.assertNotNull(m);
    }

    @Test
    public void testStatus() {
        ModelAndView m = syncController.status();
        Assert.assertNotNull(m);
        StatusSummary summary = (StatusSummary)m.getModel().get("status");
        Assert.assertNotNull(summary);
    }

}
