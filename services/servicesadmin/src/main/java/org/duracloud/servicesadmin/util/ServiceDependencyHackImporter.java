
package org.duracloud.servicesadmin.util;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

public class ServiceDependencyHackImporter {

    /**
     * All of the classes instantiated below are used in the servlet-config.xml
     * Spring application context file. In order for the dependencies to
     * automatically be included in the OSGi MANIFEST.MF by the
     * maven-bundle-plugin, they need to appear to be used in a way that the
     * bytecode reflects.
     */
    public void hackToImportSpringServletConfigPackages() {
        ContextLoaderListener hack = new ContextLoaderListener();
        hack.toString();

        OsgiBundleXmlWebApplicationContext hack2 =
                new OsgiBundleXmlWebApplicationContext();
        hack2.toString();

        SimpleUrlHandlerMapping hack3 = new SimpleUrlHandlerMapping();
        hack3.toString();

        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.toString();

    }

}
