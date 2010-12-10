/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.db.DuracloudRepoMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> initialize(InputStream request) {
        String text = "initialization successful";
        HttpStatus status = HttpStatus.OK;
        try {
            repoMgr.initialize(request);

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
}
