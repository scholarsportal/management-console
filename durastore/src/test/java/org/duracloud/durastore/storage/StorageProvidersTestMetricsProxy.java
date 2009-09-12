
package org.duracloud.durastore.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Date;
import java.util.Formatter;

import org.duracloud.storage.domain.StorageException;
import org.duracloud.storage.provider.StorageProvider;

import static org.junit.Assert.assertNotNull;

/**
 * This class captures timing metrics for each executed method of the
 * StorageProvidersTestInterface. The output is written to the 'metricsFileName'
 * in the base directory of the project.
 *
 * @author Andrew Woods
 */
public class StorageProvidersTestMetricsProxy
        implements StorageProvidersTestInterface {

    private final StorageProvidersTestInterface tester;

    private final String metricsFileName = "Storage-Providers-Metrics.txt";

    private final BufferedWriter writer;

    private final Formatter formatter;

    private final int LINE_WIDTH = 80;

    private long startTime;

    private String currentTest;

    public StorageProvidersTestMetricsProxy(StorageProvidersTestInterface tester)
            throws IOException {
        assertNotNull(tester);
        this.tester = tester;

        writer = new BufferedWriter(new FileWriter(new File(metricsFileName)));
        formatter = new Formatter(writer);
        writeProlog();
    }

    private void writeProlog() throws IOException {
        separator('=');
        String text = "Test metrics for: StorageProvidersTest";
        formatter.format("%s", text);

        int width = this.LINE_WIDTH - text.length();
        formatter.format("%1$" + width + "tc%n", new Date());
        separator('=');
    }

    private void separator(char c) {
        StringBuffer sep = new StringBuffer();
        for (int i = 0; i < LINE_WIDTH; ++i) {
            sep.append(c);
        }
        formatter.format("%s%n", sep);
    }

    private void startClock(String testMethod) {
        if (!testMethod.equals(currentTest)) {
            String text = "Running test: " + testMethod;
            formatter.format("%n%n%s", text);

            int width = this.LINE_WIDTH - text.length();
            formatter.format("%1$" + width + "s%n", "elapsed secs");
            separator('-');
        }

        currentTest = testMethod;
        startTime = System.currentTimeMillis();
    }

    private void writeElapsed(StorageProvider provider) {
        long endTime = System.currentTimeMillis();
        float elapsed = (endTime - startTime) / 1000f;

        String text = provider.getClass().getName();
        formatter.format("%1$s", text);

        int width = this.LINE_WIDTH - text.length();
        formatter.format("%1$" + width + ".3f%n", elapsed);
    }

    public void testAddAndGetContent(StorageProvider provider,
                                     String spaceId0,
                                     String contentId0,
                                     String contentId1,
                                     String contentId2) throws Exception {
        startClock("testAddAndGetContent");
        tester.testAddAndGetContent(provider,
                                    spaceId0,
                                    contentId0,
                                    contentId1,
                                    contentId2);
        writeElapsed(provider);
    }

    public void testAddAndGetContentOverwrite(StorageProvider provider,
                                              String spaceId0,
                                              String contentId0,
                                              String contentId1)
            throws Exception {
        startClock("testAddAndGetContentOverwrite");
        tester.testAddAndGetContentOverwrite(provider,
                                             spaceId0,
                                             contentId0,
                                             contentId1);
        writeElapsed(provider);
    }

    public void testAddContentLarge(StorageProvider provider,
                                    String spaceId0,
                                    String contentId0,
                                    String contentId1) throws Exception {
        startClock("testAddContentLarge");
        tester.testAddContentLarge(provider, spaceId0, contentId0, contentId1);
        writeElapsed(provider);
    }

    public void testCreateSpace(StorageProvider provider, String spaceId0)
            throws StorageException {
        startClock("testCreateSpace");
        tester.testCreateSpace(provider, spaceId0);
        writeElapsed(provider);
    }

    public void testDeleteContent(StorageProvider provider,
                                  String spaceId0,
                                  String contentId0,
                                  String contentId1) throws StorageException {
        startClock("testDeleteContent");
        tester.testDeleteContent(provider, spaceId0, contentId0, contentId1);
        writeElapsed(provider);
    }

    public void testDeleteSpace(StorageProvider provider,
                                String spaceId0,
                                String spaceId1) throws StorageException {
        startClock("testDeleteSpace");
        tester.testDeleteSpace(provider, spaceId0, spaceId1);
        writeElapsed(provider);
    }

    public void testGetContentMetadata(StorageProvider provider,
                                       String spaceId0,
                                       String contentId0)
            throws StorageException {
        startClock("testGetContentMetadata");
        tester.testGetContentMetadata(provider, spaceId0, contentId0);
        writeElapsed(provider);
    }

    public void testGetSpaceAccess(StorageProvider provider, String spaceId0)
            throws StorageException {
        startClock("testGetSpaceAccess");
        tester.testGetSpaceAccess(provider, spaceId0);
        writeElapsed(provider);
    }

    public void testGetSpaceContents(StorageProvider provider,
                                     String spaceId0,
                                     String contentId0,
                                     String contentId1) throws StorageException {
        startClock("testGetSpaceContents");
        tester.testGetSpaceContents(provider, spaceId0, contentId0, contentId1);
        writeElapsed(provider);
    }

    public void testGetSpaceMetadata(StorageProvider provider, String spaceId0)
            throws StorageException {
        startClock("testGetSpaceMetadata");
        tester.testGetSpaceMetadata(provider, spaceId0);
        writeElapsed(provider);
    }

    public void testGetSpaces(StorageProvider provider,
                              String spaceId0,
                              String spaceId1) throws StorageException {
        startClock("testGetSpaces");
        tester.testGetSpaces(provider, spaceId0, spaceId1);
        writeElapsed(provider);
    }

    public void testSetContentMetadata(StorageProvider provider,
                                       String spaceId0,
                                       String spaceId1,
                                       String contentId0)
            throws StorageException {
        startClock("testSetContentMetadata");
        tester.testSetContentMetadata(provider, spaceId0, spaceId1, contentId0);
        writeElapsed(provider);
    }

    public void testSetSpaceMetadata(StorageProvider provider, String spaceId0)
            throws StorageException {
        startClock("testSetSpaceMetadata");
        tester.testSetSpaceMetadata(provider, spaceId0);
        writeElapsed(provider);
    }

    public void close() {
        formatter.flush();
        formatter.close();
    }
}
