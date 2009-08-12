
package org.duracloud.services.hello.impl;

import org.duracloud.services.ComputeService;

public class HelloServiceImpl
        implements ComputeService {

    public void start() throws Exception {
        System.out.println("HelloService is Starting");
    }

    public void stop() throws Exception {
        System.out.println("HelloService is Stoping");
    }

    public String describe() throws Exception {
        return "Service: " + getClass().getName();
    }

}
