package org.duracloud.duradmin.control;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.config.DuradminConfig;
import org.duracloud.duradmin.contentstore.ContentStoreProvider;
import org.duracloud.duradmin.util.MessageUtils;
import org.duracloud.duradmin.util.NavigationUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;


public class ControllerSupport {
    private ContentStoreProvider contentStoreProvider;

    public ContentStore getContentStore() throws ContentStoreException {
        return contentStoreProvider.getContentStore();
    }


    public ContentStoreProvider getContentStoreProvider() {
        return contentStoreProvider;
    }

    public void setContentStoreProvider(ContentStoreProvider contentStoreProvider) {
        this.contentStoreProvider = contentStoreProvider;
    }
    
    protected List<String> getSpaces() throws Exception {
        List<String> spaces = getContentStore().getSpaces();
        Collections.sort(spaces);
        return spaces;
    }

    protected ServicesManager getServicesManager() throws Exception {
        ServicesManager servicesManager =
                new ServicesManager(DuradminConfig.getHost(), DuradminConfig
                        .getPort());
        return servicesManager;
    }

    public ModelAndView handle(ModelAndView modelAndView, HttpServletRequest request,
                                      HttpServletResponse response) {
        MessageUtils.addRedirectMessageToModelAndView(modelAndView,request);
        return modelAndView;
    }
    


}
