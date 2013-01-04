package org.duracloud.aitsync.queue;

import java.io.IOException;

import org.duracloud.aitsync.io.PauseableInputStream;
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
public class ResourceTransferTask implements TransferTask<Resource> {
    private static Logger log =
        LoggerFactory.getLogger(ResourceTransferTask.class);
    private Resource resource;
    private boolean success = false;
    private int tries = 0;
    private transient PauseableInputStream pis;

    public ResourceTransferTask(Resource resource) {
        this.resource = resource;
    }

    @Override
    public Long getQueueId() {
        return resource.getGroupId();
    }

    @Override
    public int getTries() {
        return tries;
    }

    @Override
    public void pause() {
        PauseableInputStream pis = this.pis;
        if (pis != null) {
            pis.pause();
        }
    }

    @Override
    public void resume() {
        PauseableInputStream pis = this.pis;
        if (pis != null) {
            pis.resume();
        }
    }

    @Override
    public Resource getResource() {
        return this.resource;
    }

    @Override
    public void transferTo(EndPoint endPoint) {
        if(success){
            throw new IllegalStateException("this resource was already transferred.");
        }
        
        try {
            tries++;

            // Create pauseable content stream
            pis = new PauseableInputStream(this.resource.getInputStream());
            this.success =  endPoint.sync(this.resource.getFilename(),
                          this.resource.getMd5(),
                          this.resource.toURL(),
                          pis);
            this.success = true;
        }  catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }

}
