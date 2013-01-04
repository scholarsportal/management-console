package org.duracloud.aitsync.repo;

import java.util.Date;
import java.util.List;

import org.duracloud.aitsync.repo.RemoteRepoImpl;
import org.duracloud.aitsync.repo.Resource;
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
public class ArchiveItRepoImplTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetResources() throws Exception{
        RemoteRepoImpl repo = new RemoteRepoImpl();
        
        
        List<Resource>  list = repo.getResources(1, new Date());
        
        Assert.assertTrue(list.size() > 0);
        for(Resource r : list){
            System.out.println(r.getFilename());
        }
    }

}
