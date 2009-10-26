package org.duracloud.servicesutil.util.internal;

import org.duracloud.services.common.error.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Andrew Woods
 * Date: Oct 1, 2009
 */
public class ServiceInstallBase {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected String bundleHome;

    /**
     * Internal management directory.
     */
    private final String ATTIC = "attic" + File.separator;


    protected boolean isJar(String name) throws ServiceException {
        return getExtension(name).equalsIgnoreCase(".jar");
    }

    protected boolean isZip(String name) throws ServiceException {
        return getExtension(name).equalsIgnoreCase(".zip");
    }

    private String getExtension(String name) throws ServiceException {
        if (name == null) {
            throwServiceException("Filename is null.");
        }

        int index = name.lastIndexOf('.');
        if (index == -1) {
            throwServiceException("No extension: '" + name + "'");
        }

        String ext = name.substring(index);
        if (ext == null) {
            throwServiceException("File extension null: '" + name + "'");
        }

        return ext;
    }

    protected File getFromAttic(String name) {
        return new File(getAttic().getPath() + File.separator + name);
    }

    protected File getAttic() {
        return new File(getBundleHome() + ATTIC);
    }

    protected File getHome() {
        return new File(getBundleHome());
    }

    protected void throwServiceException(String msg) throws ServiceException {
        log.error("Error: " + msg);
        throw new ServiceException(msg);
    }

    public String getBundleHome() {
        return bundleHome;
    }
}
