
package org.duracloud.services.replication;

import java.util.Dictionary;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.duracloud.services.ComputeService;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class ReplicationService
        implements ComputeService, MessageListener, ManagedService {

    protected static final String STORE_ID = "storeId";

    protected static final String SPACE_ID = "spaceId";

    protected static final String CONTENT_ID = "contentId";

    private static final Logger log =
            LoggerFactory.getLogger(ReplicationService.class);

    private String host;

    private String port;

    private String context;

    private String brokerURL;

    private String fromStoreId;

    private String toStoreId;

    private String replicationType;

    private AbstractMessageListenerContainer jmsContainer;

    private ActiveMQConnectionFactory connectionFactory;

    private Destination destination;

    private Replicator replicator;

    public void start() throws Exception {
        log.info("Starting Replication Service");

        //TODO: Convert to log msg
        System.out.println("**********");
        System.out.println("Starting replication service");
        System.out.println("host: " + host);
        System.out.println("port: " + port);
        System.out.println("context: " + context);
        System.out.println("brokerURL: " + brokerURL);
        System.out.println("fromStoreId: " + fromStoreId);
        System.out.println("toStoreId: " + toStoreId);
        System.out.println("replicationType: " + replicationType);

        jmsContainer = new DefaultMessageListenerContainer();
        connectionFactory.setBrokerURL(brokerURL);
        jmsContainer.setConnectionFactory(connectionFactory);
        jmsContainer.setDestination(destination);
        jmsContainer.setMessageSelector(STORE_ID + " = '" + fromStoreId + "'");
        jmsContainer.setMessageListener(this);
        jmsContainer.start();
        jmsContainer.initialize();

        replicator =
                new Replicator(host, port, context, fromStoreId, toStoreId);

        System.out.print("Listener container started: ");
        System.out.println("jmsContainer.isRunning()");
        System.out.println("**********");
        log.info("Replication Service Listener Started");
    }

    public void stop() throws Exception {
        log.info("Stopping Replication Service");
        jmsContainer.stop();
    }

    public String describe() throws Exception {
        return "Service: " + getClass().getName();
    }

    public ActiveMQConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public void onMessage(Message message) {
        if (log.isDebugEnabled()) {
            log.debug("Message recieved in Replication Service: " + message);
        }
        System.out.println("Message Received: " + message); //TODO: Remove once logging works

        if (message instanceof MapMessage) {
            handleMapMessage((MapMessage) message);
        } else if (message instanceof TextMessage) {
            handleTextMessage(((TextMessage) message));
        } else {
            String error =
                    "Message received which cannot be processed: " + message;
            log.warn(error);
            System.out.println(error); //TODO: Remove once logging works
        }
    }

    private void handleTextMessage(TextMessage message) {
        try {
            String msgText = message.getText();
            String msg =
                    "Text message received in replication service: " + msgText;
            log.warn(msg);
            System.out.println(msg); //TODO: Remove once logging works
        } catch (JMSException je) {
            String error =
                    "Error occured processing text message: " + je.getMessage();
            log.error(error);
            throw new RuntimeException(error, je);
        }
    }

    private void handleMapMessage(MapMessage message) {
        try {
            String spaceId = message.getString(SPACE_ID);
            String contentId = message.getString(CONTENT_ID);
            replicator.replicateContent(spaceId, contentId);
        } catch (JMSException je) {
            String error =
                    "Error occured processing map message: " + je.getMessage();
            log.error(error);
            throw new RuntimeException(error, je);
        }
    }

    @SuppressWarnings("unchecked")
    public void updated(Dictionary properties) throws ConfigurationException {
        // Implementation not needed. Update performed through setters.
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getContext() {
        log.debug("getContext(): " + context);
        return context;
    }

    public void setContext(String context) {
        log.debug("setContext(): " + context);
        this.context = context;
    }

    public String getBrokerURL() {
        return brokerURL;
    }

    public void setBrokerURL(String brokerURL) {
        this.brokerURL = brokerURL;
    }

    public String getFromStoreId() {
        return fromStoreId;
    }

    public void setFromStoreId(String fromStoreId) {
        this.fromStoreId = fromStoreId;
    }

    public String getToStoreId() {
        return toStoreId;
    }

    public void setToStoreId(String toStoreId) {
        this.toStoreId = toStoreId;
    }

    public String getReplicationType() {
        return replicationType;
    }

    public void setReplicationType(String replicationType) {
        this.replicationType = replicationType;
    }

}