
package org.duraspace.customerwebapp.aop;

import javax.jms.Connection;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.jms.support.converter.MessageConverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestIngestMessageConverter
        extends MessagingTestSupport {

    private Connection conn;

    private Session session;

    private MessageConverter converter;

    private Message mapMsg;

    private IngestMessage ingestMsg;

    private final String SPACE_ID = "testSpaceId";

    private final String MIMETYPE = "testMimeType";

    private final String CONTENT_ID = "testContentId";

    @Before
    public void setUp() throws Exception {
        conn = createConnection();
        session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

        converter = new IngestMessageConverter();
        mapMsg = session.createMapMessage();
        mapMsg.setStringProperty(IngestMessageConverter.CONTENT_ID, CONTENT_ID);
        mapMsg.setStringProperty(IngestMessageConverter.MIMETYPE, MIMETYPE);
        mapMsg.setStringProperty(IngestMessageConverter.SPACE_ID, SPACE_ID);

        ingestMsg = new IngestMessage();
        ingestMsg.setContentId(CONTENT_ID);
        ingestMsg.setContentMimeType(MIMETYPE);
        ingestMsg.setSpaceId(SPACE_ID);
    }

    @After
    public void tearDown() throws Exception {
        if (session != null) {
            session.close();
            session = null;
        }
        if (conn != null) {
            conn.close();
            conn = null;
        }
        converter = null;
        mapMsg = null;
        ingestMsg = null;
    }

    @Test
    public void testFromMessage() throws Exception {
        IngestMessage msg = (IngestMessage) converter.fromMessage(mapMsg);
        assertNotNull(msg);
        assertEquals(CONTENT_ID, msg.getContentId());
        assertEquals(MIMETYPE, msg.getContentMimeType());
        assertEquals(SPACE_ID, msg.getSpaceId());
    }

    @Test
    public void testToMessage() throws Exception {
        MapMessage msg = (MapMessage) converter.toMessage(ingestMsg, session);
        assertNotNull(msg);
        assertEquals(CONTENT_ID, msg
                .getStringProperty(IngestMessageConverter.CONTENT_ID));
        assertEquals(MIMETYPE, msg
                .getStringProperty(IngestMessageConverter.MIMETYPE));
        assertEquals(SPACE_ID, msg
                .getStringProperty(IngestMessageConverter.SPACE_ID));
    }

}
