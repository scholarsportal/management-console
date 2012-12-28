package org.duracloud.aitsync.service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.duracloud.aitsync.watcher.TransferTask;
import org.duracloud.aitsync.watcher.TransferTaskQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
@Component
public class SyncManagerImpl implements SyncManager {
    private Logger log = LoggerFactory.getLogger(SyncManagerImpl.class);
    private TransferTaskQueue queue;

    private Thread queueReader = null;

    private WorkManager workManager;

    private InnerState uninitializedState = new UninitializedState();
    private InnerState readyState = new ReadyState();
    private InnerState runningState = new RunningState();
    private InnerState pausedState = new PausedState();

    private InnerState currentState = uninitializedState;

    @Autowired
    public SyncManagerImpl(TransferTaskQueue queue, WorkManager workManager) {
        this.queue = queue;
        this.workManager = workManager;
    }

    @PostConstruct
    public void init() {
        this.currentState.init();
    }

    private class QueueReader implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    readQueue();
                }
            } catch (InterruptedException e) {
                log.warn("queue reader interrupted.");
            }
        }
    }

    private void changeState(InnerState newState) {
        this.currentState = newState;
    }

    private void throwIllegalStateException() throws IllegalStateException {
        throw new IllegalStateException("Operation is not allowed while in the following state: "
            + getState());
    }

    @PreDestroy
    public void destroy() {
        if (this.queueReader != null) {
            this.queueReader.interrupt();
            this.queueReader = null;
        }

        changeState(this.uninitializedState);
    }

    @Override
    public State getState() {
        return this.currentState.getState();
    }

    @Override
    public synchronized void start() {
        this.currentState.start();
    }

    @Override
    public synchronized void stop() {
        this.currentState.stop();
    }

    @Override
    public void pause() {
        this.currentState.pause();
    }

    @Override
    public void resume() {
        this.currentState.resume();
    }

    private void startImpl() {
        this.queueReader = new Thread(new QueueReader(), "QueueReaderThread");
        this.queueReader.start();
        changeState(this.runningState);
    }

    private void stopImpl() {
        this.queueReader.interrupt();
        this.queueReader = null;
        changeState(this.readyState);
    }

    private void readQueue() throws InterruptedException {
        TransferTask t = this.queue.take();
        workManager.perform(t);
    }

    private abstract class InnerState implements SyncManager {

        @Override
        public void start() {
            throwIllegalStateException();
        }

        @Override
        public void stop() {
            throwIllegalStateException();
        }

        public void init() {
            throwIllegalStateException();
        }

        @Override
        public void pause() {
            throwIllegalStateException();
        }

        @Override
        public void resume() {
            throwIllegalStateException();
        }
    }

    private class UninitializedState extends InnerState {
        @Override
        public State getState() {
            return State.UNITIALIZED;
        }

        @Override
        public void init() {
            changeState(readyState);
        }
    }

    private class RunningState extends InnerState {
        @Override
        public State getState() {
            return State.RUNNING;
        }
        
        @Override
        public void pause() {
            workManager.pause();
            changeState(pausedState);
        }

        @Override
        public void stop() {
            stopImpl();
        }
    }

    private class ReadyState extends InnerState {
        @Override
        public State getState() {
            return State.READY;
        }

        @Override
        public void start() {
            startImpl();
        }
    }

    private class PausedState extends InnerState {
        @Override
        public State getState() {
            return State.PAUSED;
        }

        @Override
        public void resume() {
            workManager.resume();
            changeState(runningState);
        }

        @Override
        public void stop() {
            stopImpl();
        }
    }

}
