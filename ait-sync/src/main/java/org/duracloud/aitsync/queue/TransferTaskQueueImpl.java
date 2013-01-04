package org.duracloud.aitsync.queue;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.duracloud.aitsync.config.ConfigManager;
import org.duracloud.aitsync.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
@Component
public class TransferTaskQueueImpl implements TransferTaskQueue {

    private Map<Long, LinkedBlockingQueue<TransferTask>> outQueuesMap;
    private List<LinkedBlockingQueue<TransferTask>> outQueuesList =
        new LinkedList<LinkedBlockingQueue<TransferTask>>();

    private CountDownLatch latch;

    private int lastIndex = 0;

    private ConfigManager configManager;

    private static String OUT_QUEUES_MAP_FILE = "out-queues-map.xml";

    @Autowired
    public TransferTaskQueueImpl(ConfigManager configManager) {

        this.configManager = configManager;

        this.outQueuesMap =
            new HashMap<Long, LinkedBlockingQueue<TransferTask>>();
        this.outQueuesList =
            new LinkedList<LinkedBlockingQueue<TransferTask>>();
        this.latch = new CountDownLatch(1);
    }

    @PostConstruct
    public void init() {

        loadQueues();
        if (this.outQueuesList.size() > 0) {
            openDoor();
        }
    }

    protected void loadQueues() {
        File outQueuesMapFile = getPersistentFile();
        if (outQueuesMapFile.exists()) {
            try {
                this.outQueuesMap =
                    (Map<Long, LinkedBlockingQueue<TransferTask>>) IOUtils.fromXML(outQueuesMapFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        for (LinkedBlockingQueue<TransferTask> queue : this.outQueuesMap.values()) {
            this.outQueuesList.add(queue);
        }
    }

    @PreDestroy
    public void destroy() {
        IOUtils.toXML(getPersistentFile(), this.outQueuesMap);
    }

    private File getPersistentFile() {
        return new File(this.configManager.getStateDirectory(), OUT_QUEUES_MAP_FILE);
    }

    @Override
    public void put(TransferTask tt) throws InterruptedException {

        long id = tt.getResource().getGroupId();
        LinkedBlockingQueue<TransferTask> queue = getQueue(id);
        queue.put(tt);
        openDoor();
    }

    protected void openDoor() {
        if (this.outQueuesList.size() > 0) {
            // opens the door to waiting takers
            // if this is the first transfer task.
            this.latch.countDown();
        }
    }

    private LinkedBlockingQueue<TransferTask> getQueue(long id)
        throws InterruptedException {
        LinkedBlockingQueue<TransferTask> queue = this.outQueuesMap.get(id);

        if (queue == null) {
            queue = new LinkedBlockingQueue<TransferTask>();
            this.outQueuesMap.put(id, queue);
            this.outQueuesList.add(queue);
        }

        return queue;
    }


    @Override
    public synchronized TransferTask take()
        throws InterruptedException {
            this.latch.await();

        int index = this.lastIndex + 1;
        if (index == this.outQueuesList.size()) {
            index = 0;
        }

        LinkedBlockingQueue<TransferTask> q = null;

        TransferTask tt = null;

        while (index != this.lastIndex) {
            q = this.outQueuesList.get(index);
            if (q.size() > 0) {
                tt = q.take();
                this.lastIndex = index;
                break;
            }

            index++;
            if (index == this.outQueuesList.size()) {
                index = 0;
            }
        }

        return tt;
    }
}
