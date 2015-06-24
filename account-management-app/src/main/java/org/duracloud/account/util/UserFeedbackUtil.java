/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util;

import org.springframework.binding.message.Message;
import org.springframework.binding.message.Severity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 20, 2012
 *
 */
public class UserFeedbackUtil {
    public static final String FEEDBACK_KEY = "message";

    public static Message
        create(Severity severity, String text) {
        return new Message(null, text, severity);
    }

    public static void addFailureFlash(String message,
                                       RedirectAttributes redirectAttributes) {
        addFlash(create(Severity.ERROR, message), redirectAttributes);
    }

    public static void addWarningFlash(String message,
                                       RedirectAttributes redirectAttributes) {
        addFlash(create(Severity.WARNING, message), redirectAttributes);
    }

    public static void addSuccessFlash(String message,
                                       RedirectAttributes redirectAttributes) {
        addFlash(create(Severity.INFO, message), redirectAttributes);
    }

    public static void addFlash(String message,
                                Severity severity,
                                RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(FEEDBACK_KEY,
                                             create(severity, message));
    }

    public static Message createSuccess(String text) {
       return new Message(null, text, Severity.INFO);
    }

    public static void addFlash(Message message, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(FEEDBACK_KEY, message);
    }
}
