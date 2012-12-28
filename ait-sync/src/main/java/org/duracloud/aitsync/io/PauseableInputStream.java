package org.duracloud.aitsync.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public class PauseableInputStream extends FilterInputStream {
    private CountDownLatch latch = null;

    public PauseableInputStream(InputStream is){
        super(is);
    }

    @Override
    public int read() throws IOException {
        CountDownLatch l = this.latch;
        if(l != null){
            try {
                l.await();
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
        }

        return super.read();
    }
    
    
    public void pause(){
        if(this.latch == null){
            latch = new CountDownLatch(1);
        }
    }
    
    public void resume(){
        if(latch != null){
            latch.countDown();
            latch = null;
        }
    }
    
}
