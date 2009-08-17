
package org.duracloud.servicesutil.osgi;

import java.util.jar.Manifest;

import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;

public class AbstractDuracloudOSGiTestBase
        extends AbstractConfigurableBundleCreatorTests {

    @Override
    protected String[] getTestBundlesNames() {
        return new String[] {
                "org.duracloud.services, servicesutil, 1.0.0",
                "org.duracloud.services, computeservice, 1.0.0",
                "org.duracloud.services, replicationservice, 1.0.0",
                "org.duracloud.services, helloservice, 1.0.0",

                "org.apache.felix, org.apache.felix.configadmin, 1.0.10",

                "org.apache.commons, com.springsource.org.apache.commons.fileupload, 1.2.0",
                "javax.servlet, com.springsource.javax.servlet, 2.5.0",
                "org.apache.commons, com.springsource.org.apache.commons.io, 1.4.0",
                "org.apache.commons, com.springsource.org.apache.commons.httpclient, 3.1.0",
                "org.apache.commons, com.springsource.org.apache.commons.codec, 1.3.0",

                "org.duracloud, common, 1.0.0",
                "com.thoughtworks.xstream, com.springsource.com.thoughtworks.xstream, 1.3.0",
                "javax.xml.stream, com.springsource.javax.xml.stream, 1.0.1",
                "org.xmlpull, com.springsource.org.xmlpull, 1.1.3.4-O",

                "org.apache.derby, com.springsource.org.apache.derby, 10.5.1000001.764942-duracloud",
                "org.springframework, org.springframework.jdbc, 2.5.6.A",
                "org.springframework, org.springframework.transaction, 2.5.6.A",

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
                "org.apache.xmlcommons, com.springsource.org.apache.xmlcommons, 1.3.3",

                "org.duracloud, storageprovider, 1.0.0",
                "org.duracloud, javaclient, 1.0.0",
                "org.jdom, com.springsource.org.jdom, 1.0.0",
                "commons-lang, commons-lang, 2.4"};
    };

    @Override
    protected Manifest getManifest() {
        return super.createDefaultManifest();
    }

}
