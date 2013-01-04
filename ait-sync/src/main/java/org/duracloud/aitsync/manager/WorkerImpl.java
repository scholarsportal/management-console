package org.duracloud.aitsync.manager;

import org.duracloud.aitsync.queue.TransferTask;
import org.duracloud.aitsync.repo.Resource;
import org.duracloud.aitsync.store.EndPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public class WorkerImpl implements Worker, Runnable {
    private Logger log = LoggerFactory.getLogger(WorkerImpl.class);
    private TransferTask<Resource> transferTask;
    private WorkerListener listener;
    private EndPoint endPoint;

    public WorkerImpl(
        TransferTask<Resource> transferTask, WorkerListener listener,
        EndPoint endPoint) {
        this.transferTask = transferTask;
        this.listener = listener;
        this.endPoint = endPoint;
    }

    @Override
    public void run() {
        log.debug("starting transfer from {} to {}"
            + this.transferTask.getResource().getFilename(), this.endPoint);
        this.transferTask.transferTo(endPoint);
        this.listener.done(this);
    }

    @Override
    public void pause() {
        this.transferTask.pause();
    }

    @Override
    public void resume() {
        this.transferTask.resume();
    }

    @Override
    public TransferTask getTransferTask() {
        return this.transferTask;
    }

}
