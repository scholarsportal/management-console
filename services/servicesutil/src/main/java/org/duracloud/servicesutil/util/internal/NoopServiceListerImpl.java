package org.duracloud.servicesutil.util.internal;

import org.duracloud.services.ComputeService;
import org.duracloud.servicesutil.util.ServiceLister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoopServiceListerImpl implements ServiceLister {

    private List<ComputeService> duraServices;

    public List<ComputeService> getDuraServices() {
        return duraServices;
    }

    public void setDuraServices(List<String> argServices) {
        this.duraServices = new ArrayList<ComputeService>();

        for (String srv : argServices) {
            final String name = srv;
            this.duraServices.add(new ComputeService() {

                private ServiceStatus status = ServiceStatus.INSTALLED;

                public String describe() throws Exception {
                    return name;
                }

                public String getServiceId() {
                    return name;
                }

                public ServiceStatus getServiceStatus() throws Exception {
                    return status;
                }

                public void start() throws Exception {
                    status = ServiceStatus.STARTED;
                }

                public void stop() throws Exception {
                    status = ServiceStatus.STOPPED;
                }

                public Map<String, String> getServiceProps() {
                    Map<String, String> props = new HashMap<String, String>();
                    props.put("serviceId", name);
                    return props;
                }
            });
        }

    }

}
