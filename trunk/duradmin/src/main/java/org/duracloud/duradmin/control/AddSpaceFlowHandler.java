package org.duracloud.duradmin.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.duradmin.util.MessageUtils;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.mvc.servlet.AbstractFlowHandler;


public class AddSpaceFlowHandler extends AbstractFlowHandler {
    
    private static final String SUCCESS_OUTCOME = "success";

    public String handleExecutionOutcome(FlowExecutionOutcome outcome,
                                         HttpServletRequest request, HttpServletResponse response) {
        if (outcome.getId().equals(SUCCESS_OUTCOME)) {
            String redirect =  "contextRelative:/contents.htm?spaceId=" + outcome.getOutput().get("spaceId");
            return MessageUtils.appendRedirectMessage(redirect, MessageUtils.createMessage("Successfully added space!"), request);
        } else {
            return "contextRelative:/spaces.htm";
        }
    }
}