
package org.duracloud.services;

public interface ComputeService {

    public void start() throws Exception;

    public void stop() throws Exception;

    public String describe() throws Exception;

}
