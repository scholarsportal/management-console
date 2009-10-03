
package org.duracloud.duradmin.control;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.config.DuradminConfig;
import org.springframework.web.servlet.mvc.SimpleFormController;

public abstract class BaseController
        extends SimpleFormController {

    protected final Logger log = Logger.getLogger(getClass());

    private ContentStoreManager contentStoreManager;

    public ContentStore getContentStore() throws Exception {
        return contentStoreManager.getPrimaryContentStore();
    }

    @Override
    protected boolean isFormSubmission(HttpServletRequest request) {
        // Process both GET and POST requests as form submissions
        return true;
    }

    public ContentStoreManager getContentStoreManager() {
        return contentStoreManager;
    }

    public void setContentStoreManager(ContentStoreManager contentStoreManager) {
        this.contentStoreManager = contentStoreManager;
    }

    protected ServicesManager getServicesManager() throws Exception {
        ServicesManager servicesManager =
                new ServicesManager(DuradminConfig.getHost(), DuradminConfig
                        .getPort());
        return servicesManager;
    }
}