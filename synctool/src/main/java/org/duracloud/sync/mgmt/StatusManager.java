package org.duracloud.sync.mgmt;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Singleton class which tracks the status of the sync queue
 *
 * @author: Bill Branan
 * Date: Apr 2, 2010
 */
public class StatusManager {

    private long queue;
    private long completed;
    private List<File> failed;
    private String startTime;

    private static StatusManager instance;

    public static StatusManager getInstance() {
        if(instance == null) {
            instance = new StatusManager();
        }
        return instance;
    }

    /*
     * Not to be used outside of tests
     */
    protected StatusManager() {
        queue = 0;
        completed = 0;
        failed = new ArrayList<File>();
        startTime =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public long getQueueSize() {
        return queue;
    }

    public synchronized void addedToQueue() {
        queue++;
    }

    public long getCompleted() {
        return completed;
    }

    public synchronized void completedProcessing() {
        completed++;
        queue--;
    }

    public List<File> getFailed() {
        return failed;
    }

    public synchronized void failedProcessing(File file) {
        failed.add(file);
        queue--;
    }

    public String getPrintableStatus() {
        StringBuilder status = new StringBuilder();

        status.append("\n--------------------------------------\n");
        status.append(" Sync Tool Status");
        status.append("\n--------------------------------------\n");
        status.append("Start Time: " + startTime + "\n");
        status.append("Sync Queue Size: " + queue + "\n");
        status.append("Successful Syncs: " + completed + "\n");
        status.append("Failed Syncs: " + failed.size() + "\n");
        for(File failedFile : failed) {
            status.append("  " + failedFile.getAbsolutePath() + "\n");    
        }
        status.append("--------------------------------------\n");
        return status.toString();
    }

}
