/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.backup.util;

import java.io.File;

/**
 * This interface defines the contract for a utility that manages filesystem-
 * level functions.
 *
 * @author Andrew Woods
 *         Date: 6/30/11
 */
public interface FileSystemUtil {

    /**
     * This method creates the arg directory if it does not already exist.
     *
     * @param dir to be created
     */
    public void createIfNecessary(File dir);

    /**
     * This method throws a runtime exception if the arg directory is not empty.
     *
     * @param dir to check for emptiness
     */
    public void verifyNotEmpty(File dir);

    /**
     * This method throws a runtime exception if the arg file does not exist.
     *
     * @param file to check for existence
     */
    public void verifyExists(File file);

    /**
     * This method returns true if the arg directories contain files of the
     * same names and those files are of the same size.
     *
     * @param dir0 to compare with the other arg directory
     * @param dir1 to compare with the other arg directory
     * @return true if arg directories are the same
     */
    public boolean directoriesAreEqual(File dir0, File dir1);

    /**
     * This method creates a zip file of the arg directory with the same name
     * as the directory but with the .zip extension.
     * It does not recursively zip nested directories.
     *
     * @param dir to be zipped
     * @return handle to newly created zip file
     */
    public File zip(File dir);

    /**
     * This method unzips the arg zip file in the directory where the arg file
     * is found
     *
     * @param file to unzip
     */
    public void unZipInPlace(File file);
}
