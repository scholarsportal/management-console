package org.duracloud.aitsync.manager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import org.duracloud.aitsync.config.ConfigManagerImpl;
import org.duracloud.aitsync.manager.WorkManagerImpl;
import org.duracloud.aitsync.queue.ResourceTransferTask;
import org.duracloud.aitsync.repo.ArchiveItResource;
import org.duracloud.aitsync.repo.Resource;
import org.duracloud.aitsync.store.EndPoint;
import org.duracloud.aitsync.store.EndPointException;
import org.duracloud.aitsync.store.EndPointFactory;
import org.duracloud.aitsync.test.Utils;
import org.duracloud.error.ContentStoreException;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public class WorkManagerImplTest {
    private Logger log = LoggerFactory.getLogger(WorkManagerImplTest.class);

    @Before
    public void setUp() throws Exception {
        Utils.configureStateDirectory();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws ContentStoreException, EndPointException {
        final String md5 = "md5";

        final int times = 10;
        EndPoint endPoint = EasyMock.createMock(EndPoint.class);

        EndPointFactory endPointFactory =
            EasyMock.createMock(EndPointFactory.class);
        EasyMock.expect(endPointFactory.createEndPoint(EasyMock.isA(Resource.class)))
                .andReturn(endPoint)
                .times(times);

        EasyMock.expect(endPoint.sync(EasyMock.isA(String.class),
                                      EasyMock.isA(String.class),
                                      EasyMock.isA(URL.class),
                                      EasyMock.isA(InputStream.class)))
                .andAnswer(new IAnswer<Boolean>() {
                    @Override
                    public Boolean answer() throws Throwable {

                        log.info("mock sync sleeping for 1/2 sec");
                        Utils.sleep(500);
                        return true;
                    }
                })
                .times(times);

        EasyMock.replay(endPoint, endPointFactory);
        int workerCount = 5;
        final WorkManagerImpl wmi =
            new WorkManagerImpl(new ConfigManagerImpl(),
                                workerCount,
                                endPointFactory);
        wmi.start();
        final CountDownLatch latch = new CountDownLatch(10);
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < times; i++) {
                    ResourceTransferTask tt =
                        new ResourceTransferTask(new ArchiveItResource((long) i,
                                                                       "filename",
                                                                       md5) {
                            @Override
                            public InputStream getInputStream()
                                throws IOException {
                                return new ByteArrayInputStream(new byte[200]);
                            }
                        });
                    wmi.perform(tt);
                    latch.countDown();
                }
            }
        });

        t.start();

        Utils.sleep(1000);

        Assert.assertEquals(workerCount, wmi.getActiveWorkers().size());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Utils.sleep(6000);

        Assert.assertEquals(0, wmi.getActiveWorkers().size());

        wmi.shutdown();

        EasyMock.verify(endPoint, endPointFactory);

    }

}
