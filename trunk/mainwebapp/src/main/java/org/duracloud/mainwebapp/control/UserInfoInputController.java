package org.duracloud.mainwebapp.control;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.duracloud.mainwebapp.domain.cmd.flow.DuraAcctCreateWrapper;
import org.duracloud.mainwebapp.domain.model.Address;
import org.duracloud.mainwebapp.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class UserInfoInputController
        implements Action {

    protected final Logger log = LoggerFactory.getLogger(UserInfoInputController.class);


    public Event execute(RequestContext context) throws Exception {

        String lastName = context.getRequestParameters().get("lastName");
        String firstName = context.getRequestParameters().get("firstName");
        String email = context.getRequestParameters().get("email");
        String phoneWork = context.getRequestParameters().get("phoneWork");
        String phoneOther = context.getRequestParameters().get("phoneOther");
        String street1 = context.getRequestParameters().get("street1");
        String street2 = context.getRequestParameters().get("street2");
        String apt = context.getRequestParameters().get("apt");
        String city = context.getRequestParameters().get("city");
        String state = context.getRequestParameters().get("state");
        String zip = context.getRequestParameters().get("zip");

        Map<String, String> nonNillableFields = new HashMap<String, String>();
        nonNillableFields.put("lastName", lastName);
        nonNillableFields.put("firstName", firstName);
        nonNillableFields.put("email", email);
        nonNillableFields.put("street1", street1);
        nonNillableFields.put("city", city);
        nonNillableFields.put("state", state);
        nonNillableFields.put("zip", zip);
        validateInput(nonNillableFields);

        User user = new User();
        user.setLastname(lastName);
        user.setFirstname(firstName);
        user.setEmail(email);
        user.setPhoneWork(phoneWork);
        user.setPhoneOther(phoneOther);

        Address addr = new Address();
        addr.setStreet1(street1);
        addr.setStreet2(street2);
        addr.setApt(apt);
        addr.setCity(city);
        addr.setState(state);
        addr.setZip(zip);

        DuraAcctCreateWrapper wrapper =
                (DuraAcctCreateWrapper) context.getFlowScope().get("wrapper");

        wrapper.setUser(user);
        wrapper.setAddrShipping(addr);

        return new Event(this, "success");
    }

    private void validateInput(Map<String, String> nonNillableFields)
            throws Exception {
        for (String key : nonNillableFields.keySet()) {
            if (StringUtils.isBlank(nonNillableFields.get(key))) {
                String msg = "Fields with (*) may not be blank: " + key;
                throw new Exception(msg);
            }
        }
    }

}
