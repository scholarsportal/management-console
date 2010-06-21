
package org.duracloud.services.hello.impl;

import org.duracloud.services.BaseService;
import org.duracloud.services.ComputeService;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import java.util.Dictionary;
import java.util.Enumeration;

public class HelloServiceImpl extends BaseService
        implements ComputeService, ManagedService {

    private String text;

    @Override
    public void start() throws Exception {
        System.out.println("HelloService is Starting");
        super.start();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("HelloService is Stopping");
        super.stop();
    }

    @Override
    public String describe() throws Exception {
        System.out.println("HelloServiceImpl: Calling describe().");
        String baseDescribe = super.describe();
        return baseDescribe + "; Service message: '" + text + "'";
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
