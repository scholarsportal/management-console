package org.duracloud.duradmin.control;

import org.duracloud.appconfig.domain.Application;
import org.duracloud.duradmin.config.DuradminConfig;
import org.duracloud.duradmin.domain.SecurityUserCommand;
import org.duracloud.security.DuracloudUserDetailsService;
import org.duracloud.security.domain.SecurityUserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Woods
 *         Date: Apr 23, 2010
 */
public class ManageSecurityUsersController extends BaseFormController {

    private final Logger log = LoggerFactory.getLogger(ManageSecurityUsersController.class);

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
            cmd.removeUser(cmd.getUsername());
        } else if (verb.equalsIgnoreCase("modify")) {
            for(SecurityUserBean user : cmd.getUsers()){
            	if(user.getUsername().equals(cmd.getUsername())){
                	user.setPassword(cmd.getPassword());
                	break;
            	}
            }
        } 

        pushUpdates(cmd.getUsers());
        return new ModelAndView("users", "userBeans", cmd);
    }

    private void pushUpdates(List<SecurityUserBean> users) throws Exception {
        // update duradmin.
        userDetailsService.setUsers(users);

        // update durastore.
        Application durastore = getDuraStoreApp();
        durastore.setSecurityUsers(users);

        // update duraservice
        Application duraservice = getDuraServiceApp();
        duraservice.setSecurityUsers(users);
    }

    private Application getDuraStoreApp() {
        String host = DuradminConfig.getDuraStoreHost();
        String port = DuradminConfig.getDuraStorePort();
        String ctxt = DuradminConfig.getDuraStoreContext();
        return new Application(host, port, ctxt);
    }    

    private Application getDuraServiceApp() {
        String host = DuradminConfig.getDuraServiceHost();
        String port = DuradminConfig.getDuraServicePort();
        String ctxt = DuradminConfig.getDuraServiceContext();
        return new Application(host,port,ctxt);
    }

}