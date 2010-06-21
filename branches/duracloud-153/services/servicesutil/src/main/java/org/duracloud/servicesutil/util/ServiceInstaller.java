
package org.duracloud.servicesutil.util;

import org.duracloud.services.common.error.ServiceException;
import org.duracloud.services.common.util.BundleHome;

import java.io.InputStream;

public interface ServiceInstaller {

    public abstract void install(String name, InputStream bundle)
            throws ServiceException;

    public abstract BundleHome getBundleHome();

}