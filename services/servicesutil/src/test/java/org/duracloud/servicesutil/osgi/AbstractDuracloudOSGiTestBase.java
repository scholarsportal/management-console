
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

                "org.apache.commons, com.springsource.org.apache.commons.fileupload, 1.2.0",
                "javax.servlet, com.springsource.javax.servlet, 2.5.0",
                "org.apache.commons, com.springsource.org.apache.commons.io, 1.4.0",
                "org.apache.commons, com.springsource.org.apache.commons.httpclient, 3.1.0",
                "org.apache.commons, com.springsource.org.apache.commons.codec, 1.3.0",

                "org.duraspace, common, 1.0.0",
                "com.thoughtworks.xstream, com.springsource.com.thoughtworks.xstream, 1.3.0",
                "javax.xml.stream, com.springsource.javax.xml.stream, 1.0.1",
                "org.xmlpull, com.springsource.org.xmlpull, 1.1.3.4-O",

                "org.apache.derby, com.springsource.org.apache.derby, 10.5.1000001.764942-duracloud",
                "org.springframework, org.springframework.jdbc, 2.5.6.A",
                "org.springframework, org.springframework.transaction, 2.5.6.A"};
    };

    @Override
    protected Manifest getManifest() {
        return super.createDefaultManifest();
    }
}
