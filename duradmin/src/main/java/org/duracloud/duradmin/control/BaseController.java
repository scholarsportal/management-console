
package org.duracloud.duradmin.control;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.config.DuradminConfig;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.SpaceUtil;
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
        Collections.sort(spaces, new Comparator<Space>(){
            @Override
            public int compare(Space o1, Space o2) {
                return o1.getSpaceId().compareTo(o2.getSpaceId());
            }
        });
        return spaces;
    }

    protected ServicesManager getServicesManager() throws Exception {
        ServicesManager servicesManager =
                new ServicesManager(DuradminConfig.getHost(), DuradminConfig
                        .getPort());
        return servicesManager;
    }


}