package org.duracloud.aitsync.service;

import java.util.concurrent.CountDownLatch;

import org.duracloud.aitsync.test.Utils;
import org.duracloud.aitsync.watcher.TransferTask;
import org.duracloud.aitsync.watcher.TransferTaskQueue;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein 
 * Date: 12/24/2012
 * 
 */
public class SyncManagerImplTest {

    private SyncManagerImpl syncManager;
    private WorkManager workManager;
    private TransferTaskQueue queue;

    @Before
    public void setUp() throws Exception {
        queue = EasyMock.createMock(TransferTaskQueue.class);
        workManager = EasyMock.createMock(WorkManager.class);
        syncManager = new SyncManagerImpl(queue, workManager);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(queue, workManager);
    }

    @Test
    public void testAll() throws InterruptedException {

        EasyMock.expect(queue.take())
                .andReturn(EasyMock.createMock(TransferTask.class));
        EasyMock.expect(queue.take()).andAnswer(new IAnswer<TransferTask>() {
            @Override
            public TransferTask answer() throws Throwable {
                CountDownLatch latch = new CountDownLatch(1);
                latch.await();
                throw new IllegalStateException("shouldn't ever reach this line");
            }
        });

        workManager.perform(EasyMock.isA(TransferTask.class));
        EasyMock.expectLastCall();

        workManager.pause();
        EasyMock.expectLastCall();

        workManager.resume();
        EasyMock.expectLastCall();

        EasyMock.replay(queue, workManager);

        syncManager.init();
        Assert.assertEquals(SyncManager.State.READY, syncManager.getState());
        syncManager.start();
        Assert.assertEquals(SyncManager.State.RUNNING, syncManager.getState());

        Utils.sleep(1000);

        syncManager.pause();

        Assert.assertEquals(SyncManager.State.PAUSED, syncManager.getState());

        syncManager.resume();

        Assert.assertEquals(SyncManager.State.RUNNING, syncManager.getState());

        syncManager.stop();

        Assert.assertEquals(SyncManager.State.READY, syncManager.getState());

    }

}
