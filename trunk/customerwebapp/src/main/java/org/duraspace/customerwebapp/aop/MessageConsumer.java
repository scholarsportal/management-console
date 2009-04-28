
package org.duraspace.customerwebapp.aop;

import org.apache.log4j.Logger;

import org.springframework.jms.core.JmsTemplate;

/**
 * This is placeholder class that will be replaced when consumers are written to
 * monitor messaging topics/queues
 *
 * @author Andrew Woods
 */
public class MessageConsumer {

    protected final Logger log = Logger.getLogger(getClass());

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
