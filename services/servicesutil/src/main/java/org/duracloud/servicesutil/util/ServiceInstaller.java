
package org.duracloud.servicesutil.util;

import java.io.InputStream;

import org.duracloud.servicesutil.error.ServiceException;

public interface ServiceInstaller {

    public abstract void install(String name, InputStream bundle)
            throws ServiceException;

    public abstract void init() throws Exception;

    public abstract String getBundleHome();

}