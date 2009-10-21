
package org.duracloud.duradmin.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.duradmin.contentstore.ContentStoreSelector;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * @author dbernstein@duraspace.org
 */
public class HomeController
        extends AbstractController {

    private ContentStoreSelector contentStoreSelector;
    private ControllerSupport controllerSupport = new ControllerSupport();
    
    public ContentStoreSelector getContentStoreSelector() {
        return contentStoreSelector;
    }

    
    public void setContentStoreSelector(ContentStoreSelector contentStoreSelector) {
        this.contentStoreSelector = contentStoreSelector;
    }

    @Override
    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response)
            throws Exception {
        
       ModelAndView mav = super.handleRequest(request, response);
       return controllerSupport.handle(mav,request,response);
    }
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
                                                 HttpServletResponse response)
            throws Exception {
        ModelAndView m = new ModelAndView("home");
        return m;
    }
    
    
}