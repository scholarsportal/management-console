/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.flow.createaccount;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.account.app.controller.AccountsController;
import org.duracloud.account.util.UserFeedbackUtil;
import org.springframework.binding.message.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.mvc.servlet.AbstractFlowHandler;

/**
 * 
 * @author Daniel Bernstein 
 *         Date: Mar 3, 2012
 * 
 */
@Component(CreateAccountFlowHandler.FLOW_ID)
public class CreateAccountFlowHandler extends AbstractFlowHandler {
    public static final String FLOW_ID = "create-account";
    @Override
    public String getFlowId() {
        return FLOW_ID;
    }
    
    @Override
    public String handleExecutionOutcome(FlowExecutionOutcome outcome,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        Message fb = (Message)outcome.getOutput().get(UserFeedbackUtil.FEEDBACK_KEY);
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        flashMap.put(UserFeedbackUtil.FEEDBACK_KEY, fb);
        RequestContextUtils.getFlashMapManager(request).saveOutputFlashMap(flashMap, request, response);
        return "contextRelative:"+AccountsController.BASE_MAPPING;
    }
}
