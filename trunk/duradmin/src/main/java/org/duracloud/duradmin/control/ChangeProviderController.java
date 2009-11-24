
package org.duracloud.duradmin.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.duradmin.contentstore.ContentStoreSelector;
import org.duracloud.duradmin.domain.StorageProvider;
import org.duracloud.duradmin.util.MessageUtils;
import org.springframework.binding.message.Message;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class ChangeProviderController
        extends BaseCommandController {

    protected final Log log = LogFactory.getLog(getClass());

    public ChangeProviderController() {
        setCommandClass(StorageProvider.class);
        setCommandName("storageProvider");
    }

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        StorageProvider storageProvider = (StorageProvider) command;
        String storageProviderId = storageProvider.getStorageProviderId();
        if (!StringUtils.hasText(storageProviderId)) {
            throw new IllegalArgumentException("Storage Provider ID must be provided.");
        }

        ContentStoreSelector selector =
                getContentStoreProvider().getContentStoreSelector();
        selector.setSelectedId(storageProviderId);
        Message message =
                MessageUtils
                        .createMessage("Successfully modified storage provider.");
        return setView(request, new ModelAndView(), message);
    }

}