package org.duracloud.aitsync.manager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import org.duracloud.aitsync.manager.Worker;
import org.duracloud.aitsync.manager.WorkerImpl;
import org.duracloud.aitsync.manager.WorkerListener;
import org.duracloud.aitsync.queue.ResourceTransferTask;
import org.duracloud.aitsync.repo.ArchiveItResource;
import org.duracloud.aitsync.store.EndPoint;
import org.duracloud.aitsync.test.Utils;
import org.duracloud.client.ContentStore;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public class WorkerImplTest {

    private CountDownLatch latch;
    private WorkerImpl worker;
    private WorkerListener listener;
    private EndPoint endPoint;
    
    private Thread t;
    private static int TRANSFER_TIME = 1000;

    @Before
    public void setUp() throws Exception {
        latch = new CountDownLatch(1);
        listener = createListener(latch);

        endPoint = EasyMock.createMock(EndPoint.class);

        worker = setupWorker(listener, endPoint);
 
        EasyMock.replay(endPoint);
        
        t = new Thread(worker);
        t.start();
        
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(endPoint);
    }

    @Test
    public void testPauseResume() throws InterruptedException {
        worker.pause();
        worker.resume();
        latch.await();
    }

    private WorkerListener createListener(final CountDownLatch latch) {

        WorkerListener listener = new WorkerListener() {
            @Override
            public void done(Worker worker) {
                latch.countDown();
            }
        };

        return listener;
    }

    private WorkerImpl setupWorker(WorkerListener listener, EndPoint endPoint) {
        long id = 1;
        String filename = "test";
        String md5 = "051513523523523";

        ResourceTransferTask tt =
            new ResourceTransferTask(new ArchiveItResource(id, filename, md5) {
                @Override
                public InputStream getInputStream() throws IOException {
                    return new ByteArrayInputStream(new byte[] { 0, 1, 0, 1 });
                }
            });


        EasyMock.expect(endPoint.sync(EasyMock.isA(String.class),
                                      EasyMock.isA(String.class),
                                      EasyMock.isA(URL.class),
                                      EasyMock.isA(InputStream.class))).andAnswer(new IAnswer<Boolean>(
                                          ) {
                                          @Override
                                        public Boolean answer()
                                            throws Throwable {
                                              Utils.sleep(TRANSFER_TIME);
                                              return true;
                                          }
                                    });

        
        WorkerImpl w = new WorkerImpl(tt, listener, endPoint);
        return w;
    }
}
