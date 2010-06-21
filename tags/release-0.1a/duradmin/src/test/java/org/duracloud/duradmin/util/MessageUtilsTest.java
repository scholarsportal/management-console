package org.duracloud.duradmin.util;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.duracloud.duradmin.util.MessageUtils.NameValuePair;
import org.junit.Assert;
import org.springframework.binding.message.Message;
import org.springframework.mock.web.MockHttpServletRequest;


public class MessageUtilsTest extends TestCase{
    private MockHttpServletRequest request;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.request = new MockHttpServletRequest();
    }
    
    private Message createTestMessage(){
        return MessageUtils.createMessage("Test Message");
    }
    
    public void testAddFlashMessage(){
        Message message = createTestMessage();
        MessageUtils.addFlashMessage(message, request);
        Assert.assertNotNull(request.getAttribute(MessageUtils.FLASH_MESSAGE));
    }
    
    public void testAddMessageToRedirect(){
        String key = MessageUtils.addMessageToRedirect(createTestMessage(), request);
        Assert.assertNotNull(request.getSession().getAttribute(key));
    }
    
    public void testGetRedirectMessage(){
        String key = MessageUtils.addMessageToRedirect(createTestMessage(), request);
        request.addParameter(MessageUtils.REDIRECT_KEY, key);
        
        Message message = MessageUtils.getRedirectMessage(request);
        Assert.assertNotNull(message);
    }

    public void testAppendRedirectMessage(){
        String input = "http://testurl";
        Message message = createTestMessage();
        String output  = MessageUtils.appendRedirectMessage(input, message, request);
        Assert.assertTrue(output.contains("?"+MessageUtils.REDIRECT_KEY+"="));
    }

    public void testKeyReplacement(){
        String input = "http://testurl?id=123&"+ MessageUtils.REDIRECT_KEY+"=1234";
        Message message = createTestMessage();
        String output  = MessageUtils.appendRedirectMessage(input, message, request);
        Assert.assertFalse(output.contains("&"+MessageUtils.REDIRECT_KEY+"=1234"));
        Assert.assertTrue(output.contains("&"+MessageUtils.REDIRECT_KEY+"="));

    }

}