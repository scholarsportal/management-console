
package org.duraspace.customerwebapp.aop;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.customerwebapp.rest.RestTestHelper;

/**
 * <pre>
 *
 * This test exercises three elements of the ingest flow:
 * 1. actual content ingest
 * 2. aop publishing ingest event to topic
 * 3. topic consumer asynchronously receiving the event
 *
 * </pre>
 *
 * @author Andrew Woods
 */
public class TestIngestAdvice
        extends MessagingTestSupport
        implements MessageListener {

    private Connection conn;

    private Session session;

    private Destination destination;

    private boolean received;

    private final long MAX_WAIT = 5000;

    private static RestHttpHelper restHelper = new RestHttpHelper();

    private static final String CONTENT = "<junk/>";

    @Override
    @Before
    public void setUp() throws Exception {
        conn = createConnection();
        session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        destination = createDestination();
        received = false;

        // Initialize the Instance
        HttpResponse response = RestTestHelper.initialize();
        int statusCode = response.getStatusCode();
        Assert.assertTrue("status: " + statusCode, statusCode == 200);

        // Add space1
        response = RestTestHelper.addSpace("space1");
        statusCode = response.getStatusCode();
        Assert.assertTrue("status: " + statusCode, statusCode == 201);

    }

    @Override
    @After
    public void tearDown() throws Exception {
        if (conn != null) {
            conn.close();
            conn = null;
        }
        if (session != null) {
            session.close();
            session = null;
        }
        destination = null;

        // Delete space1
        HttpResponse response = RestTestHelper.deleteSpace("space1");
        Assert.assertTrue(response.getStatusCode() == 200);
    }

    @Test
    public void testIngestEventFail() throws Exception {
        boolean successful = false;
        doTestIngestEvent(successful);
    }

    @Test
    public void testIngestEventPass() throws Exception {
        boolean successful = true;
        doTestIngestEvent(successful);
    }

    private void doTestIngestEvent(boolean successful) throws Exception {
        createEventListener();
        publishIngestEvent(successful);
        verifyEventHeard(successful);
    }

    /**
     * This method implements the MessageListener.
     */
    public void onMessage(Message msg) {
        received = true;
    }

    private void createEventListener() throws Exception {
        javax.jms.MessageConsumer consumer =
                session.createConsumer(destination);
        consumer.setMessageListener(this);
        conn.start();
    }

    private void publishIngestEvent(boolean successful) throws Exception {
        String suffix = "/space1/contentGOOD";
        if (!successful) {
            suffix = "/space1x/contentBAD";
        }

        String url = RestTestHelper.getBaseUrl() + suffix;
        restHelper.put(url, CONTENT, false);
    }

    private void verifyEventHeard(boolean successful) throws Exception {
        boolean expired = false;
        long startTime = System.currentTimeMillis();
        while (!received && !expired) {
            Thread.sleep(1000);
            expired = MAX_WAIT < (System.currentTimeMillis() - startTime);
        }
        Assert.assertEquals(received, successful);
    }

}
