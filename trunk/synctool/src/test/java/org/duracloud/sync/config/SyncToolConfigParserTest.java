package org.duracloud.sync.config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author: Bill Branan
 * Date: Mar 25, 2010
 */
public class SyncToolConfigParserTest {

    SyncToolConfigParser syncConfigParser;
    File tempDir;

    @Before
    public void setUp() throws Exception {
        syncConfigParser = new SyncToolConfigParser();
        tempDir = new File(System.getProperty("java.io.tmpdir"));
    }

    @After
    public void tearDown() throws Exception {
        File backupFile = new File(tempDir, SyncToolConfigParser.BACKUP_FILE_NAME);
        if(backupFile.exists()) {
            backupFile.delete();
        }
    }

    @Test
    public void testStandardOptions() throws Exception {
        HashMap<String, String> argsMap = getArgsMap();

        // Process configs, make sure values match
        SyncToolConfig syncConfig =
            syncConfigParser.processStandardOptions(mapToArray(argsMap));
        checkStandardOptions(argsMap, syncConfig);

        // Remove optional params
        argsMap.remove("-f");
        argsMap.remove("-p");
        argsMap.remove("-t");

        // Process configs, make sure optional params are set to defaults
        syncConfig =
            syncConfigParser.processStandardOptions(mapToArray(argsMap));
        assertEquals(SyncToolConfigParser.DEFAULT_POLL_FREQUENCY,
                     syncConfig.getPollFrequency());
        assertEquals(SyncToolConfigParser.DEFAULT_PORT, syncConfig.getPort());
        assertEquals(SyncToolConfigParser.DEFAULT_NUM_THREADS,
                     syncConfig.getNumThreads());

        // Make sure error is thrown on missing required params
        for(String arg : argsMap.keySet()) {
            String failMsg = "An exception should have been thrown due to " +
                             "missing arg: " + arg;
            removeArgFailTest(argsMap, arg, failMsg);
        }

        // Make sure error is thrown when numerical args are not numerical
        String failMsg = "Frequency arg should require a numerical value";
        addArgFailTest(argsMap, "-f", "nonNum", failMsg);
        failMsg = "Port arg should require a numerical value";
        addArgFailTest(argsMap, "-p", "nonNum", failMsg);
        failMsg = "Threads arg should require a numerical value";
        addArgFailTest(argsMap, "-t", "nonNum", failMsg);        
    }

    private HashMap<String, String> getArgsMap() {
        HashMap<String, String> argsMap = new HashMap<String, String>();
        argsMap.put("-b", tempDir.getAbsolutePath());
        argsMap.put("-f", "1000");
        argsMap.put("-h", "localhost");
        argsMap.put("-p", "8088");
        argsMap.put("-s", tempDir.getAbsolutePath());
        argsMap.put("-t", "5");
        argsMap.put("-u", "user");
        argsMap.put("-w", "pass");
        return argsMap;
    }

    private void checkStandardOptions(HashMap<String, String> argsMap,
                                      SyncToolConfig syncConfig) {
        assertEquals(argsMap.get("-b"),
                     syncConfig.getBackupDir().getAbsolutePath());
        assertEquals(argsMap.get("-f"),
                     String.valueOf(syncConfig.getPollFrequency()));
        assertEquals(argsMap.get("-h"), syncConfig.getHost());
        assertEquals(argsMap.get("-p"),
                     String.valueOf(syncConfig.getPort()));
        assertEquals(argsMap.get("-s"),
                     syncConfig.getSyncDirs().get(0).getAbsolutePath());
        assertEquals(argsMap.get("-t"),
                     String.valueOf(syncConfig.getNumThreads()));
        assertEquals(argsMap.get("-u"), syncConfig.getUsername());
        assertEquals(argsMap.get("-w"), syncConfig.getPassword());
    }

    private String[] mapToArray(HashMap<String, String> map) {
        ArrayList<String> list = new ArrayList<String>();
        for(String key : map.keySet()) {
            list.add(key);
            list.add(map.get(key));
        }
        return list.toArray(new String[0]);
    }

    private void addArgFailTest(HashMap<String, String> argsMap,
                                String arg,
                                String value,
                                String failMsg) {
        HashMap<String, String> cloneMap =
            (HashMap<String, String>)argsMap.clone();
        cloneMap.put(arg, value);
        try {
            syncConfigParser.processStandardOptions(mapToArray(cloneMap));
            fail(failMsg);
        } catch(ParseException e) {
            assertNotNull(e);
        }
    }

    private void removeArgFailTest(HashMap<String, String> argsMap,
                                   String arg,
                                   String failMsg) {
        HashMap<String, String> cloneMap =
            (HashMap<String, String>)argsMap.clone();
        cloneMap.remove(arg);
        try {
            syncConfigParser.processStandardOptions(mapToArray(cloneMap));
            fail(failMsg);
        } catch(ParseException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testBackupRestore() throws Exception {
        String[] testArgs = {"-a", "b", "-c", "d", "e", "f", "-g", "-h", "i"};
        syncConfigParser.backupConfig(tempDir, testArgs);

        File backupFile = getBackupFile();
        String[] retrieveArgs = syncConfigParser.retrieveConfig(backupFile);
        for(int i=0; i<testArgs.length; i++) {
            assertTrue(testArgs[i].equals(retrieveArgs[i]));
        }
    }

    private File getBackupFile() {
        File backupFile = new File(tempDir, SyncToolConfigParser.BACKUP_FILE_NAME);
        assertTrue(backupFile.exists());
        return backupFile;
    }

    @Test
    public void testConfigFileOptions() throws Exception {
        HashMap<String, String> argsMap = getArgsMap();

        // Process standard options, which should produce config backup file
        SyncToolConfig syncConfig =
            syncConfigParser.processStandardOptions(mapToArray(argsMap));
        File backupFile = getBackupFile();

        // Create arg map including only -c option, pointing to config file
        argsMap = new HashMap<String, String>();
        argsMap.put("-c", backupFile.getAbsolutePath());

        // Process using config file
        syncConfigParser = new SyncToolConfigParser();
        syncConfig =
            syncConfigParser.processConfigFileOptions(mapToArray(argsMap));
        checkStandardOptions(getArgsMap(), syncConfig);
    }
}
