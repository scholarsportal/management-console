
package org.duracloud.servicesutil.util.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.duracloud.servicesutil.util.ServiceInstaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceInstallerImpl
        implements ServiceInstaller {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String bundleHome;

    public void install(String name, InputStream bundle) throws IOException {
        log.info("bundleHome: '" + bundleHome + "'");

        FileOutputStream outputFile =
                FileUtils.openOutputStream(new File(getBundleHome() + name));

        IOUtils.copy(bundle, outputFile);
        outputFile.close();
    }

    public String getBundleHome() {
        return bundleHome;
    }

    public void setBundleHome(String bundleHome) {
        this.bundleHome = bundleHome;
    }

}
