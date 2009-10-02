package org.duracloud.servicesutil.util.internal;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;

import org.duracloud.servicesutil.util.ServiceUninstaller;
import org.duracloud.services.common.error.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrew Woods
 */
public class ServiceUninstallerImpl extends ServiceInstallBase
        implements ServiceUninstaller {

    private final Logger log = LoggerFactory.getLogger(getClass());


    public void uninstall(String name) throws Exception {
        log.info("bundleHome: '" + getBundleHome() + "'");

        if (isJar(name)) {
            uninstallBundleFromHomeAndAttic(name);
        } else if (isZip(name)) {
            uninstallBagAndBundles(name);
        } else {
            throwServiceException("Unsupported filetype: '" + name + "'");
        }
    }

    private void uninstallBundleFromHomeAndAttic(String name) throws ServiceException {
        delete(getHome(), name);
        delete(getAttic(), name);
    }

    private void uninstallBagAndBundles(String zipName) throws IOException, ServiceException {
        ZipFile zip = new ZipFile(getFromAttic(zipName));
        Enumeration entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            delete(getHome(), entry.getName());
        }

        delete(getAttic(), zipName);
    }

    private void delete(File dir, String name) throws ServiceException {
        boolean success = false;
        for (File file : dir.listFiles()) {
            String fileName = file.getName();
            log.debug("found in " + dir.getPath() + ": '" + fileName + "'");

            if (fileName.contains(name)) {
                log.debug("about to delete: " + fileName);
                success = file.delete();
                break;
            }
        }

        if (!success) {
            String msg = "Unable to uninstall service: '" + name + "'";
            log.error(msg);
            super.throwServiceException(msg);
        }
    }

    public void setBundleHome(String bundleHome) {
        this.bundleHome = bundleHome;
    }

}
