package org.duracloud.aitsync.audit;


public interface AuditLogManager {
    public void onMessage(AuditMessage auditMessage);
}
