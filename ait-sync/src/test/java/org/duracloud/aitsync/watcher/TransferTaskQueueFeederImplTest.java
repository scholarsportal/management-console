package org.duracloud.aitsync.watcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.duracloud.aitsync.domain.ArchiveItResource;
import org.duracloud.aitsync.domain.Mapping;
import org.duracloud.aitsync.mapping.MappingManager;
import org.duracloud.aitsync.test.Utils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public class TransferTaskQueueFeederImplTest {
    private MappingManager mappingManager;
    private WatchStateManager watchStateManager;
    private RemoteRepo remoteRepo;
    private TransferTaskQueue queue;
    private WatchState watchState;

    @Before
    public void setUp() throws Exception {
        this.mappingManager = EasyMock.createMock(MappingManager.class);
        this.watchStateManager = EasyMock.createMock(WatchStateManager.class);
        this.remoteRepo = EasyMock.createMock(RemoteRepo.class);
        this.queue = EasyMock.createMock(TransferTaskQueue.class);
        this.watchState = EasyMock.createMock(WatchState.class);

    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(mappingManager,
                        watchStateManager,
                        remoteRepo,
                        queue,
                        watchState);
    }

    @Test
    public void testFetchAndFeed() throws Exception {
        List<Mapping> mapList = new LinkedList<Mapping>();
        mapList.add(new Mapping(1, "host", 80, "space"));
        EasyMock.expect(mappingManager.getMappings()).andReturn(mapList);
        EasyMock.expect(watchState.getDateOfLastCopiedResource())
                .andReturn(new Date());
        EasyMock.expect(watchStateManager.getState(EasyMock.anyLong()))
                .andReturn(this.watchState);

        List<Resource> resources =
            new ArrayList<Resource>(10);
        for (int i = 0; i < 10; i++) {
            resources.add(new ArchiveItResource((long) i,
                                                "12-20120102121212-test.warc"
                                                    + i,
                                                "md5" + i));
        }

        EasyMock.expect(remoteRepo.getResources(EasyMock.anyLong(),
                                                EasyMock.isA(Date.class)))
                .andReturn(resources);

        queue.put(EasyMock.isA(TransferTask.class));
        EasyMock.expectLastCall().times(resources.size());

        watchStateManager.setDateOfLastCopiedResource(EasyMock.anyLong(),
                                                      EasyMock.isA(Date.class));
        EasyMock.expectLastCall();

        EasyMock.replay(mappingManager,
                        watchStateManager,
                        remoteRepo,
                        queue,
                        watchState);

        TransferTaskQueueFeederImpl f =
            new TransferTaskQueueFeederImpl(mappingManager,
                                            watchStateManager,
                                            remoteRepo,
                                            queue);

        f.start();

        Utils.sleep(2000);
    }

}
