package org.duracloud.aitsync.watcher;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.duracloud.aitsync.domain.Mapping;
import org.duracloud.aitsync.mapping.MappingManager;
import org.duracloud.aitsync.service.RemoteRepoException;
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
public class TransferTaskQueueFeederImpl implements TransferTaskQueueFeeder {

    private Logger log = LoggerFactory.getLogger(RemoteRepoImpl.class);
    private MappingManager mappingManager;
    private WatchStateManager watchStateManager;
    private RemoteRepo remoteRepo;
    private TransferTaskQueue queue;
    private Thread thread = null;
    private Timer timer;

    @Autowired
    public TransferTaskQueueFeederImpl(
        MappingManager mappingManager, WatchStateManager watchStateManager,
        RemoteRepo remoteRepo, TransferTaskQueue queue) {
        this.mappingManager = mappingManager;
        this.watchStateManager = watchStateManager;
        this.remoteRepo = remoteRepo;
        this.queue = queue;

    }

    private class FetchAndFeedTask extends TimerTask {

        @Override
        public void run() {
            log.debug("executing scheduled fetch and feed task.");
            fetchAndFeed();
        }

    }
    
    public void start(){
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new FetchAndFeedTask(),
                                       1000,
                                       60 * 1000 * 5);
    }
    
    public void stop(){
        this.timer.cancel();
    }

    public void fetchAndFeed() {
        if (thread == null) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    TransferTaskQueueFeederImpl.this.run();
                    thread = null;
                }
            });
            
            thread.start();
        } else {
            log.info("Already fetching and feeding.");
        }
    }

    private void run() {
        log.info("beginning fetch and feed round...");
        // for each mapping
        for (Mapping m : this.mappingManager.getMappings()) {

            long id = m.getArchiveItAccountId();

            try {
                // get the most recent pull date.
                Date last =
                    this.watchStateManager.getState(id)
                                          .getDateOfLastCopiedResource();
                // fetch list from repo
                List<Resource> resources =
                    this.remoteRepo.getResources(id, last);
                // for each item

                Date mostRecent = last;
                for (Resource r : resources) {

                    // add to queue
                    try {
                        queue.put(createTransferTask(r));
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        break;
                    }

                    // determine the most recent resource date in the list.
                    if (r.getCreatedDate().getTime() > mostRecent.getTime()) {
                        mostRecent = r.getCreatedDate();
                    }

                }

                this.watchStateManager.setDateOfLastCopiedResource(id,
                                                                   mostRecent);
            } catch (RemoteRepoException e) {
                log.warn("unable to retrieve resources from Archive-It for account "
                             + id + ". Will retry later.",
                         e);
            }

        }

    }

    private TransferTask createTransferTask(Resource r) {
        return new ResourceTransferTask(r);
    }

}
