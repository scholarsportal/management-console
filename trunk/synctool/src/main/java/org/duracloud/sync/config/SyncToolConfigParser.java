package org.duracloud.sync.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading the configuration parameters for the Sync Tool
 *
 * @author: Bill Branan
 * Date: Mar 15, 2010
 */
public class SyncToolConfigParser {

    private final Logger logger =
        LoggerFactory.getLogger(SyncToolConfigParser.class);

    protected static final String BACKUP_FILE_NAME = "config.bak";

    protected static final int DEFAULT_PORT = 8080;
    protected static final long DEFAULT_POLL_FREQUENCY = 10000;
    protected static final int DEFAULT_NUM_THREADS = 10;
    protected static final String context = "durastore";

    private Options cmdOptions;
    private Options configFileOptions;


    public SyncToolConfigParser() {
       // Command Line Options
       cmdOptions = new Options();

       Option hostOption =
           new Option("h", "host", true,
                      "the host address of the DuraCloud " +
                      "DuraStore application");
       hostOption.setRequired(true);
       cmdOptions.addOption(hostOption);

       Option portOption =
           new Option("p", "port", true,
                      "the port of the DuraCloud DuraStore application " +
                      "(optional, default value is " + DEFAULT_PORT + ")");
       portOption.setRequired(false);
       cmdOptions.addOption(portOption);

       Option usernameOption =
           new Option("u", "username", true,
                      "the username necessary to perform writes to DuraStore");
       usernameOption.setRequired(true);
       cmdOptions.addOption(usernameOption);

       Option passwordOption =
           new Option("w", "password", true,
                      "the password necessary to perform writes to DuraStore");
       passwordOption.setRequired(true);
       cmdOptions.addOption(passwordOption);

       Option spaceId =
           new Option("s", "space", true,
           "the ID of the DuraCloud space where content will be stored");
       spaceId.setRequired(true);
       cmdOptions.addOption(spaceId);

       Option backupDirOption =
           new Option("b", "backup-dir", true,
                      "the state of the sync tool is persisted to " +
                      "this directory");
       backupDirOption.setRequired(true);
       cmdOptions.addOption(backupDirOption);

       Option syncDirs =
           new Option("d", "sync-dirs", true,
                      "the directory paths to monitor and sync with DuraCloud");
       syncDirs.setRequired(true);
       syncDirs.setArgs(Option.UNLIMITED_VALUES);
       cmdOptions.addOption(syncDirs);

       Option pollFrequency =
           new Option("f", "poll-frequency", true,
                      "the time (in ms) to wait between each poll of the " +
                      "sync-dirs (optional, default value is " +
                      DEFAULT_POLL_FREQUENCY + ")");
        pollFrequency.setRequired(false);
        cmdOptions.addOption(pollFrequency);

       Option numThreads =
           new Option("t", "threads", true,
                      "the number of threads in the pool used to manage " +
                      "file transfers (optional, default value is " +
                      DEFAULT_NUM_THREADS + ")");
        numThreads.setRequired(false);
        cmdOptions.addOption(numThreads);

       // Options to use Backup Config
       configFileOptions = new Options();

       Option configFileOption =
           new Option("c", "config-file", true,
                      "read configuration from this file (a file containing " +
                      "the most recently used configuration can be found in " +
                      "the backup-dir, named " + BACKUP_FILE_NAME + ")");
       configFileOption.setRequired(true);
       configFileOptions.addOption(configFileOption);
    }

    public SyncToolConfig processCommandLine(String[] args) {
        SyncToolConfig config = null;
        try {
            config = processConfigFileOptions(args);
        } catch (ParseException e) {
            printHelp(e.getMessage());
        }
        return config;
    }

    protected SyncToolConfig processConfigFileOptions(String[] args)
        throws ParseException {
        try {
            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse(configFileOptions, args);
            
            String configFilePath = cmd.getOptionValue("c");
            File configFile = new File(configFilePath);
            if(!configFile.exists()) {
                throw new ParseException("No configuration file exists at " +
                                         "the indicated path: " +
                                         configFilePath);
            }

            String[] configFileArgs = retrieveConfig(configFile);
            return processStandardOptions(configFileArgs);
        } catch(ParseException e) {
            return processStandardOptions(args);
        }
    }

