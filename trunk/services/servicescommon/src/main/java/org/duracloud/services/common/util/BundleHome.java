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
        File serviceWork = new File(getWork(), serviceId);
        if (!serviceWork.exists()) {
            serviceWork.mkdir();
        }
        return serviceWork;
    }

    public File getContainer() {
        File container = new File(getBaseDir(), CONTAINER);
        if (!container.exists()) {
            container.mkdir();
        }
        return container;
    }

    public File getAttic() {
        File attic = new File(getBaseDir(), ATTIC);
        if (!attic.exists()) {
            attic.mkdir();
        }
        return attic;
    }

    public File getWork() {
        File work = new File(getBaseDir(), WORK);
        if (!work.exists()) {
            work.mkdir();
        }
        return work;
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
