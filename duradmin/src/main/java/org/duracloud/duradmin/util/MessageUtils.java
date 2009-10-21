package org.duracloud.duradmin.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.binding.message.Message;
import org.springframework.binding.message.Severity;
import org.springframework.web.servlet.ModelAndView;


public class MessageUtils {
    private static final String REDIRECT_KEY = "redirectKey";
    public static final String FLASH_MESSAGE = "flashMessage";

    public static void addFlashMessage(Message message, HttpServletRequest request){
        request.setAttribute(FLASH_MESSAGE, message);
    }

    public static String addMessageToRedirect(Message message, HttpServletRequest request){
        String key = String.valueOf(System.currentTimeMillis());
        request.getSession().setAttribute(key, new NameValuePair(FLASH_MESSAGE, message));
        return key;
    }
    
    public static Message getRedirectMessage(HttpServletRequest request){
        String key = request.getParameter(REDIRECT_KEY);
        NameValuePair obj = (NameValuePair)request.getSession().getAttribute(key);
        if(obj != null){
            request.getSession().removeAttribute(key);
            return (Message)obj.getValue();
        }else{
            return null;
        }
        
    }

    public static class NameValuePair {
        public NameValuePair(String key, Object value) {
            super();
            this.key = key;
            Value = value;
        }
        
        public String getKey() {
            return key;
        }
        
        public Object getValue() {
            return Value;
        }
        private String key;
        private Object Value;
    }

    public static String appendRedirectMessage(String outcomeUrl,
                                               Message message,
                                               HttpServletRequest request) {
        String key = addMessageToRedirect(message, request);
        if(!outcomeUrl.contains("?")){
            outcomeUrl+="?";
        }else{
            outcomeUrl+="&";
        }
        
        int index = outcomeUrl.indexOf(REDIRECT_KEY);
        
        if(index > 0){
           int start = index + REDIRECT_KEY.length()+1;
           int end = outcomeUrl.indexOf("=", start);
           if(end < 0){
               end = outcomeUrl.length();
           }
           String value = outcomeUrl.substring(start, end);
           outcomeUrl = outcomeUrl.replace(REDIRECT_KEY + "=" + value, REDIRECT_KEY + "=" + key);
        }else{
            outcomeUrl+=REDIRECT_KEY + "="+key;
        }

        return outcomeUrl;
    }

    public static void addRedirectMessageToModelAndView(ModelAndView modelAndView, HttpServletRequest request) {
        Message message = getRedirectMessage(request);
        if(message != null){
            modelAndView.addObject(FLASH_MESSAGE, message);
        }
    }

    public static Message createMessage(String string) {
        Message message = new Message(null, string, Severity.INFO);
        return message;
    }

    public static void addFlashMessage(String messageText, ModelAndView mav) {
        mav.addObject("flashMessage", MessageUtils.createMessage(messageText));
    }
}
