package org.duracloud.durastore.aop;

import javax.jms.Connection;
import javax.jms.Destination;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

import org.junit.Assert;

import junit.framework.TestCase;

/**
 * This class provides some basic Messaging connectivity support used by
 * sub-classing Tests.
 *
 * @author Andrew Woods
 */
public class MessagingTestSupport
        extends TestCase {

    protected ActiveMQConnectionFactory connectionFactory;

    protected String configString = "tcp://localhost:61617";

    protected final String topicName = "org.duracloud.topic.ingest";

    protected Connection createConnection() throws Exception {
        return getConnectionFactory().createConnection();
    }

    protected Destination createDestination() {
        return new ActiveMQTopic(topicName);
    }

    private ActiveMQConnectionFactory getConnectionFactory() throws Exception {
        if (connectionFactory == null) {
            connectionFactory = createConnectionFactory();
            Assert.assertTrue("Should have created a connection factory!",
                              connectionFactory != null);
        }
        return connectionFactory;
    }

    private ActiveMQConnectionFactory createConnectionFactory()
            throws Exception {
        return new ActiveMQConnectionFactory(configString);
    }

}
