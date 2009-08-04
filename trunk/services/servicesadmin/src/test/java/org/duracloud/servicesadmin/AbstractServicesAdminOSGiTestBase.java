
package org.duracloud.servicesadmin;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;
import org.springframework.osgi.util.OsgiStringUtils;

public class AbstractServicesAdminOSGiTestBase
        extends AbstractConfigurableBundleCreatorTests {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected String[] getTestBundlesNames() {
        return new String[] {
                "org.duracloud.services, servicesadmin, 1.0.0, war",
                "org.duracloud.services, servicesutil, 1.0.0",
                "org.duracloud.services, computeservice, 1.0.0",
                "org.duracloud.services, replicationservice, 1.0.0",

                "org.apache.commons, com.springsource.org.apache.commons.fileupload, 1.2.0",
                "javax.servlet, com.springsource.javax.servlet, 2.5.0",
                "org.apache.commons, com.springsource.org.apache.commons.io, 1.4.0",
                "org.apache.commons, com.springsource.org.apache.commons.httpclient, 3.1.0",
                "org.apache.commons, com.springsource.org.apache.commons.codec, 1.3.0",

                "org.springframework.osgi, spring-osgi-web, 1.2.0",
                "org.springframework, org.springframework.web, 2.5.6.A",
                "org.springframework, org.springframework.web.servlet, 2.5.6.A",

                "org.springframework.osgi, spring-osgi-web-extender, 1.2.0",
                "org.springframework.osgi, catalina.osgi, 5.5.23-SNAPSHOT",
                "org.springframework.osgi, catalina.start.osgi, 1.0.0",
                "org.duracloud.services, tomcatconfig, 1.0.0",

                "org.duraspace, common, 1.0.0",
                "com.thoughtworks.xstream, com.springsource.com.thoughtworks.xstream, 1.3.0",
                "javax.xml.stream, com.springsource.javax.xml.stream, 1.0.1",
                "org.xmlpull, com.springsource.org.xmlpull, 1.1.3.4-O",

                "org.apache.derby, com.springsource.org.apache.derby, 10.5.1000001.764942-duracloud",
                "org.springframework, org.springframework.jdbc, 2.5.6.A",
                "org.springframework, org.springframework.transaction, 2.5.6.A",
                "org.junit, com.springsource.junit, 3.8.2",
                
                "org.springframework, spring-core, 2.5.6",
                "org.springframework, spring-beans, 2.5.6",
                "org.springframework, spring-context, 2.5.6",
                "org.springframework, spring-tx, 2.5.6",
                "org.springframework, spring-jms, 2.5.6",
                
                "javax.jms, com.springsource.javax.jms, 1.1.0",
                "org.apache.activemq, com.springsource.org.apache.activemq, 5.2.0",
                "org.apache.geronimo.specs, com.springsource.javax.management.j2ee, 1.0.1",
                "javax.ejb, com.springsource.javax.ejb, 3.0.0",
                "javax.xml.rpc, com.springsource.javax.xml.rpc, 1.1.0",
                "javax.xml.soap, com.springsource.javax.xml.soap, 1.3.0",
                "javax.activation, com.springsource.javax.activation, 1.1.1",
                "org.apache.commons, com.springsource.org.apache.commons.logging, 1.1.1",
                "org.apache.xmlcommons, com.springsource.org.apache.xmlcommons, 1.3.3"};
    };

    public void testInspectBundles() throws Exception {
        Bundle[] bundles = bundleContext.getBundles();
        StringBuilder sb = new StringBuilder("bundles:\n");
        for (Bundle bundle : bundles) {
            sb.append("\tbundle: " + OsgiStringUtils.nullSafeName(bundle));
            String name = bundle.getSymbolicName();
            int state = bundle.getState();
            sb.append(": " + name + ": " + state);
            sb.append(", \n");
        }
        log.debug(sb.toString());
    }

}
