package org.duracloud.sync.config;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Configuration for the Sync Tool
 *
 * @author: Bill Branan
 * Date: Mar 25, 2010
 */
public class SyncToolConfig implements Serializable {

    private String host;
    private int port;
    private String context;
    private String username;
    private String password;
    private File backupDir;
    private List<File> syncDirs;
    private long pollFrequency;
    private int numThreads;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public File getBackupDir() {
        return backupDir;
    }

    public void setBackupDir(File backupDir) {
        this.backupDir = backupDir;
    }

    public List<File> getSyncDirs() {
        return syncDirs;
    }

    public void setSyncDirs(List<File> syncDirs) {
        this.syncDirs = syncDirs;
    }

    public long getPollFrequency() {
        return pollFrequency;
    }

    public void setPollFrequency(long pollFrequency) {
        this.pollFrequency = pollFrequency;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }
}
