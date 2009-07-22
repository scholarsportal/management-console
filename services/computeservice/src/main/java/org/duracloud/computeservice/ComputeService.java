
package org.duracloud.computeservice;

public interface ComputeService {

    public void start() throws Exception;

    public void stop() throws Exception;

    public String describe() throws Exception;

}
