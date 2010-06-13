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
import org.duracloud.duradmin.util.TagUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class RemoveTagController
        extends TagController {

    protected final Logger log = LoggerFactory.getLogger(RemoveTagController.class);

    @Override
    protected ModelAndView handleTag(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Tag tag,
                                     BindException errors) throws Exception {
        Map<String, String> metadata = getMetadata(tag);
        if (TagUtil.removeTag(tag.getTag(), metadata)) {
            setMetadata(metadata, tag);
            //mark content item list for update if a spaces tag
            if(!StringUtils.hasText(tag.getContentId())){
                ContentItemList list = ContentItemListCache.get(request, tag.getSpaceId(), getContentStoreProvider());
                list.markForUpdate();
            }
            log.info(formatLogMessage("removed", tag));
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("success");
        mav.setViewName("jsonView");
        return mav;
    }

}