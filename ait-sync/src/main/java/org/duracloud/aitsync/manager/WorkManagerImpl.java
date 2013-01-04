package org.duracloud.aitsync.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.duracloud.aitsync.config.ConfigManager;
import org.duracloud.aitsync.queue.TransferTask;
import org.duracloud.aitsync.repo.Resource;
import org.duracloud.aitsync.store.EndPoint;
import org.duracloud.aitsync.store.EndPointException;
import org.duracloud.aitsync.store.EndPointFactory;
import org.duracloud.aitsync.util.IOUtils;
import org.duracloud.concurrent.BlockingThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
@Component
public class WorkManagerImpl implements WorkManager {

    private ConfigManager configManager;
    private List<Worker> currentWorkers;
    private ThreadPoolExecutor executor;
    private int maxConcurrentWorkers;
    private WorkerImpl incomingWorker;
    private EndPointFactory endPointFactory;

    @Autowired
    public WorkManagerImpl(
        ConfigManager configManager, EndPointFactory endPointFactory) {
        this(configManager,
             configManager.getMaxConcurrentWorkers(),
             endPointFactory);
    }

    public WorkManagerImpl(
        ConfigManager configManager, int maxConcurrentWorkers,
        EndPointFactory endPointFactory) {
        this.configManager = configManager;
        this.currentWorkers = new LinkedList<Worker>();
        this.maxConcurrentWorkers = maxConcurrentWorkers;
        this.endPointFactory = endPointFactory;
    }

    @Override
    public void perform(TransferTask transferTask) {
        performImpl(transferTask);
    }

    @PostConstruct
    @Override
    public synchronized void start() {

        this.executor =
            new BlockingThreadPoolExecutor(this.maxConcurrentWorkers,
                                           this.maxConcurrentWorkers,
                                           10,
                                           TimeUnit.MINUTES);

        new Thread() {
            public void run() {
                File f = getWorkerManagerFile();
                if (f.exists()) {
                    try {
                        List<TransferTask<Resource>> tasks =
                            (List<TransferTask<Resource>>) IOUtils.fromXML(f);
                        for (TransferTask<Resource> t : tasks) {
                            performImpl(t);
                        }
                    } catch (Exception e) {
                        // should never happen.
                        throw new RuntimeException(e);
                    }
                }
            }
        }.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void performImpl(TransferTask<Resource> t) {
        try {
            this.incomingWorker = createWorker(t);
        } catch (EndPointException e) {
            throw new RuntimeException(e);
        }
        this.executor.submit(incomingWorker);

        synchronized (this.currentWorkers) {
            this.currentWorkers.add(incomingWorker);
        }
    }

    protected WorkerImpl createWorker(TransferTask<Resource> t) throws EndPointException {
        EndPoint endPoint = endPointFactory.createEndPoint(t.getResource());
        return new WorkerImpl(t, new WorkerListenerImpl(), endPoint);
    }

    @Override
    public synchronized void pause() {
        for (Worker w : this.currentWorkers) {
            w.pause();
        }
    }

    @Override
    public synchronized void resume() {
        for (Worker w : this.currentWorkers) {
            w.resume();
        }
    }

    private class WorkerListenerImpl implements WorkerListener {

        @Override
        public void done(Worker worker) {
            synchronized (currentWorkers) {
                currentWorkers.remove(worker);
            }
        }
    }

    private File getWorkerManagerFile() {
        return new File(this.configManager.getStateDirectory(),
                        "worker-manager.xml");
    }

    @PreDestroy
    @Override
    public synchronized void shutdown() {

        // create list of current tasks
        List<TransferTask<Resource>> transferTasks =
            new LinkedList<TransferTask<Resource>>();
        for (Worker w : this.currentWorkers) {
            transferTasks.add(w.getTransferTask());
        }

        if (this.incomingWorker != null) {
            transferTasks.add(incomingWorker.getTransferTask());
        }

        // shutdown executor
        this.executor.shutdownNow();

        IOUtils.toXML(getWorkerManagerFile(), transferTasks);

    }

    public List<Worker> getActiveWorkers() {
        return new ArrayList<Worker>(this.currentWorkers);
    }

}
