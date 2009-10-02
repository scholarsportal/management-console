package org.duracloud.servicesutil.util.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.duracloud.services.common.error.ServiceException;
import org.duracloud.servicesutil.util.ServiceInstaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrew Woods
 */
public class ServiceInstallerImpl extends ServiceInstallBase
        implements ServiceInstaller {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public void init() throws Exception {
        log.info("initializing SerivceInstallerImpl");
        createHoldingDirs();
    }

    public void install(String name, InputStream bundle)
            throws ServiceException {
        log.info("bundleHome: '" + getBundleHome() + "'");

        ensureFileTypeSupported(name);
        storeBundle(name, bundle);

        if (isJar(name)) {
            installBundleFromAttic(name);
        } else if (isZip(name)) {
            explodeAndInstall(name);
        } else {
            throwServiceException("Unsupported filetype: '" + name + "'");
        }
    }

    private void createHoldingDirs() {
        File home = getHome();
        if (!home.exists()) {
            home.mkdir();
        }

        File attic = getAttic();
        if (!attic.exists()) {
            attic.mkdir();
        }
    }

    private void ensureFileTypeSupported(String name) throws ServiceException {
        if (!isJar(name) && !isZip(name)) {
            throwServiceException("Extension not supported: '" + name + "'");
        }
    }

    private void storeBundle(String name, InputStream bundle) {
        FileOutputStream atticFile = null;
        try {
            atticFile = FileUtils.openOutputStream(getFromAttic(name));
            IOUtils.copy(bundle, atticFile);

            log.debug("bundle name  : " + getFromAttic(name).getName());
            log.debug("bundle length: " + getFromAttic(name).length());

        } catch (IOException e) {
            throwRuntimeException("storeBundle(): '" + name + "'", e);

        } finally {
            if (atticFile != null) {
                try {
                    atticFile.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void installBundleFromAttic(String name) {
        try {
            FileUtils.copyFileToDirectory(getFromAttic(name), getHome());

        } catch (IOException e) {
            throwRuntimeException("installBundle(): '" + name + "'", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void explodeAndInstall(String name) throws ServiceException {
        try {
            ZipFile zip = new ZipFile(getFromAttic(name));
            Enumeration entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String entryName = entry.getName();
                if (isJar(entryName)) {
                    InputStream entryStream = zip.getInputStream(entry);
                    installBundleFromStream(entryName, entryStream);
                } else {
                    log.info("Not installing non-jars: " + entryName);
                }
            }

        } catch (IOException e) {
            throwRuntimeException("explodeAndInstall(): '" + name + "'", e);
        }
    }

    private void installBundleFromStream(String name, InputStream inStream) {
        File installedBundleFile = new File(getBundleHome() + name);
        OutputStream installedBundle = null;
        try {
            installedBundle = new FileOutputStream(installedBundleFile);
            IOUtils.copy(inStream, installedBundle);

        } catch (IOException e) {
            throwRuntimeException("installBundleFromStream(): " + name, e);

        } finally {
            if (installedBundle != null) {
                try {
                    installedBundle.close();
                    inStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void throwRuntimeException(String msg, Throwable t) {
        log.error("Error: " + msg, t);
        throw new RuntimeException(msg, t);
    }

    public void setBundleHome(String bundleHome) {
        this.bundleHome = bundleHome;
    }

}
