package org.duracloud.aitsync.service;

import java.io.File;

import junit.framework.Assert;

import org.duracloud.aitsync.domain.Mapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MappingManagerImplTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws Exception {
        configureStateDirectory();

        ConfigManagerImpl cm = new ConfigManagerImpl();

        MappingManagerImpl mm = new MappingManagerImpl(cm);

        mm.load();

        Assert.assertEquals(0, mm.getMappings().size());

        Mapping m = new Mapping(1, "host", 80, "spaceId");
        mm.addMapping(m);

        Assert.assertEquals(1, mm.getMappings().size());

        mm.shutdown();

        mm = new MappingManagerImpl(cm);

        mm.load();

        Assert.assertEquals(1, mm.getMappings().size());
        
        Mapping m2 = mm.getMapping(m.getArchiveItAccountId());
        
        Assert.assertNotNull(m2);
        
        m2 = mm.removeMapping(m2.getArchiveItAccountId());
        
        Assert.assertEquals(0, mm.getMappings().size());
        
        mm.addMapping(m2);
        
        mm.clear();

        Assert.assertEquals(0, mm.getMappings().size());

    }



    protected void configureStateDirectory() {
        File sd =
            new File(System.getProperty("java.tmp.dir")
                + File.separator + "test" + System.currentTimeMillis());
        sd.deleteOnExit();
        sd.mkdirs();
        System.setProperty(ConfigManagerImpl.DURACLOUD_AITSYNC_STATE_DIR,
                           sd.getAbsolutePath());
    }

}
