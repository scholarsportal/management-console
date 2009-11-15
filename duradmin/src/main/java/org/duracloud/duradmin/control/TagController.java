
package org.duracloud.duradmin.control;

import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.client.ContentStoreException;
import org.duracloud.duradmin.domain.Tag;
import org.duracloud.duradmin.util.ControllerUtils;
import org.duracloud.duradmin.util.MetadataUtils;
import org.duracloud.duradmin.util.SpaceUtil;
import org.duracloud.duradmin.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public abstract class TagController
        extends BaseCommandController {

    public TagController() {
        setCommandClass(Tag.class);
        setCommandName("tag");
    }

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        Tag tag = (Tag) command;
        ControllerUtils.checkSpaceId(tag.getSpaceId());
        return handleTag(request, response, tag, errors);
    }

    abstract protected ModelAndView handleTag(HttpServletRequest request,
                                              HttpServletResponse response,
                                              Tag tag,
                                              BindException errors)
            throws Exception;

    protected Map<String, String> getMetadata(Tag tag)
            throws ContentStoreException {
        return MetadataUtils.getMetadata(getContentStore(),
                                         tag.getSpaceId(),
                                         tag.getContentId());
    }

    protected void setMetadata(Map<String, String> metadata, Tag tag)
            throws ContentStoreException {
        MetadataUtils.setMetadata(getContentStore(), tag.getSpaceId(), tag
                .getContentId(), metadata);
    }

    protected String formatLogMessage(String command, Tag tag) {
        String contentId = tag.getContentId();
        String contentString =
                (!StringUtils.isEmptyOrAllWhiteSpace(contentId)) ? ": "
                        + contentId : "";
        return MessageFormat.format("successfully {0} from space {1} {2}",
                                    command,
                                    tag.getSpaceId(),
                                    contentString);
    }

}