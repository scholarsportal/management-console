
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

    protected static final String SPACE_ID = "spaceId";
    protected static final String CONTENT_ID = "contentId";

    private static final Logger log =
        LoggerFactory.getLogger(ReplicationService.class);

    private String host;
    private String port;
    private String context;
    private String brokerURL;
    private String fromStoreID;
    private String toStoreID;
    private String replicationType;

    private AbstractMessageListenerContainer jmsContainer;
    private ActiveMQConnectionFactory connectionFactory;
    private Destination destination;
    private Replicator replicator;

    public ReplicationService(String host,
                              String port,
                              String context,
                              String brokerURL,
                              String fromStoreID,
                              String toStoreID,
                              String replicationType) {
        this.host = host;
        this.port = port;
        this.context = context;
        this.brokerURL = brokerURL;
        this.fromStoreID = fromStoreID;
        this.toStoreID = toStoreID;
        this.replicationType = replicationType;
    }

    public void start() throws Exception {
        log.info("Starting Replication Service");
        String baseURL = getBaseURL();

        System.out.println("**********");
        System.out.println("Starting replication service");
        System.out.println("baseURL: " + baseURL);
        System.out.println("brokerURL: " + brokerURL);
        System.out.println("fromStoreID: " + fromStoreID);
        System.out.println("toStoreID: " + toStoreID);
        System.out.println("replicationType: " + replicationType);

        jmsContainer = new DefaultMessageListenerContainer();
        connectionFactory.setBrokerURL(brokerURL);
        jmsContainer.setConnectionFactory(connectionFactory);
        jmsContainer.setDestination(destination);
        jmsContainer.setMessageListener(this);
        jmsContainer.start();
        jmsContainer.initialize();

        replicator = new Replicator(baseURL, fromStoreID, toStoreID);

        System.out.println("Listener container started: " + jmsContainer.isRunning());
        System.out.println("**********");
    }

    public void stop() throws Exception {
        log.info("Stopping Replication Service");
        jmsContainer.stop();
    }

    public String describe() throws Exception {
        return "Service: " + getClass().getName();
    }

    private String getBaseURL() {
        return "http://" + host + ":" + port + "/" + context;
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
        System.out.println("--------- Message ---------");
        System.out.println(message);
        System.out.println("---------------------------");

        if(message instanceof MapMessage) {
            handleMapMessage((MapMessage)message);
        } else if(message instanceof TextMessage) {
            handleTextMessage(((TextMessage)message));
        } else {
          String error = "Message received which cannot be processed: " + message;
          log.warn(error);
          System.out.println(error);
        }
    }

    private void handleTextMessage(TextMessage message) {
        try {
            String msgText = message.getText();
            System.out.println("Text message received: " + msgText);
        } catch(JMSException je) {
            String error = "Error occured processing text message: " + je.getMessage();
            log.error(error);
            throw new RuntimeException(error, je);
        }
    }

    private void handleMapMessage(MapMessage message) {
        try {
            String spaceID = message.getString(SPACE_ID);
            String contentID = message.getString(CONTENT_ID);

            System.out.println("Processing Ingest of content");
            System.out.println("spaceID:" + spaceID);
            System.out.println("contentID:" + contentID);

            replicator.replicate(spaceID, contentID);
        } catch(JMSException je) {
            String error = "Error occured processing map message: " + je.getMessage();
            log.error(error);
            throw new RuntimeException(error, je);
        }
    }

}