package org.duracloud.duradmin.control;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.config.DuradminConfig;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.MessageUtils;
import org.duracloud.duradmin.util.NavigationUtils;
import org.duracloud.duradmin.util.SpaceUtil;
import org.springframework.web.servlet.ModelAndView;


public class ControllerSupport {
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

    public ModelAndView handle(ModelAndView modelAndView, HttpServletRequest request,
                                      HttpServletResponse response) {
        modelAndView.addObject(NavigationUtils.RETURN_TO_KEY, request.getRequestURI()+"?"+request.getQueryString());
        MessageUtils.addRedirectMessage(modelAndView,request);
        return modelAndView;
    }
    


}
