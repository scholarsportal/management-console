package org.duracloud.aitsync.javaconfig;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.duracloud.aitsync.audit.AuditLogManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

/**
 * @author Daniel Bernstein
 * @created 12/20/2012
 * 
 */
@Configuration
public class MessagingConfig {

    public static final String DEFAULT_TOPIC = "defaultTopic";

    @Bean(initMethod = "start", destroyMethod = "stop")
    public BrokerService broker() throws Exception {
        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:0");
        broker.start();
        return broker;
    }

    @Bean(destroyMethod = "stop")
    public ConnectionFactory jmsFactory() {
        return new PooledConnectionFactory(new ActiveMQConnectionFactory("vm://localhost:61616"));
    }

    @Bean
    public JmsTemplate pubSubJmsTemplate(ConnectionFactory factory,
                                         Destination auditEventTopic) {
        JmsTemplate template = new JmsTemplate(factory);
        template.setDefaultDestination(auditEventTopic);
        template.setPubSubDomain(true);
        return template;
    }

    @Bean
    public Destination defaultTopic() {
        ActiveMQTopic destination = new ActiveMQTopic();
        destination.setPhysicalName(DEFAULT_TOPIC);
        return destination;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public DefaultMessageListenerContainer
        auditLogListenerContainer(ConnectionFactory factory,
                                  AuditLogManager auditLogManager) {
        
        return createMessageListener(factory,
                                     defaultTopic(),
                                     "onMessage",
                                     auditLogManager);
    }

    protected DefaultMessageListenerContainer
        createMessageListener(ConnectionFactory factory,
                              Destination defaultDestination,
                              String defaultListenerMethod,
                              Object listener) {
        DefaultMessageListenerContainer c =
            new DefaultMessageListenerContainer();
        c.setConnectionFactory(factory);
        c.setDestination(defaultDestination);
        MessageListenerAdapter mla = new MessageListenerAdapter();
        mla.setDefaultListenerMethod(defaultListenerMethod);
        mla.setDefaultResponseDestination(defaultTopic());
        mla.setDelegate(listener);

        c.setMessageListener(mla);

        return c;

    }

}
