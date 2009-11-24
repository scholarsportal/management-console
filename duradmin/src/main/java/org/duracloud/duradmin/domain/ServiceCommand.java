
package org.duracloud.duradmin.domain;

public class ServiceCommand {

    public Integer serviceInfoId;

    public Integer deploymentId;
    
    public void setServiceInfoId(Integer serviceInfoId) {
        this.serviceInfoId = serviceInfoId;
    }

    public Integer getServiceInfoId() {
       return this.serviceInfoId;
    }

    @Override
    public String toString() {
        return "ServiceCommand [deploymentId=" + deploymentId
                + ", serviceInfoId=" + serviceInfoId + "]";
    }

    
    public Integer getDeploymentId() {
        return deploymentId;
    }

    
    public void setDeploymentId(Integer deploymentId) {
        this.deploymentId = deploymentId;
    }

}
