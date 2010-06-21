
package org.duracloud.servicesutil.util;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;


public class ServiceDependencyHackImporter {

    /**
     * All of the classes instantiated below are used in the bundle-context.xml
     * Spring application context file. In order for the dependencies to
     * automatically be included in the OSGi MANIFEST.MF by the
     * maven-bundle-plugin, they need to appear to be used in a way that the
     * bytecode reflects.
     */
    public void hackToImportSpringServletConfigPackages() {

        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.toString();

    }

}
