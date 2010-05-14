
package org.duracloud.servicesutil.util;

import org.duracloud.services.common.util.BundleHome;

public interface ServiceUninstaller {

    public abstract void uninstall(String serviceId) throws Exception;

    public abstract BundleHome getBundleHome();

}