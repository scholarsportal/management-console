
package org.duracloud.duradmin.webflow.content;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;
import org.duracloud.duradmin.contentstore.ContentStoreProvider;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.MessageUtils;
import org.duracloud.duradmin.util.NavigationUtils;
import org.duracloud.duradmin.util.SpaceUtil;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.mvc.servlet.AbstractFlowHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

public class AddContentItemFlowHandler
        extends AbstractFlowHandler {

    private static final String SUCCESS_OUTCOME = "success";

    private static final String CONTENT_ITEM = "contentItem";

    private static final String SPACE = "space";

    private static Log log = LogFactory.getLog(AddContentItemFlowHandler.class);

    private transient ContentStoreProvider contentStoreProvider;

    public ContentStoreProvider getContentStoreProvider() {
        return contentStoreProvider;
    }

    public void setContentStoreProvider(ContentStoreProvider contentStoreProvider) {
        this.contentStoreProvider = contentStoreProvider;
    }

    private ContentStore getContentStore() throws ContentStoreException {
        return contentStoreProvider.getContentStore();
    }

    private Space getSpace(String spaceId) throws Exception {
        Space space = new Space();
        org.duracloud.domain.Space cloudSpace =
                getContentStore().getSpace(spaceId, null, 0, null);
        SpaceUtil.populateSpace(space, cloudSpace);
        return space;
    }

    @Override
    public MutableAttributeMap createExecutionInputMap(HttpServletRequest request) {
        MutableAttributeMap map = super.createExecutionInputMap(request);
        try {
            if (map == null) {
                map = new LocalAttributeMap();
            }

            NavigationUtils.setReturnTo(request, map);

            String spaceId = request.getParameter("spaceId");
            Space space = getSpace(spaceId);
            map.put(SPACE, space);
        } catch (Exception ex) {
            log.error(ex);
        }
        return map;
    }

    public String handleExecutionOutcome(FlowExecutionOutcome outcome,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        String returnTo = NavigationUtils.getReturnTo(outcome);
        Space space = (Space) outcome.getOutput().get(SPACE);
        ContentItem contentItem =
                (ContentItem) outcome.getOutput().get(CONTENT_ITEM);

        String outcomeUrl = null;

        if (outcome.getId().equals(SUCCESS_OUTCOME)) {
            outcomeUrl =
                    MessageFormat
                            .format("contextRelative:/content.htm?spaceId={0}&contentId={1}",
                                    space.getSpaceId(),
                                    contentItem.getContentId());
            outcomeUrl =
                    MessageUtils
                            .appendRedirectMessage(outcomeUrl,
                                                   MessageUtils
                                                           .createMessage("Successfully added content."),
                                                   request);

        } else if (returnTo == null) {
            outcomeUrl =
                    MessageFormat
                            .format("contextRelative:/contents.htm?spaceId={0}",
                                    space.getSpaceId());
        } else {
            outcomeUrl = returnTo;
        }

        return outcomeUrl;
    }

}
