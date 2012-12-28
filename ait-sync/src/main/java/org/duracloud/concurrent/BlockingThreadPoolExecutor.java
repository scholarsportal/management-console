package org.duracloud.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class BlockingThreadPoolExecutor extends ThreadPoolExecutor {

    private Semaphore semaphore;

    public BlockingThreadPoolExecutor(
        int corePoolSize, int maxPoolSize, long keepAlive, TimeUnit unit) {
        super(corePoolSize,
              maxPoolSize,
              keepAlive,
              unit,
              new LinkedBlockingQueue<Runnable>(maxPoolSize));
        this.semaphore = new Semaphore(maxPoolSize);
    }

    @Override
    public void execute(Runnable task) {
        boolean acquired = false;
        do {
            try {
                semaphore.acquire();
                acquired = true;
            } catch (InterruptedException e) {
                // wait forever!
            }
        } while (!acquired);

        try {
            super.execute(task);
        } catch (RuntimeException e) {
            // specifically, handle RejectedExecutionException
            semaphore.release();
            throw e;
        } catch (Error e) {
            semaphore.release();
            throw e;
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        semaphore.release();
    }
}
