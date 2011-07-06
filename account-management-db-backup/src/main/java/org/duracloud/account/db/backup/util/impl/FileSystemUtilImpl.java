/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.backup.util.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.duracloud.account.db.backup.util.FileSystemUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author Andrew Woods
 *         Date: 6/30/11
 */
public class FileSystemUtilImpl implements FileSystemUtil {

    private final Logger log = LoggerFactory.getLogger(FileSystemUtilImpl.class);

    @Override
    public void createIfNecessary(File dir) {
        if (null == dir) {
            StringBuilder sb = new StringBuilder();
            sb.append("Arg directory is null!");
            log.error(sb.toString());
            throw new DuraCloudRuntimeException(sb.toString());
        }

        if (!dir.exists() && !dir.mkdir()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Directory does not exist and was not created: ");
            sb.append(dir.getAbsolutePath());
            log.error(sb.toString());
            throw new DuraCloudRuntimeException(sb.toString());
        }

        try {
            FileUtils.cleanDirectory(dir);

        } catch (IOException e) {
            log.error("Error cleaning directory: {}, {}",
                      dir.getAbsolutePath(),
                      e.getMessage());
            throw new DuraCloudRuntimeException(e);
        }
    }

    @Override
    public void verifyNotEmpty(File dir) {
        String[] tables = dir.list();
        if (null == tables || tables.length == 0) {
            StringBuilder error = new StringBuilder();
            error.append("Error , directory is empty: ");
            error.append(dir.getAbsolutePath());
            log.error(error.toString());
            throw new DuraCloudRuntimeException(error.toString());
        }
    }

    @Override
    public void verifyExists(File file) {
        if (!file.exists()) {
            StringBuilder error = new StringBuilder();
            error.append("File does not exist: ");
            error.append(file.getAbsolutePath());
            log.error(error.toString());
            throw new DuraCloudRuntimeException(error.toString());
        }
    }

    @Override
    public boolean directoriesAreEqual(File dir0, File dir1) {
        if (null == dir0 && null == dir1) {
            return true;
        }
        if (null == dir0 || null == dir1) {
            return false;
        }

        if (!Arrays.equals(dir0.list(), dir1.list())) {
            return false;
        }

        File[] files0 = dir0.listFiles();
        File[] files1 = dir1.listFiles();

        Map<String, Long> sizeMap = new HashMap<String, Long>();
        for (File file : files0) {
            sizeMap.put(file.getName(), file.length());
        }
        for (File file : files1) {
            long size0 = sizeMap.get(file.getName());
            long size1 = file.length();
            if (size0 != size1) {
                log.debug("Size mismatch for file: {}", file.getAbsolutePath());
                return false;
            }
        }
        return true;
    }

    @Override
    public File zip(File dir) {
        verifyNotEmpty(dir);

        // Ensure another zipfile of the same name does not already exist.
        File zipFile = new File(dir.getParentFile(), dir.getName() + ".zip");
        if (zipFile.isDirectory()) {
            StringBuilder error = new StringBuilder();
            error.append("Zip file name already used as a directory name: ");
            error.append(zipFile.getAbsolutePath());
            log.error(error.toString());
            throw new DuraCloudRuntimeException(error.toString());
        }
        if (zipFile.exists()) {
            FileUtils.deleteQuietly(zipFile);
        }

        String zipFilePath = zipFile.getAbsolutePath();

        // This stream is the destination of the new zipfile.
        OutputStream outputStream = getOutputStream(zipFile);
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

        // Add files of directory to zipfile.
        for (File file : dir.listFiles()) {
            String filename = file.getName();

            if (file.isDirectory()) {
                log.warn("Not adding directory zipEntry {}: ",
                         file.getAbsolutePath());

            } else {
                ZipEntry zipEntry = new ZipEntry(file.getName());
                putNextEntry(zipOutputStream, zipEntry, zipFilePath, filename);

                InputStream fileInputStream = getInputStream(file);
                copyInputStreamToOutputStream(fileInputStream,
                                              zipOutputStream,
                                              filename,
                                              zipFilePath);

                closeEntry(zipOutputStream, filename);
            }

        }
        IOUtils.closeQuietly(zipOutputStream);

        return zipFile;
    }

    private OutputStream getOutputStream(File zipFile) {
        OutputStream outputStream;
        try {
            outputStream = FileUtils.openOutputStream(zipFile);

        } catch (IOException e) {
            StringBuilder error = new StringBuilder();
            error.append("Error opening output stream for zipfile: ");
            error.append(zipFile.getAbsolutePath());
            error.append(" msg: " + e.getMessage());
            log.error(error.toString());
            throw new DuraCloudRuntimeException(error.toString());
        }
        return outputStream;
    }

