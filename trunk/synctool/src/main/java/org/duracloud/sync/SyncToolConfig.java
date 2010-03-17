package org.duracloud.sync;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Bill Branan
 * Date: Mar 15, 2010
 */
public class SyncToolConfig {

    private final Logger logger = LoggerFactory.getLogger(SyncToolConfig.class);

    private static final long DEFAULT_POLL_FREQUENCY = 10000;
    private static final int DEFAULT_NUM_THREADS = 10;

    private Options cmdOptions;
    private String host;
    private String port;
    private String context;
    private String username;
    private String password;
    private File backupDir;
    private List<File> syncDirs;
    private long pollFrequency;
    private int numThreads;

    public SyncToolConfig() {
       cmdOptions = new Options();

       Option hostOption =
           new Option("h", "host", true,
                      "the host address of the DuraCloud " +
                      "DuraStore application");
       hostOption.setRequired(true);
       cmdOptions.addOption(hostOption);

       Option portOption =
           new Option("p", "port", true,
                      "the port of the DuraCloud DuraStore application");
       portOption.setRequired(true);
       cmdOptions.addOption(portOption);

       Option contextOption =
           new Option("c", "context", true,
                      "the context of the DuraCloud DuraStore application");
       contextOption.setRequired(true);
       cmdOptions.addOption(contextOption);

       Option usernameOption =
           new Option("u", "username", true,
                      "the username necessary to perform writes to DuraStore");
       usernameOption.setRequired(true);
       cmdOptions.addOption(usernameOption);

       Option passwordOption =
           new Option("p", "password", true,
                      "the password necessary to perform writes to DuraStore");
       passwordOption.setRequired(true);
       cmdOptions.addOption(passwordOption);

       Option backupDirOption =
           new Option("b", "backup-dir", true,
                      "the state of the sync tool is persisted to " +
                      "this directory");
       backupDirOption.setRequired(true);
       cmdOptions.addOption(backupDirOption);

       Option syncDirs =
           new Option("s", "sync-dirs", true,
                      "the directory paths to monitor and sync with DuraCloud");
       syncDirs.setRequired(true);
       syncDirs.setArgs(Option.UNLIMITED_VALUES);
       cmdOptions.addOption(syncDirs);

       Option pollFrequency =
           new Option("f", "poll-frequency", true,
                      "the time (in ms) to wait between each poll of the " +
                      "sync directories");
        pollFrequency.setRequired(false);
        cmdOptions.addOption(pollFrequency);

       Option numThreads =
           new Option("t", "threads", true,
                      "the number of threads in the pool used to manage " +
                      "file transfers");
        numThreads.setRequired(false);
        cmdOptions.addOption(numThreads);        
    }

    public void processCommandLine(String[] args) {
        CommandLine cmd = null;
        try {
            CommandLineParser parser = new PosixParser();
            cmd = parser.parse(cmdOptions, args);
        } catch(ParseException e) {
            printHelp(e.getMessage());
        }

        host = cmd.getOptionValue("h");
        port = cmd.getOptionValue("p");
        context = cmd.getOptionValue("c");
        username = cmd.getOptionValue("u");
        password = cmd.getOptionValue("p");

        backupDir = new File(cmd.getOptionValue("b"));
        if(!backupDir.exists() || !backupDir.isDirectory()) {
            printHelp("Backup Dir paramter must provide the full path " +
                      "to a directory.");
        }

        String[] syncDirPaths = cmd.getOptionValues("s");
        syncDirs = new ArrayList<File>();
        for(String path : syncDirPaths) {
            File syncDir = new File(path);
            if(!syncDir.exists() || !syncDir.isDirectory()) {
                printHelp("Each sync dir value must provide the full path " +
                          "to a directory.");
            }
            syncDirs.add(syncDir);
        }

        if(cmd.hasOption("f")) {
            pollFrequency = Long.valueOf(cmd.getOptionValue("f"));
        } else {
            pollFrequency = DEFAULT_POLL_FREQUENCY;
        }

        if(cmd.hasOption("t")) {
            numThreads = Integer.valueOf(cmd.getOptionValue("t"));
        } else {
            numThreads = DEFAULT_NUM_THREADS;
        }

        backupConfig();
    }

    private void printHelp(String message) {
        logger.info("\n-----------------------\n" +
                    message +
                    "\n-----------------------\n");

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("SyncTool", cmdOptions);
        System.exit(1);
    }

    public void printConfig() {
        StringBuilder config = new StringBuilder();

        config.append("\n-----------------------\n");
        config.append("Sync Tool Configuration");
        config.append("\n-----------------------\n");

        config.append("Sync Directories:\n");
        for(File dir : syncDirs) {
            config.append("  ").append(dir.getAbsolutePath()).append("\n");
        }

        config.append("DuraStore Host: ").append(host).append("\n");
        config.append("DuraStore Port: ").append(port).append("\n");
        config.append("DuraStore Context: ").append(context).append("\n");
        config.append("DuraStore Username: ").append(username).append("\n");
        config.append("DuraStore Password: ").append(password).append("\n");
        config.append("SyncTool Backup Directory: ");
        config.append(backupDir).append("\n");
        config.append("-----------------------\n");

        logger.info(config.toString());
    }

    private void backupConfig() {
        //TODO: Write out config to backup dir
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getContext() {
        return context;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public File getBackupDir() {
        return backupDir;
    }

    public List<File> getSyncDirs() {
        return syncDirs;
    }

    public long getPollFrequency() {
        return pollFrequency;
    }

    public int getNumThreads() {
        return numThreads;
    }
}
