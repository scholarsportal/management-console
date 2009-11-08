
package org.duracloud.duradmin.control;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.duradmin.contentstore.ContentStoreProvider;
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


    public ContentStoreProvider getContentStoreProvider() {
        return controllerSupport.getContentStoreProvider();
    }

    public void setContentStoreProvider(ContentStoreProvider contentStoreProvider) {
        this.controllerSupport.setContentStoreProvider(contentStoreProvider);
    }
    
    protected List<String> getSpaces() throws Exception {
        return controllerSupport.getSpaces();
    }


}