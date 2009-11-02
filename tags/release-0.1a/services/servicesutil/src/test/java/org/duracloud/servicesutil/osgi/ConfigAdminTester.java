
package org.duracloud.servicesutil.osgi;

import java.util.Map;

import org.duracloud.services.ComputeService;
import org.duracloud.servicesutil.util.DuraConfigAdmin;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

import static junit.framework.Assert.assertNotNull;

public class ConfigAdminTester {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final DuraConfigAdmin configAdmin;

    private final ComputeService hello;

    private final String CONFIG_PID =
            "org.duracloud.services.helloservice.config";

    public ConfigAdminTester(DuraConfigAdmin configAdmin, ComputeService hello) {
        assertNotNull(configAdmin);
        assertNotNull(hello);

        this.configAdmin = configAdmin;
        this.hello = hello;
    }

    public void testConfigAdmin() throws Exception {
        StringBuffer sb = new StringBuffer("testing ConfigAdmin\n");

        String newValue = "tester.text";
        String key = "text";

        String origText = hello.describe();
        assertNotNull(sb.toString(), origText);
        sb.append("origText: '" + origText + "'\n");

        Map<String, String> props = configAdmin.getConfiguration(CONFIG_PID);
        assertNotNull(sb.toString(), props);

        props.put(key, "tester.text");

        configAdmin.updateConfiguration(CONFIG_PID, props);

        // Make sure thread updating container props has time to complete.
        Thread.sleep(100);

        String newText = hello.describe();
        assertNotNull(sb.toString(), newText);
        sb.append("newText : '" + newText + "'\n");

        Assert.assertTrue(sb.toString(), !newText.equals(origText));
        Assert.assertTrue(sb.toString(), newText.indexOf(newValue) > -1);

        if (log.isDebugEnabled()) {
            sb.append(configDetailsText());
        }
        log.debug(sb.toString());
    }

    private String configDetailsText() throws Exception, InvalidSyntaxException {
        StringBuffer sb = new StringBuffer();
        Map<String, String> props = configAdmin.getConfiguration(CONFIG_PID);
        sb.append("\tProps: ");
        assertNotNull(props);
        for (String key : props.keySet()) {
            String val = props.get(key);
            sb.append(" [" + key + "|" + val + "]");
        }
        sb.append("\n");
        return sb.toString();
    }

}
