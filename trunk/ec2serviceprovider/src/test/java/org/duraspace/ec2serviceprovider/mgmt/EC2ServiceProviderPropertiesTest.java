
package org.duraspace.ec2serviceprovider.mgmt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.input.AutoCloseInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class EC2ServiceProviderPropertiesTest {

    private EC2ServiceProviderProperties props;

    private String content;

    private final String provider = "test-amazon-provider";

    private final String signatureMethod = "test-signature-method";

    private final String keyname = "test-keypair";

    private final String imageId = "test-image-id";

    private final int minCount = 3;

    private final int maxCount = 4;

    private final int maxAsyncThreads = 123;

    private final String protocol = "http";

    private final int port = 8080;

    private final String appname = "test-app-name";

    @Before
    public void setUp() throws Exception {
        props = new EC2ServiceProviderProperties();

        props.setProvider(provider);
        props.setSignatureMethod(signatureMethod);
        props.setKeyname(keyname);
        props.setImageId(imageId);
        props.setMinInstanceCount(minCount);
        props.setMaxInstanceCount(maxCount);
        props.setMaxAsyncThreads(maxAsyncThreads);
        props.setWebappProtocol(protocol);
        props.setWebappPort(port);
        props.setWebappName(appname);

        populateContent();
    }

    private void populateContent() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        props.store(out);
        out.flush();
        content = out.toString();
        out.close();
    }

    @After
    public void tearDown() throws Exception {
        props = null;
        content = null;
    }

    @Test
    public void testStore() {
        assertNotNull(content);
        assertTrue(content.indexOf(provider) > 0);
        assertTrue(content.indexOf(signatureMethod) > 0);
        assertTrue(content.indexOf(keyname) > 0);
        assertTrue(content.indexOf(imageId) > 0);
        assertTrue(content.indexOf(Integer.toString(minCount)) > 0);
        assertTrue(content.indexOf(Integer.toString(maxCount)) > 0);
        assertTrue(content.indexOf(Integer.toString(maxAsyncThreads)) > 0);
        assertTrue(content.indexOf(protocol) > 0);
        assertTrue(content.indexOf(Integer.toString(port)) > 0);
        assertTrue(content.indexOf(appname) > 0);
    }

    @Test
    public void testLoad() {
        assertNotNull(content);
        EC2ServiceProviderProperties p = new EC2ServiceProviderProperties();
        p.load(getContentAsStream());

        String pvdr = p.getProvider();
        String sig = p.getSignatureMethod();
        String key = p.getKeyname();
        String img = p.getImageId();
        int minCnt = p.getMinInstanceCount();
        int maxCnt = p.getMaxInstanceCount();
        int threads = p.getMaxAsyncThreads();
        String proto = p.getWebappProtocol();
        int prt = p.getWebappPort();
        String app = p.getWebappName();

        assertNotNull(pvdr);
        assertTrue(pvdr.equals(provider));

        assertNotNull(sig);
        assertTrue(sig.equals(signatureMethod));

        assertNotNull(key);
        assertTrue(key.equals(keyname));

        assertNotNull(img);
        assertTrue(img.equals(imageId));

        assertTrue(minCnt == minCount);
        assertTrue(maxCnt == maxCount);
        assertTrue(threads == maxAsyncThreads);

        assertNotNull(proto);
        assertTrue(proto.equals(protocol));

        assertTrue(prt == port);

        assertNotNull(app);
        assertTrue(app.equals(appname));

    }

    private InputStream getContentAsStream() {
        AutoCloseInputStream in =
                new AutoCloseInputStream(new ByteArrayInputStream(content
                        .getBytes()));
        return in;
    }

}