    private void putNextEntry(ZipOutputStream zipOutputStream,
                              ZipEntry zipEntry,
                              String zipFilePath,
                              String entryName) {
        try {
            zipOutputStream.putNextEntry(zipEntry);

        } catch (IOException e) {
            StringBuilder error = new StringBuilder();
            error.append("Error adding entry to zipfile: ");
            error.append(zipFilePath);
            error.append(" entry: " + entryName);
            error.append(" msg: " + e.getMessage());
            log.error(error.toString());
            throw new DuraCloudRuntimeException(error.toString());
        }
    }

    private InputStream getInputStream(File file) {
        InputStream fileInputStream;
        try {
            fileInputStream = FileUtils.openInputStream(file);

        } catch (IOException e) {
            StringBuilder error = new StringBuilder();
            error.append("Error opening input stream for file: ");
            error.append(file.getAbsolutePath());
            error.append(" msg: " + e.getMessage());
            log.error(error.toString());
            throw new DuraCloudRuntimeException(error.toString());
        }
        return fileInputStream;
    }

    private void copyInputStreamToOutputStream(InputStream inputStream,
                                               OutputStream outputStream,
                                               String inputPath,
                                               String outputPath) {
        try {
            IOUtils.copy(inputStream, outputStream);

        } catch (IOException e) {
            StringBuilder error = new StringBuilder();
            error.append("Error copying input stream to output stream: ");
            error.append(" in: " + inputPath);
            error.append(" out: " + outputPath);
            error.append(" msg: " + e.getMessage());
            log.error(error.toString());
            throw new DuraCloudRuntimeException(error.toString());
        }
    }

    private void closeEntry(ZipOutputStream zipOutputStream, String entryName) {
        try {
            zipOutputStream.closeEntry();

        } catch (IOException e) {
            StringBuilder error = new StringBuilder();
            error.append("Error closing zipEntry: ");
            error.append(entryName);
            error.append(" msg: " + e.getMessage());
            log.error(error.toString());
            throw new DuraCloudRuntimeException(error.toString());
        }
    }

    @Override
    public void unZipInPlace(File file) {
        File dir = file.getParentFile();

        ZipFile zipFile = createZipFile(file);
        Enumeration zipEntries = zipFile.entries();
        while (zipEntries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) zipEntries.nextElement();
            InputStream inputStream = getInputStream(zipFile, zipEntry);

            String entryName = zipEntry.getName();
            File outputFile = new File(dir, entryName);
            OutputStream outputStream = getOutputStream(outputFile, entryName);

            String outputName = outputFile.getAbsolutePath();
            copyInputStreamToOutputStream(inputStream,
                                          outputStream,
                                          entryName,
                                          outputName);

            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }

        close(zipFile);
    }

    private ZipFile createZipFile(File file) {
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(file);

        } catch (IOException e) {
            StringBuilder error = new StringBuilder();
            error.append("Error creating zipFile: ");
            error.append(file.getName());
            error.append(" msg: " + e.getMessage());
            log.error(error.toString());
            throw new DuraCloudRuntimeException(error.toString());
        }
        return zipFile;
    }

    private InputStream getInputStream(ZipFile zipFile, ZipEntry zipEntry) {
        InputStream inputStream;
        try {
            inputStream = zipFile.getInputStream(zipEntry);

        } catch (IOException e) {
            StringBuilder error = new StringBuilder();
            error.append("Error getting input stream from zipEntry: ");
            error.append(zipEntry.getName() + ", from zipFile: ");
            error.append(zipFile.getName());
            error.append(" msg: " + e.getMessage());
            log.error(error.toString());
            throw new DuraCloudRuntimeException(error.toString());
        }
        return inputStream;
    }

    private OutputStream getOutputStream(File outputFile, String zipEntryName) {
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(outputFile);

        } catch (FileNotFoundException e) {
            StringBuilder error = new StringBuilder();
            error.append("Error creating output stream of zipEntry: ");
            error.append(zipEntryName + ", for output file: ");
            error.append(outputFile.getAbsolutePath());
            error.append(" msg: " + e.getMessage());
            log.error(error.toString());
            throw new DuraCloudRuntimeException(error.toString());
        }
        return outputStream;
    }

    private void close(ZipFile zipFile) {
        try {
            zipFile.close();

        } catch (IOException e) {
            StringBuilder error = new StringBuilder();
            error.append("Error closing zipFile: ");
            error.append(zipFile.getName());
            error.append(" msg: " + e.getMessage());
            log.error(error.toString());
            throw new DuraCloudRuntimeException(error.toString());
        }
    }

}
