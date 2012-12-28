package org.duracloud.aitsync.service;

import org.duracloud.aitsync.domain.Mapping;
import org.duracloud.aitsync.mapping.MappingManagerImpl;
import org.duracloud.aitsync.test.Utils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public class MappingManagerImplTest {

    @Before
    public void setUp() throws Exception {
        Utils.configureStateDirectory();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws Exception {

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





}
