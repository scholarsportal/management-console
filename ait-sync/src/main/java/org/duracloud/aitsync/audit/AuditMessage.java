package org.duracloud.aitsync.audit;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public class AuditMessage implements Serializable {
    private String message;
    private String source;
    private Date timeStamp;

    public AuditMessage(String message, String source, Date timeStamp) {
        super();
        this.message = message;
        this.source = source;
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public String getSource() {
        return source;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

}
