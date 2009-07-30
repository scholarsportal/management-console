
package org.duracloud.servicesutil.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceUninstallerImpl
        implements ServiceUninstaller {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String bundleHome;

    /**
     * {@inheritDoc}
     */
    public void uninstall(String serviceId) throws Exception {
        boolean success = false;
        File homeDir = new File(getBundleHome());
        for (File file : homeDir.listFiles()) {
            if (file.getName().contains(serviceId)) {
                success = file.delete();
            }
        }

        if (!success) {
            String msg = "Unable to uninstall service: " + serviceId;
            log.error(msg);
            throw new Exception(msg);
        }

    }

    public String getBundleHome() {
        return bundleHome;
    }

    public void setBundleHome(String bundleHome) {
        this.bundleHome = bundleHome;
    }

}
