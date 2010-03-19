package org.duracloud.sync;

import org.duracloud.sync.mgmt.SyncManager;
import org.duracloud.sync.monitor.DirectoryUpdateMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author: Bill Branan
 * Date: Mar 11, 2010
 */
public class SyncTool {

    private final Logger logger = LoggerFactory.getLogger(SyncToolConfig.class);        
    private SyncToolConfig syncConfig;
    private SyncManager syncManager;
    private DirectoryUpdateMonitor dirMonitor;

    private void processCommandLineArgs(String[] args) {
        syncConfig = new SyncToolConfig();
        syncConfig.processCommandLine(args);
        syncConfig.printConfig();       
    }

    private void startSyncManager() {
        syncManager = new SyncManager(syncConfig.getNumThreads(),
                                      syncConfig.getBackupDir(),
                                      syncConfig.getPollFrequency());
        syncManager.beginSync();
    }

    private void startDirWalker() {
        DirWalker dirWalker = new DirWalker(syncConfig.getSyncDirs());
        dirWalker.walkDirs();
    }

    private void startDirMonitor() {
        dirMonitor = new DirectoryUpdateMonitor(syncConfig.getSyncDirs(),
                                                syncConfig.getPollFrequency());
        dirMonitor.startMonitor();
    }

    private void listenForExit() {
        BufferedReader br =
            new BufferedReader(new InputStreamReader(System.in));
        boolean exit = false;
        while(!exit) {
            String input;
            try {
                input = br.readLine();
                if(input.equals("exit") ||
                   input.equals("close") ||
                   input.equals("x")) {
                    exit = true;
                }
            } catch(IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }
        closeSyncTool();
    }

    private void closeSyncTool() {
        syncManager.endSync();
        dirMonitor.stopMonitor();        
    }

    public static void main(String[] args) throws Exception {
        SyncTool syncTool = new SyncTool();
        syncTool.processCommandLineArgs(args);
        syncTool.startSyncManager();
        syncTool.startDirWalker();
        syncTool.startDirMonitor();
        syncTool.listenForExit();
    }
}
