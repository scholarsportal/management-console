
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

public class ServiceInstallerImpl
        implements ServiceInstaller {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String bundleHome;

    /**
     * Internal management directory.
     */
    private final String ATTIC = "attic" + File.separator;

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

    private boolean isJar(String name) throws ServiceException {
        return getExtension(name).equalsIgnoreCase(".jar");
    }

    private boolean isZip(String name) throws ServiceException {
        return getExtension(name).equalsIgnoreCase(".zip");
    }

    private String getExtension(String name) throws ServiceException {
        if (name == null) {
            throwServiceException("Filename is null.");
        }

        String ext = name.substring(name.lastIndexOf('.'));
        if (ext == null) {
            throwServiceException("File extension null: '" + name + "'");
        }

        return ext;
    }

    private void storeBundle(String name, InputStream bundle) {
        FileOutputStream atticFile = null;
        try {
            atticFile = FileUtils.openOutputStream(getFromAttic(name));
            IOUtils.copy(bundle, atticFile);

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

    private File getFromAttic(String name) {
        return new File(getAttic().getPath() + File.separator + name);
    }

    private File getAttic() {
        return new File(getBundleHome() + ATTIC);
    }

    private File getHome() {
        return new File(getBundleHome());
    }

    private void throwServiceException(String msg) throws ServiceException {
        log.error("Error: " + msg);
        throw new ServiceException(msg);
    }

    private void throwRuntimeException(String msg, Throwable t) {
        log.error("Error: " + msg, t);
        throw new RuntimeException(msg, t);
    }

    public String getBundleHome() {
        return bundleHome;
    }

    public void setBundleHome(String bundleHome) {
        this.bundleHome = bundleHome;
    }

}
