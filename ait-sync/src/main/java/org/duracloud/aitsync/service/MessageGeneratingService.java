package org.duracloud.aitsync.service;

import java.util.Date;

import org.duracloud.aitsync.audit.AuditMessage;
import org.duracloud.aitsync.javaconfig.MessagingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Daniel Bernstein
 * Date:  12/17/2012
 *
 */
@Component
public class MessageGeneratingService {
    private Logger log =
        LoggerFactory.getLogger(MessageGeneratingService.class);
    
    private JmsTemplate jmsTemplate;

    @Autowired
    public MessageGeneratingService(final JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while(true) {
                        Thread.sleep(10 * 1000);
                        log.info("sending message...");

                        jmsTemplate.send(MessagingConfig.DEFAULT_TOPIC, new MessageCreator() {
                            public javax.jms.Message
                                createMessage(javax.jms.Session session)
                                    throws javax.jms.JMSException {
                                return session.createObjectMessage(new AuditMessage("test",
                                                                                    "test",
                                                                                    new Date()));
                            };
                        });
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
