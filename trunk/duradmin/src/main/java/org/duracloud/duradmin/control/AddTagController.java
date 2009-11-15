
package org.duracloud.duradmin.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.duradmin.domain.Tag;
import org.duracloud.duradmin.util.MessageUtils;
import org.duracloud.duradmin.util.MetadataUtils;
import org.duracloud.duradmin.util.TagUtil;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.Severity;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class AddTagController
        extends TagController {

    protected final Log log = LogFactory.getLog(getClass());

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