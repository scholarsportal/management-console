
package org.duracloud.servicesutil.util;

import java.io.InputStream;

import org.duracloud.services.common.error.ServiceException;

public interface ServiceInstaller {

    public abstract void install(String name, InputStream bundle)
            throws ServiceException;

    public abstract void init() throws Exception;

    public abstract String getBundleHome();

}