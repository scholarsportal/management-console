/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.backup.util.impl;

import org.apache.commons.io.FileUtils;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Andrew Woods
 *         Date: 6/30/11
 */
public class FileSystemUtilImplTest {

    private FileSystemUtilImpl fileSystemUtil;

    private File testDir = new File("target", "unit-test-fsutil");

    @Before
    public void setUp() throws Exception {
        if (!testDir.exists()) {
            Assert.assertTrue("must exist: " + testDir.getAbsolutePath(),
                              testDir.mkdir());
        }

        fileSystemUtil = new FileSystemUtilImpl();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCreateIfNecessary() throws Exception {
        File dir = new File(testDir, "test-dir");
        dir.delete();

        Assert.assertFalse(dir.exists());
        fileSystemUtil.createIfNecessary(dir);
        Assert.assertTrue(dir.exists());
    }

    @Test
    public void testCreateIfNecessaryError() throws Exception {
        File dir = new File(testDir, "bad/dir");
        Assert.assertFalse(dir.exists());

        boolean thrown = false;
        try {
            fileSystemUtil.createIfNecessary(dir);
            Assert.fail("exception expected");
        } catch (DuraCloudRuntimeException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
        Assert.assertFalse(dir.exists());
    }

    @Test
    public void testVerifyNotEmpty() throws Exception {
        File dir = new File(testDir, "test-populated");
        dir.mkdir();
        Assert.assertTrue(dir.getAbsolutePath(), dir.exists());

        FileUtils.writeStringToFile(new File(dir, "f0.txt"), "hello");
        FileUtils.writeStringToFile(new File(dir, "f1.txt"), "monkey");

        fileSystemUtil.verifyNotEmpty(dir);
    }

    @Test
    public void testVerifyNotEmptyError() throws Exception {
        File dir = new File(testDir, "test-empty");

        boolean thrown = false;
        try {
            fileSystemUtil.verifyNotEmpty(dir);
            Assert.fail("exception expected");
        } catch (DuraCloudRuntimeException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testVerifyExists() throws Exception {
        File file = new File(testDir, "exists.txt");
        FileUtils.writeStringToFile(file, "hello");

        Assert.assertTrue(file.exists());
        fileSystemUtil.verifyExists(file);
    }

    @Test
    public void testVerifyExistsError() throws Exception {
        File file = new File(testDir, "not-exist.txt");
        Assert.assertFalse(file.exists());

        boolean thrown = false;
        try {
            fileSystemUtil.verifyExists(file);
        } catch (DuraCloudRuntimeException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
        Assert.assertFalse(file.exists());
    }

    @Test
    public void testDirectoriesAreEqual() throws IOException {
        File base = new File(testDir, "test-dir-equals");
        File dir0 = new File(base, "dir0");
        File dir1 = new File(base, "dir1");

        int NUM_FILES = 5;
        writeFilesToDir(NUM_FILES, dir0);
        writeFilesToDir(NUM_FILES, dir1);

        Assert.assertTrue(fileSystemUtil.directoriesAreEqual(dir0, dir1));
        FileUtils.cleanDirectory(dir0);
        FileUtils.cleanDirectory(dir1);
    }

    @Test
    public void testDirectoriesAreEqualEmpty() throws IOException {
        File base = new File(testDir, "test-dir-equals");
        File dir0 = new File(base, "dir0");
        File dir1 = new File(base, "dir1");

        Assert.assertTrue(fileSystemUtil.directoriesAreEqual(dir0, dir1));
        FileUtils.cleanDirectory(dir0);
        FileUtils.cleanDirectory(dir1);
    }

    @Test
    public void testDirectoriesAreEqualNull() throws IOException {
        File base = new File(testDir, "test-dir-equals");
        File dir0 = new File(base, "dir0");
        File dir1 = null;

        int NUM_FILES = 5;
        writeFilesToDir(NUM_FILES, dir0);

        Assert.assertFalse(fileSystemUtil.directoriesAreEqual(dir0, dir1));
        FileUtils.cleanDirectory(dir0);
    }

    @Test
    public void testDirectoriesAreEqualFalse() throws IOException {
        File base = new File(testDir, "test-dir-equals");
        File dir0 = new File(base, "dir0");
        File dir1 = new File(base, "dir1");

        int NUM_FILES = 5;
        writeFilesToDir(NUM_FILES, dir0);
        writeFilesToDir(NUM_FILES + 3, dir1);

        Assert.assertFalse(fileSystemUtil.directoriesAreEqual(dir0, dir1));
        FileUtils.cleanDirectory(dir0);
        FileUtils.cleanDirectory(dir1);
    }

    @Test
    public void testDirectoriesAreEqualFalseMD5() throws IOException {
        File base = new File(testDir, "test-dir-equals");
        File dir0 = new File(base, "dir0");
        File dir1 = new File(base, "dir1");

        int NUM_FILES = 5;
        writeFilesToDir(NUM_FILES, dir0);
        writeFilesToDir(NUM_FILES, dir1);

        // change the contents of one file
        FileUtils.writeStringToFile(new File(dir1, "file-1.txt"), "new-text");

        Assert.assertFalse(fileSystemUtil.directoriesAreEqual(dir0, dir1));
        FileUtils.cleanDirectory(dir0);
        FileUtils.cleanDirectory(dir1);
    }

    @Test
    public void testZip() throws Exception {
        int NUM_FILES = 5;
        File dir = new File(testDir, "zip-dir");
        writeFilesToDir(NUM_FILES, dir);

        File zipFile = fileSystemUtil.zip(dir);
        Assert.assertNotNull(zipFile);

        // verify
        ZipFile zip = new ZipFile(zipFile);
        Assert.assertEquals(NUM_FILES, zip.size());

        Enumeration zipEntries = zip.entries();
        while (zipEntries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) zipEntries.nextElement();
            Assert.assertTrue(zipEntry.getName().startsWith("file-"));
            Assert.assertTrue("hello".length() < zipEntry.getSize());
        }
    }

    private void writeFilesToDir(int num, File dir) throws IOException {
        for (int i = 0; i < num; ++i) {
            File file = new File(dir, "file-" + i + ".txt");
            FileUtils.writeStringToFile(file, "hello" + i);
        }
    }

    @Test
    public void testUnZip() throws Exception {
        int NUM_FILES = 3;
        File dir = new File(testDir, "unzip-dir");
        writeFilesToDir(NUM_FILES, dir);

        File zipFile = fileSystemUtil.zip(dir);

        File outputDir = new File(dir, "zip-out");
        if (outputDir.exists()) {
            FileUtils.cleanDirectory(outputDir);
        }
        FileUtils.moveFileToDirectory(zipFile, outputDir, true);

        File newZip = new File(outputDir, zipFile.getName());
        Assert.assertTrue("should exist: " + newZip.getAbsolutePath(),
                          newZip.exists());
        fileSystemUtil.unZipInPlace(newZip);

        // verify
        File[] files = outputDir.listFiles();
        Assert.assertEquals(NUM_FILES + 1, files.length);

        for (File file : files) {
            String filename = file.getName();
            if (!filename.equals(newZip.getName())) {
                Assert.assertTrue(filename, filename.startsWith("file-"));
                Assert.assertTrue("hello".length() < file.length());
            }
        }
    }

}
