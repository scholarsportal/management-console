
package org.duracloud.duradmin.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.duradmin.domain.MetadataItem;
import org.duracloud.duradmin.util.MessageUtils;
import org.duracloud.duradmin.util.MetadataUtils;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.Severity;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class AddMetadataController
        extends MetadataController {

    protected final Log log = LogFactory.getLog(getClass());

    @Override
    protected ModelAndView handleMetadataItem(HttpServletRequest request,
                                              HttpServletResponse response,
                                              MetadataItem metadataItem,
                                              BindException errors)
            throws Exception {
        ModelAndView mav = new ModelAndView();
        Message message;
        Map<String, String> metadata = getMetadata(metadataItem);
        if (MetadataUtils.add(metadataItem.getName(),
                              metadataItem.getValue(),
                              metadata) == null) {
            setMetadata(metadata, metadataItem);
            log.info(formatLogMessage("added", metadataItem));
            message = MessageUtils.createMessage("Successfully added metadata");
        } else {
            message =
                    MessageUtils.createMessage("Metadata value replaced.",
                                               Severity.WARNING);
        }
        return setView(request, mav, message);
    }

}