
package org.duracloud.servicesutil.osgi;

import org.junit.Before;
import org.junit.runner.RunWith;

import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

import junit.framework.Assert;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.mavenConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.profile;

@RunWith(JUnit4TestRunner.class)
public class AbstractDuracloudOSGiTestBasePax {

    @Inject
    private BundleContext bundleContext;

    @Before
    public void setUp() throws Exception {
        Thread.sleep(2000);
    }

    @Configuration
    public static Option[] configuration() {

        Option bundles =
                provision(bundle("file:src/test/resources/helloservice-1.0.0.jar"),
                          bundle("file:target/servicesutil-1.0.0.jar"));

        Option frameworks =
                CoreOptions.frameworks(CoreOptions.equinox(), CoreOptions
                        .knopflerfish(), CoreOptions.felix());

        return options(bundles,
                       mavenConfiguration(),
                       frameworks,
                       profile("spring.dm"));
    }

    protected BundleContext getBundleContext() {
        Assert.assertNotNull(bundleContext);
        return bundleContext;
    }

}
