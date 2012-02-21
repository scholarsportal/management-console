/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.util.UserFeedbackMessage.FeedbackType;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 20, 2012
 *
 */
public class UserFeedbackUtil {
    public static final String FEEDBACK_KEY = "feedback";

    public static UserFeedbackMessage
        create(FeedbackType fbType, String message) {
        return new UserFeedbackMessage(fbType, message);
    }

    public static void addFailureFlash(String message,
                                       RedirectAttributes redirectAttributes) {
        addFlash(message, FeedbackType.FAILURE, redirectAttributes);
    }

    public static void addWarningFlash(String message,
                                       RedirectAttributes redirectAttributes) {
        addFlash(message, FeedbackType.WARNING, redirectAttributes);
    }

    public static void addSuccessFlash(String message,
                                       RedirectAttributes redirectAttributes) {
        addFlash(message, FeedbackType.SUCCESS, redirectAttributes);
    }

    public static void addFlash(String message,
                                FeedbackType fbtype,
                                RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(FEEDBACK_KEY,
                                             create(fbtype, message));
    }

    public static UserFeedbackMessage createSuccess(String message) {
       return new UserFeedbackMessage(FeedbackType.SUCCESS, message);
    }
}
