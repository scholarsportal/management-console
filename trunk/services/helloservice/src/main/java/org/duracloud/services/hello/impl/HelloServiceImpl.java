
package org.duracloud.services.hello.impl;

import org.duracloud.services.ComputeService;

public class HelloServiceImpl
        implements ComputeService {

    public void start() throws Exception {
    }

    public void stop() throws Exception {
    }

    public String describe() throws Exception {
        return "Service: " + getClass().getName();
    }

}
