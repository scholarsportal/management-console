package org.duracloud.aitsync.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * 
 * @author Daniel Bernstein
 * Date:  12/17/2012
 *
 */
@Component
public class AuditLogManagerImpl implements AuditLogManager {
    private Logger log = LoggerFactory.getLogger(AuditLogManagerImpl.class);
    
    public void onMessage(AuditMessage message) {
        log.info("message received: " + message);
    }
}