    protected SyncToolConfig processStandardOptions(String[] args)
        throws ParseException {
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(cmdOptions, args);
        SyncToolConfig config = new SyncToolConfig();

        config.setContext(context);
        config.setHost(cmd.getOptionValue("h"));
        config.setUsername(cmd.getOptionValue("u"));
        config.setPassword(cmd.getOptionValue("w"));
        config.setSpaceId(cmd.getOptionValue("s"));

        if(cmd.hasOption("p")) {
            try {
                config.setPort(Integer.valueOf(cmd.getOptionValue("p")));
            } catch(NumberFormatException e) {
                throw new ParseException("The value for port (-p) must be " +
                                         "a number.");
            }
        } else {
            config.setPort(DEFAULT_PORT);
        }

        File backupDir = new File(cmd.getOptionValue("b"));
        if(!backupDir.exists() || !backupDir.isDirectory()) {
            throw new ParseException("Backup Dir paramter must provide " +
                                     "the full path to a directory.");
        }
        config.setBackupDir(backupDir);

        String[] syncDirPaths = cmd.getOptionValues("d");
        List<File> syncDirs = new ArrayList<File>();
        for(String path : syncDirPaths) {
            File syncDir = new File(path);
            if(!syncDir.exists() || !syncDir.isDirectory()) {
                throw new ParseException("Each sync dir value must provide " +
                                         "the full path to a directory.");
            }
            syncDirs.add(syncDir);
        }
        config.setSyncDirs(syncDirs);

        if(cmd.hasOption("f")) {
            try {
                config.setPollFrequency(Long.valueOf(cmd.getOptionValue("f")));
            } catch(NumberFormatException e) {
                throw new ParseException("The value for poll frequency (-f) " +
                                         "must be a number.");
            }
        } else {
            config.setPollFrequency(DEFAULT_POLL_FREQUENCY);
        }

        if(cmd.hasOption("t")) {
            try {
                config.setNumThreads(Integer.valueOf(cmd.getOptionValue("t")));
            } catch(NumberFormatException e) {
                throw new ParseException("The value for threads (-t) must " +
                                         "be a number.");
            }
        } else {
            config.setNumThreads(DEFAULT_NUM_THREADS);
        }

        backupConfig(backupDir, args);
        return config;
    }

    private void printHelp(String message) {
        logger.info("\n-----------------------\n" +
                    message +
                    "\n-----------------------\n");

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Running SyncTool",
                            cmdOptions);
        formatter.printHelp("ReRunning SyncTool",
                            configFileOptions); 
        System.exit(1);
    }

    public void printConfig(SyncToolConfig toolConfig) {
        String config = toolConfig.getPrintableConfig();
        logger.info(config);
    }

    protected void backupConfig(File backupDir, String[] args) {
        File configBackupFile = new File(backupDir, BACKUP_FILE_NAME);
        try {
            BufferedWriter backupWriter =
                new BufferedWriter(new FileWriter(configBackupFile));
            for(String arg : args) {
                backupWriter.write(arg);
                backupWriter.newLine();
                backupWriter.flush();
            }
            backupWriter.close();
        } catch(IOException e) {
            throw new RuntimeException("Unable to write configuration file " +
                                       "due to: " + e.getMessage(), e);
        }
    }

    public String[] retrieveConfig(File configBackupFile) {
        String[] config = null;
        if(configBackupFile.exists()) {
            ArrayList<String> args = new ArrayList<String>();
            try {
                BufferedReader backupReader =
                    new BufferedReader(new FileReader(configBackupFile));
                String line = backupReader.readLine();
                while(line != null) {
                    args.add(line);
                    line = backupReader.readLine();
                }
                config = args.toArray(new String[0]);
            } catch(IOException e) {
                throw new RuntimeException("Unable to read configuration file " +
                                           "due to: " + e.getMessage(), e);
            }
        }
        return config;
    }
}
