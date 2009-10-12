
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
    
    
    public ContentStoreSelector getContentStoreSelector() {
        return contentStoreSelector;
    }

    
    public void setContentStoreSelector(ContentStoreSelector contentStoreSelector) {
        this.contentStoreSelector = contentStoreSelector;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
                                                 HttpServletResponse response)
            throws Exception {
        ModelAndView m = new ModelAndView("home");
        m.addObject("contentStoreSelector", contentStoreSelector);
        return m;
    }
    
    
}