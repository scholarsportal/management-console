package org.duracloud.mock;

import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageCriteria;
import org.springframework.binding.message.MessageResolver;


public class MockMessageContext implements MessageContext{
    
    public boolean hasErrorMessages() {
        // TODO Auto-generated method stub
        return false;
    }
    
    public Message[] getMessagesBySource(Object source) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Message[] getMessagesByCriteria(MessageCriteria criteria) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Message[] getAllMessages() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void clearMessages() {
        // TODO Auto-generated method stub
        
    }
    
    public void addMessage(MessageResolver messageResolver) {
        // TODO Auto-generated method stub
        
    }
}
