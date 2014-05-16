/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.db.model.AmaEndpoint;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.notification.NotificationMgr;
import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.account.init.domain.Initable;
import org.duracloud.account.init.xml.AmaInitDocumentBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.InputStream;

/**
 * @author Andrew Woods
 *         Date: Dec 9, 2010
 */
@Controller
@RequestMapping("/init")
public class InitController extends AbstractController {

    private Logger log = LoggerFactory.getLogger(InitController.class);

    @Autowired
    private DuracloudRepoMgr repoMgr;

    @Autowired
    private NotificationMgr notificationMgr;

    @Autowired
    @Qualifier("sysMonitor")
    private Initable systemMonitor;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> initialize(InputStream request) {
        String text = "initialization successful";
        HttpStatus status = HttpStatus.OK;

        AmaConfig config = AmaInitDocumentBinding.createAmaConfigFrom(request);
        try {
            //repoMgr.initialize(config);  no longer need to initialize
            notificationMgr.initialize(config);
            systemMonitor.initialize(config);
            AmaEndpoint.initialize(config.getHost(),
                    config.getPort(),
                    config.getCtxt());

        } catch (Exception e) {
            text = "initialization failed: " + e.getMessage();
            log.error(text);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<String>(text, status);
    }

    // This method is only used by tests. It is not required for injection.
    protected void setRepoMgr(DuracloudRepoMgr repoMgr) {
        this.repoMgr = repoMgr;
    }

    // This method is only used by tests. It is not required for injection.
    protected void setNotificationMgr(NotificationMgr notificationMgr) {
        this.notificationMgr = notificationMgr;
    }

    // This method is only used by tests. It is not required for injection.
    public void setSystemMonitor(Initable systemMonitor) {
        this.systemMonitor = systemMonitor;
    }
}
