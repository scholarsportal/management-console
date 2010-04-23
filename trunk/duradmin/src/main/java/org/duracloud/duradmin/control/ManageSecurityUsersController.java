package org.duracloud.duradmin.control;

import org.apache.log4j.Logger;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.model.RootUserCredential;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.duradmin.config.DuradminConfig;
import org.duracloud.duradmin.domain.SecurityUserCommand;
import org.duracloud.security.DuracloudUserDetailsService;
import org.duracloud.security.domain.SecurityUserBean;
import org.duracloud.security.xml.SecurityUsersDocumentBinding;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Woods
 *         Date: Apr 23, 2010
 */
public class ManageSecurityUsersController extends BaseFormController {

    private final Logger log = Logger.getLogger(ManageSecurityUsersController.class);

    private DuracloudUserDetailsService userDetailsService;

    public ManageSecurityUsersController(DuracloudUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        setCommandClass(SecurityUserCommand.class);
        setCommandName("userBeans");
    }

    @Override
    public Object formBackingObject(HttpServletRequest request)
        throws Exception {
        SecurityUserCommand users;
        users = (SecurityUserCommand) super.formBackingObject(request);
        users.setUsers(userDetailsService.getUsers());
        return users;
    }

    @Override
    public ModelAndView onSubmit(Object command, BindException bindException)
        throws Exception {
        SecurityUserCommand cmd = (SecurityUserCommand) command;

        String verb = cmd.getVerb();
        if (verb.equalsIgnoreCase("add")) {
            String username = cmd.getUsername();
            String password = cmd.getPassword();

            List<String> grants = new ArrayList<String>();
            grants.add("ROLE_USER");

            cmd.addUser(new SecurityUserBean(username, password, grants));

        } else if (verb.equalsIgnoreCase("remove")) {
            // FIXME: implement in jsp
            cmd.removeUser(cmd.getUsername());

        } else if (verb.equalsIgnoreCase("modify")) {
            // FIXME: implement in jsp
            // do update on users of cmd object
        }

        pushUpdates(cmd.getUsers());
        return new ModelAndView("users", "userBeans", cmd);
    }

    private void pushUpdates(List<SecurityUserBean> users) throws Exception {
        // update duradmin.
        userDetailsService.setUsers(users);

        RestHttpHelper restHelper = new RestHttpHelper(new RootUserCredential());
        String xml = SecurityUsersDocumentBinding.createDocumentFrom(users);

        // update durastore & duraservice.
        updateDuraStoreSecurity(restHelper, xml);
        updateDuraServiceSecurity(restHelper, xml);
    }

    private void updateDuraStoreSecurity(RestHttpHelper restHelper, String xml)
        throws Exception {
        String host = DuradminConfig.getDuraStoreHost();
        String port = DuradminConfig.getDuraStorePort();
        String ctxt = DuradminConfig.getDuraStoreContext();
        String url = "http://" + host + ":" + port + "/" + ctxt + "/security";
        updateSecurity(restHelper, xml, url);
    }

    private void updateDuraServiceSecurity(RestHttpHelper restHelper,
                                           String xml) throws Exception {

        String host = DuradminConfig.getDuraServiceHost();
        String port = DuradminConfig.getDuraServicePort();
        String ctxt = DuradminConfig.getDuraServiceContext();
        String url = "http://" + host + ":" + port + "/" + ctxt + "/security";
        updateSecurity(restHelper, xml, url);
    }

    private void updateSecurity(RestHttpHelper restHelper,
                                String xml,
                                String url) throws Exception {
        Map<String, String> headers = null;
        HttpResponse response = restHelper.post(url, xml, headers);
        if (null == response || response.getStatusCode() != 200) {
            StringBuilder msg = new StringBuilder();
            msg.append("Error initializing security: ");
            msg.append(url);
            msg.append(" (");
            msg.append(response.getStatusCode());
            msg.append(")");
            log.error(msg);
            throw new DuraCloudRuntimeException(msg.toString());
        }
    }

}