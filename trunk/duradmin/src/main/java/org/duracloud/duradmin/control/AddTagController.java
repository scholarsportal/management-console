/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradmin.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.duradmin.contentstore.ContentItemList;
import org.duracloud.duradmin.contentstore.ContentItemListCache;
import org.duracloud.duradmin.domain.Tag;
import org.duracloud.duradmin.util.MessageUtils;
import org.duracloud.duradmin.util.TagUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.Severity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class AddTagController
        extends TagController {

    protected final Logger log = LoggerFactory.getLogger(AddTagController.class);

    @Override
    protected ModelAndView handleTag(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Tag tag,
                                     BindException errors) throws Exception {
        ModelAndView mav = new ModelAndView();
        Message message;
        Map<String, String> metadata = getMetadata(tag);
        if (TagUtil.addTag(tag.getTag(), metadata)) {
            setMetadata(metadata, tag);

            //mark content item list for update if a spaces tag
            if(!StringUtils.hasText(tag.getContentId())){
                ContentItemList contentItemList = ContentItemListCache.get(request, tag.getSpaceId(), getContentStoreProvider());
                contentItemList.markForUpdate();
                mav.addObject("contentItemList", contentItemList);
                mav.addObject("space", contentItemList.getSpace());

            }
            
            log.info(formatLogMessage("added", tag));
            message = MessageUtils.createMessage("Successfully added tag");
        } else {
            message =
                    MessageUtils
                            .createMessage("Tag not added because it already exists.",
                                           Severity.WARNING);
        }
        

        return setView(request, mav, message);
    }

}