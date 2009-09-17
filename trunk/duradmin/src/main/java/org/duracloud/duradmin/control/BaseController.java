package org.duracloud.duradmin.control;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.config.DuradminConfig;
import org.springframework.web.servlet.mvc.SimpleFormController;

public abstract class BaseController extends SimpleFormController {

    protected final Logger log = Logger.getLogger(getClass());

    @Override
    protected boolean isFormSubmission(HttpServletRequest request){
        // Process both GET and POST requests as form submissions
        return true;
    }

    protected ContentStore getContentStore() throws Exception {
        ContentStoreManager contentStoreManager =
            new ContentStoreManager(DuradminConfig.getHost(),
                                    DuradminConfig.getPort());
        return contentStoreManager.getPrimaryContentStore();
    }

    protected ServicesManager getServicesManager() throws Exception {
        ServicesManager servicesManager =
            new ServicesManager(DuradminConfig.getHost(),
                                DuradminConfig.getPort());
        return servicesManager;
    }
}