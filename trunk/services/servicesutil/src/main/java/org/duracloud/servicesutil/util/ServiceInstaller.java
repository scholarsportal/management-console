
package org.duracloud.servicesutil.util;

import java.io.IOException;
import java.io.InputStream;

public interface ServiceInstaller {

    public abstract void install(String name, InputStream bundle)
            throws IOException;

    public abstract String getBundleHome();

}