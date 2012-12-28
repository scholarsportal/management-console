package org.duracloud.aitsync.watcher;

import java.util.Date;

import org.duracloud.aitsync.service.ConfigManagerImpl;
import org.duracloud.aitsync.test.Utils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 */
public class WatchStateManagerImplTest {

    @Before
    public void setUp() throws Exception {
        Utils.configureStateDirectory();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        WatchStateManagerImpl wsi = new WatchStateManagerImpl(new ConfigManagerImpl());

        WatchState s = wsi.getState(1);

        Assert.assertNotNull(s);

        Date date = new Date();

        Assert.assertTrue(s.getDateOfLastCopiedResource().getTime() < date.getTime());

        wsi.setDateOfLastCopiedResource(1, date);

        Assert.assertEquals(date.getTime(), wsi.getState(1)
                                               .getDateOfLastCopiedResource()
                                               .getTime());

    }

}
