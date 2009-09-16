
package org.duracloud.servicesadmin.osgi;

import org.junit.runner.RunWith;

import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.container.def.options.VMOption;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.SystemPropertyOption;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import junit.framework.Assert;

import static java.lang.System.getProperty;

import static org.ops4j.pax.exam.CoreOptions.mavenConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.exam.CoreOptions.systemProperties;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.profile;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

@RunWith(JUnit4TestRunner.class)
public class AbstractServicesAdminOSGiTestBasePax {

    private static final String FILE_INSTALL_PROP = "felix.fileinstall.dir";

    protected static final String BASE_DIR_PROP = "base.dir";

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public static Option[] configuration() {

        Option systemProperties =
                systemProperties(systemProperty(FILE_INSTALL_PROP),
                                 systemProperty(BASE_DIR_PROP));

        Option bundles = provision(CoreOptions.bundle(getServicesAdminWar()));

        Option frameworks = CoreOptions.frameworks(CoreOptions.equinox());
        // Knopflerfish does not like the felix.configadmin bundle
        // Felix does not like fragments
        //                                                   CoreOptions.knopflerfish(),
        //                                                   CoreOptions.felix());

        VMOption debugOptions =
                vmOption("-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5007");

        return options(systemProperties,
                       bundles,
                       mavenConfiguration(),
                       frameworks,
                       profile("spring.dm"));

    }

    private static SystemPropertyOption systemProperty(String propName) {
        return new SystemPropertyOption(propName).value(getProperty(propName));
    }

    private static String getServicesAdminWar() {
        String baseDir = getProperty(BASE_DIR_PROP);
        Assert.assertNotNull(baseDir);

        String war = "file:" + baseDir + "/target/servicesadmin-1.0.0.war";
        return war;
    }

    protected static String inspectBundlesText(BundleContext ctxt)
            throws Exception {
        Bundle[] bundles = ctxt.getBundles();
        StringBuilder sb = new StringBuilder("bundles:\n");
        for (Bundle bundle : bundles) {
            sb.append("\tbundle");
            String name = bundle.getSymbolicName();
            int state = bundle.getState();
            sb.append(": " + name + ": " + state);
            sb.append(", \n");

            if (name.equals("org.duracloud.services.util")) {
                ServiceReference refs[] = bundle.getRegisteredServices();
                for (ServiceReference ref : refs) {
                    sb.append(getPropsText(ref));
                }
            }
        }
        return sb.toString();
    }

    protected static String getPropsText(ServiceReference ref) {
        StringBuilder sb = new StringBuilder("properties:");
        for (String key : ref.getPropertyKeys()) {
            sb.append("\tprop: [" + key);
            sb.append(":" + ref.getProperty(key) + "]\n");
        }
        return sb.toString();
    }

    protected BundleContext getBundleContext() {
        Assert.assertNotNull(bundleContext);
        return bundleContext;
    }

}
