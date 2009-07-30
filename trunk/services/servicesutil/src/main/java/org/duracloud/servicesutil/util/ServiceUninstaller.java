
package org.duracloud.servicesutil.util;

public interface ServiceUninstaller {

    /**
     * {@inheritDoc}
     */
    public abstract void uninstall(String serviceId) throws Exception;

}