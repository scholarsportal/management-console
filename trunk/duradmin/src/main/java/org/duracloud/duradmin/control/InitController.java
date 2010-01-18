package org.duracloud.duradmin.control;

import org.duracloud.client.ContentStoreManager;
import org.duracloud.duradmin.config.DuradminConfig;
import org.duracloud.duradmin.contentstore.ContentStoreManagerFactory;
import org.duracloud.duradmin.contentstore.ContentStoreManagerFactoryImpl;
import org.duracloud.duradmin.contentstore.ContentStoreProvider;
import org.duracloud.duradmin.domain.AdminInit;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: Bill Branan
 * Date: Jan 15, 2010
 */
public class InitController extends BaseFormController {

    public InitController() {
        setCommandClass(AdminInit.class);
        setCommandName("initBean");
    }

    @Override
    public Object formBackingObject(HttpServletRequest request)
        throws Exception {
        AdminInit init = (AdminInit) super.formBackingObject(request);

        init.setDuraStoreHost(DuradminConfig.getDuraStoreHost());
        init.setDuraStorePort(DuradminConfig.getDuraStorePort());
        init.setDuraStoreContext(DuradminConfig.getDuraStoreContext());

        init.setDuraServiceHost(DuradminConfig.getDuraServiceHost());
        init.setDuraServicePort(DuradminConfig.getDuraServicePort());
        init.setDuraServiceContext(DuradminConfig.getDuraServiceContext());

        return init;
    }
    
    @Override
    public ModelAndView onSubmit(Object command, BindException bindException)
        throws Exception {
        AdminInit init = (AdminInit) command;
        updateInit(init);
        return new ModelAndView("init", "initBean", init);
    }

    private void updateInit(AdminInit init) throws Exception {
        DuradminConfig.setConfig(init);

        ContentStoreManagerFactory contentStoreManagerFactory =
            new ContentStoreManagerFactoryImpl();
        ContentStoreManager contentStoreManager =
            contentStoreManagerFactory.create();

        ContentStoreProvider contentStoreProvider = getContentStoreProvider();
        contentStoreProvider.setContentStoreManager(contentStoreManager);
    }

}
