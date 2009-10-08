
package org.duracloud.duradmin.control;

import java.util.List;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.config.DuradminConfig;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.SpaceUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public abstract class BaseController
        extends SimpleFormController {

    protected final Logger log = Logger.getLogger(getClass());

    private ContentStoreManager contentStoreManager;

    public ContentStore getContentStore() throws ContentStoreException {
        return contentStoreManager.getPrimaryContentStore();
    }


    public ContentStoreManager getContentStoreManager() {
        return contentStoreManager;
    }

    public void setContentStoreManager(ContentStoreManager contentStoreManager) {
        this.contentStoreManager = contentStoreManager;
    }
    
    protected List<Space> getSpaces() throws Exception {
        List<Space> spaces = SpaceUtil.getSpacesList(getContentStore().getSpaces());
        return spaces;
    }

    protected ServicesManager getServicesManager() throws Exception {
        ServicesManager servicesManager =
                new ServicesManager(DuradminConfig.getHost(), DuradminConfig
                        .getPort());
        return servicesManager;
    }


}