package org.duracloud.durastore.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * This is placeholder class that will be replaced when consumers are written to
 * monitor messaging topics/queues
 *
 * @author Andrew Woods
 */
public class MessageConsumer {

    protected final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

    private JmsTemplate jmsTemplate;

    public void onIngest(IngestMessage ingestMsg) {
        log.info("message consumed from topic: " + ingestMsg);
    }

    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

}
