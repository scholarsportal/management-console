
package org.duracloud.duradmin.webflow.space;

import org.duracloud.duradmin.contentstore.ContentStoreProvider;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.error.ContentStoreException;
import org.duracloud.storage.error.InvalidIdException;
import org.duracloud.storage.util.IdUtil;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.util.StringUtils;

public class SpaceValidator {

    private ContentStoreProvider contentStoreProvider;

    public ContentStoreProvider getContentStoreProvider() {
        return contentStoreProvider;
    }

    public void setContentStoreProvider(ContentStoreProvider contentStoreProvider) {
        this.contentStoreProvider = contentStoreProvider;
    }

    public void validateDefineSpace(Space space, ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        String spaceId = space.getSpaceId();
        if (spaceId == null || !StringUtils.hasLength(spaceId.trim())) {
            messages.addMessage(new MessageBuilder().error().source("spaceId")
                    .code("required").build());
        }

        try {
            IdUtil.validateSpaceId(spaceId);
        } catch(InvalidIdException e) {
            messages.addMessage(new MessageBuilder().error()
                .source("spaceId")
                .defaultText(e.getMessage()).build());
        }

        //check that space doesn't already exist.
        try {
            if (spaceExists(spaceId)) {
                messages
                        .addMessage(new MessageBuilder()
                                .error()
                                .source("spaceId")
                                .defaultText("A space with this ID already exists. Please try another name.")
                                .build());
            }
        } catch (ContentStoreException ex) {
            messages.addMessage(new MessageBuilder().error().source("spaceId")
                    .defaultText("Unable to validate spaceId: "
                            + ex.getMessage()).build());
        }

    }

    private boolean spaceExists(String spaceId) throws ContentStoreException {
        return this.contentStoreProvider.getContentStore().getSpaces()
                .contains(spaceId);

    }
}
