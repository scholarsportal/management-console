
package org.duracloud.duradmin.control;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.domain.Space;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public abstract class BaseFormController
        extends SimpleFormController {

    
    
    protected final Logger log = Logger.getLogger(getClass());

    private ControllerSupport controllerSupport = new ControllerSupport();
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request,
                                                 HttpServletResponse response)
            throws Exception {
        ModelAndView mav = super.handleRequest(request, response);
        return controllerSupport.handle(mav,request,response);
    }

    public ContentStore getContentStore() throws ContentStoreException {
        return controllerSupport.getContentStore();
    }


    public ContentStoreManager getContentStoreManager() {
        return controllerSupport.getContentStoreManager();
    }

    public void setContentStoreManager(ContentStoreManager contentStoreManager) {
        this.controllerSupport.setContentStoreManager(contentStoreManager);
    }
    
    protected List<Space> getSpaces() throws Exception {
        return controllerSupport.getSpaces();
    }

    protected ServicesManager getServicesManager() throws Exception {
        return controllerSupport.getServicesManager();
    }


}