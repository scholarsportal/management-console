
package org.duracloud.services.hello.impl;

import java.util.Dictionary;
import java.util.Enumeration;

import org.duracloud.services.ComputeService;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public class HelloServiceImpl
        implements ComputeService, ManagedService {

    private String text;

    public void start() throws Exception {
        System.out.println("HelloService is Starting");
    }

    public void stop() throws Exception {
        System.out.println("HelloService is Stopping");
    }

    public String describe() throws Exception {
        System.out.println("HelloServiceImpl: Calling describe().");
        return "Service: " + getClass().getName() + ", my message: '" + text
                + "'";
    }

    @SuppressWarnings("unchecked")
    public void updated(Dictionary config) throws ConfigurationException {
        System.out.print("HelloService updating config: ");
        if (config != null) {
            Enumeration keys = config.keys();
            {
                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    String val = (String) config.get(key);
                    System.out.print(" [" + key + "|" + val + "] ");
                }
            }
        } else {
            System.out.print("config is null.");
        }
        System.out.println();

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        System.out.println("HelloServiceImpl: setText (" + text + ")");
        this.text = text;
    }

}
