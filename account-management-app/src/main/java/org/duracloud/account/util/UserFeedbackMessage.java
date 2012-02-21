/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.util.UserFeedbackMessage;
import org.duracloud.account.util.UserFeedbackMessage.FeedbackType;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 20, 2012
 *
 */
public class UserFeedbackMessage {
    public static  enum FeedbackType {
        SUCCESS,
        FAILURE,
        WARNING;
        
        public String getToLowerCase(){
            return this.toString().toLowerCase();
        }
    }
    
    private UserFeedbackMessage.FeedbackType feedbackType;
    private String message;

    public UserFeedbackMessage(UserFeedbackMessage.FeedbackType feedbackType, String message) {
        super();
        this.feedbackType = feedbackType;
        this.message = message;
    }

    public UserFeedbackMessage.FeedbackType getType() {
        return feedbackType;
    }

    public String getMessage() {
        return message;
    }
    
}