package org.duracloud.aitsync.integration;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author Daniel Bernstein
 * @created 12/17/2012
 * 
 */
public class TestInit {
    @Test
    public void testInit() throws IOException {
        InputStream is = TestInit.class.getResourceAsStream("/test-config.xml");
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer);
        String props = writer.toString();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
            restTemplate.postForEntity("http://localhost:8080/ait-sync/init",
                                       props,
                                       String.class);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode().value());
    }
}
