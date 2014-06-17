/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.db.util.notification.NotificationMgr;
import org.duracloud.notification.Emailer;
import org.easymock.EasyMock;
import org.junit.Before;
import org.springframework.binding.message.Message;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class AmaControllerTestBase extends AmaTestBase {

    protected static final Long TEST_INSTANCE_ID = 1L;
    
    protected NotificationMgr notificationMgr;
    protected Emailer emailer;
    protected BindingResult result;
    protected RedirectAttributes redirectAttributes;
    protected Model model;
    @Before
    public void before() throws Exception {
        super.before();
        notificationMgr = createMock(NotificationMgr.class);
        emailer = createMock(Emailer.class);
        result = createMock(BindingResult.class);
        redirectAttributes = createMock(RedirectAttributes.class);
        model = new ExtendedModelMap();
    }

    protected void setupNoBindingResultErrors() {
        setupHasBindingResultErrors(false);
    }

    protected void setupHasBindingResultErrors(boolean hasErrors) {
        EasyMock.expect(result.hasErrors()).andReturn(hasErrors);
    }

    protected void addFlashAttribute() {
        EasyMock.expect(redirectAttributes.addFlashAttribute(EasyMock.isA(String.class),
                                                             EasyMock.isA(Message.class)))
                .andReturn(redirectAttributes);
    }
}
