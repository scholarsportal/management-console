package org.duracloud.services.common.util;

import java.io.File;

/**
 * @author Andrew Woods
 *         Date: Dec 13, 2009
 */
public class BundleHome {
    protected String baseDir;

    /**
     * Internal management directories.
     */
    private final String CONTAINER = "container";
    private final String ATTIC = "attic";
    private final String WORK = "work";

    public BundleHome(String baseDir) {
        this.baseDir = baseDir;
    }

    public File getFromContainer(String name) {
        return new File(getContainer(), name);
    }

    public File getFromAttic(String name) {
        return new File(getAttic(), name);
    }

    public File getServiceWork(String serviceId) {
        return new File(getWork(), serviceId);
    }

    public File getContainer() {
        return new File(getBaseDir(), CONTAINER);
    }

    public File getAttic() {
        return new File(getBaseDir(), ATTIC);
    }

    public File getWork() {
        return new File(getBaseDir(), WORK);
    }

    public File getHome() {
        return new File(getBaseDir());
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
