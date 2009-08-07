
package org.duracloud.services.replication;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.duracloud.services.ComputeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class ReplicationService
        implements ComputeService, MessageListener {

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

    public ReplicationService(String host,
                              String port,
                              String context,
                              String brokerURL,
                              String fromStoreId,
                              String toStoreId,
                              String replicationType) {
        this.host = host;
        this.port = port;
        this.context = context;
        this.brokerURL = brokerURL;
        this.fromStoreId = fromStoreId;
        this.toStoreId = toStoreId;
        this.replicationType = replicationType;
    }

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

        replicator = new Replicator(host, port, context, fromStoreId, toStoreId);

        System.out.println("Listener container started: " + jmsContainer.isRunning());
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
        if(log.isDebugEnabled()) {
            log.debug("Message recieved in Replication Service: " + message);
        }
        System.out.println("Message Received: " + message); //TODO: Remove once logging works

        if(message instanceof MapMessage) {
            handleMapMessage((MapMessage)message);
        } else if(message instanceof TextMessage) {
            handleTextMessage(((TextMessage)message));
        } else {
          String error = "Message received which cannot be processed: " + message;
          log.warn(error);
          System.out.println(error); //TODO: Remove once logging works
        }
    }

    private void handleTextMessage(TextMessage message) {
        try {
            String msgText = message.getText();
            log.warn("Text message received in replication service: " + msgText);
            System.out.println("Text message received: " + msgText); //TODO: Remove once logging works
        } catch(JMSException je) {
            String error = "Error occured processing text message: " + je.getMessage();
            log.error(error);
            throw new RuntimeException(error, je);
        }
    }

    private void handleMapMessage(MapMessage message) {
        try {
            String spaceId = message.getString(SPACE_ID);
            String contentId = message.getString(CONTENT_ID);
            replicator.replicateContent(spaceId, contentId);
        } catch(JMSException je) {
            String error = "Error occured processing map message: " + je.getMessage();
            log.error(error);
            throw new RuntimeException(error, je);
        }
    }

}