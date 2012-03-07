/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Mar 6, 2012
 *
 */
public class AccountClusterTest {
    @Test 
    public void testSerialization() throws Exception{
        int id = 10;
        String name = "test";
        Set<Integer> ids = new HashSet<Integer>();
        ids.add(1);
        ids.add(2);
        
        AccountCluster cluster = new AccountCluster(id, name, ids);
        File file = File.createTempFile("test", ".ser");
        file.deleteOnExit();
        // Serialize to a file
        ObjectOutput out = new ObjectOutputStream(new FileOutputStream(file));
        out.writeObject(cluster);
        out.close();
        
        ObjectInput in = new ObjectInputStream(new FileInputStream(file));
        AccountCluster deserialized = (AccountCluster)in.readObject();
        in.close();
        
        Assert.assertEquals(cluster,deserialized);
        Assert.assertEquals(cluster.getId(), deserialized.getId());
        Assert.assertEquals(cluster.getCounter(), deserialized.getCounter());
    }
}
