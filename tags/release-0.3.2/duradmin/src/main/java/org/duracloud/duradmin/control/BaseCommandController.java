
package org.duracloud.duradmin.control;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.contentstore.ContentStoreProvider;
import org.duracloud.duradmin.util.MessageUtils;
import org.duracloud.duradmin.util.NavigationUtils;
import org.duracloud.error.ContentStoreException;
import org.springframework.binding.message.Message;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseCommandController
        extends AbstractCommandController {

    protected final Logger log = Logger.getLogger(getClass());

    private ControllerSupport controllerSupport;

    @Override
    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response)
            throws Exception {
        ModelAndView mav = super.handleRequest(request, response);
        return controllerSupport.handle(mav, request, response);
    }

    public ContentStore getContentStore() throws ContentStoreException {
        return controllerSupport.getContentStore();
    }

    public ContentStoreProvider getContentStoreProvider() {
        return controllerSupport.getContentStoreProvider();
    }

    protected ServicesManager getServicesManager() throws Exception {
        return controllerSupport.getServicesManager();
    }

    public void setControllerSupport(ControllerSupport controllerSupport) {
        this.controllerSupport = controllerSupport;
    }

    protected ModelAndView setView(HttpServletRequest request,
                                   ModelAndView mav,
                                   Message message) {
        String url = NavigationUtils.getReturnTo(request);
        url = MessageUtils.appendRedirectMessage(url, message, request);
        RedirectView redirect = new RedirectView(url, false);
        mav.setView(redirect);
        return mav;
    }

}
