
package org.duraspace.mainwebapp.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.mvc.servlet.AbstractFlowHandler;

public class AcctCreateFlowHandler
        extends AbstractFlowHandler {

    @Override
    public String getFlowId() {
        return "acct-create-flow";
    }

    @Override
    public String handleExecutionOutcome(FlowExecutionOutcome outcome,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        if (outcome.getId().equals("acctCreateConfirmed")) {
            return "/home.htm";
        } else {
            return "/acctCreate/error";
        }
    }

}
