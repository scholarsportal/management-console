/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradmin.control;

import org.duracloud.duradmin.domain.StorageProvider;
import org.duracloud.duradmin.util.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.Message;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ChangeProviderController
        extends BaseCommandController {

    protected final Logger log = LoggerFactory.getLogger(ChangeProviderController.class);

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

        getContentStoreProvider().setSelectedContentStoreId(storageProviderId);
        Message message =
                MessageUtils
                        .createMessage("Successfully modified storage provider.");
        return setView(request, new ModelAndView(), message);
    }

}