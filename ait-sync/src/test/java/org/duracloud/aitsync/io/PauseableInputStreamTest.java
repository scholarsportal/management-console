package org.duracloud.aitsync.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
public class PauseableInputStreamTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws IOException, InterruptedException {
        byte[] buf = new byte[3];
        for(int i = 0; i < buf.length; i++){
            buf[i] = 1;
        }
        ByteArrayInputStream is = new ByteArrayInputStream(buf);
        
        final PauseableInputStream pis = new PauseableInputStream(is);
        
        
        Assert.assertEquals(1, pis.read());
        
        pis.pause();
        
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(){
            
            public void run() {
                try {
                    pis.read();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                latch.countDown();
                
            };
        }.start();
        
        Assert.assertFalse(latch.await(1000, TimeUnit.MILLISECONDS));
        
        pis.resume();
        
        Assert.assertEquals(1, pis.read());

    }

}
