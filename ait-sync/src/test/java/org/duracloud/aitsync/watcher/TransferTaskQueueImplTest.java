package org.duracloud.aitsync.watcher;

import org.duracloud.aitsync.domain.ArchiveItResource;
import org.duracloud.aitsync.service.ConfigManager;
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
 *
 */
public class TransferTaskQueueImplTest {
    private TransferTaskQueueImpl queue;

    @Before
    public void setUp() throws Exception {
        Utils.configureStateDirectory();
        ConfigManager config = new ConfigManagerImpl();
        queue = new TransferTaskQueueImpl(config);

    }

    @After
    public void tearDown() throws Exception {

    }

    class Taker implements Runnable {
        boolean taken = false;
        private TransferTaskQueue<ResourceTransferTask> queue;

        public Taker(TransferTaskQueue<ResourceTransferTask> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                queue.take();
                taken = true;
            } catch (InterruptedException e) {
            }
        }
    }

    @Test
    public void testBlankTake() {
        queue.init();
        Taker t = new Taker(queue);
        Thread th = new Thread(t);
        th.start();
        Utils.sleep(100);
        Assert.assertFalse(t.taken);
        th.interrupt();
    }
    
    @Test
    public void testRoundRobinTake() throws InterruptedException{

        queue.init();

        // verify that take times out on empty queue.
        int count = 0;
        
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                ArchiveItResource ar = new ArchiveItResource((long)i, "file-"+j+".txt", "md5");
                queue.put(new ResourceTransferTask(ar));
                count++;
            }
        }

        TransferTask previous = null;
        for(int i = 0; i < count; i++){
            TransferTask t = queue.take();
            if(previous != null){
                Assert.assertTrue(previous.getResource().getGroupId() != t.getResource()
                                                                            .getGroupId());
            }
            
            previous = t;
        }

    }

    public void testPersistence() throws InterruptedException {
        this.queue.init();
        ArchiveItResource r0 = new ArchiveItResource(1l, "filename", "md5");
        this.queue.put(new ResourceTransferTask(r0));
        this.queue.destroy();

        this.queue = new TransferTaskQueueImpl(new ConfigManagerImpl());
        this.queue.init();

        TransferTask tt = this.queue.take();
        Resource r = tt.getResource();
        Assert.assertEquals(r0.getGroupId(), r.getGroupId());
        Assert.assertEquals(r0.getFilename(), r.getFilename());
        Assert.assertEquals(r0.getMd5(), r.getMd5());

    }
    /*
     * 
     * public void test2(){ // add one item and take it. TransferTask tt = new
     * TransferTask(new ArchiveItResource(1l, "filename", "md5"));
     * 
     * try { ttq.put(tt); Assert.assertTrue(true);
     * 
     * tt = ttq.take();
     * 
     * Assert.assertNotNull(tt);
     * 
     * } catch (InterruptedException e) { Assert.assertFalse(true); }
     * 
     * // verify that take times out on empty queue. try { ttq.take(100);
     * Assert.assertFalse(true); } catch (InterruptedException e) {
     * Assert.assertTrue(true); } }
     */
}
