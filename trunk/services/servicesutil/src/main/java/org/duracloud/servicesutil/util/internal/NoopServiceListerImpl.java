
package org.duracloud.servicesutil.util.internal;

import java.util.ArrayList;
import java.util.List;

import org.duracloud.services.ComputeService;
import org.duracloud.servicesutil.util.ServiceLister;

public class NoopServiceListerImpl
        implements ServiceLister {

    private List<ComputeService> duraServices;

    public List<ComputeService> getDuraServices() {
        return duraServices;
    }

    public void setDuraServices(List<String> argServices) {
        this.duraServices = new ArrayList<ComputeService>();

        for (String srv : argServices) {
            final String name = "service-" + srv;
            this.duraServices.add(new ComputeService() {

                public String describe() throws Exception {
                    return name;
                }

                public void start() throws Exception {
                    // TODO Auto-generated method stub

                }

                public void stop() throws Exception {
                    // TODO Auto-generated method stub

                }
            });
        }

    }

}
