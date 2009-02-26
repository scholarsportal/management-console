
package org.duraspace.serviceprovider.mgmt.mock;

import org.duraspace.serviceprovider.mgmt.ServiceProviderProperties;

public class MockServiceProviderProperties
        extends ServiceProviderProperties {

    private String prop0;

    private String prop1;

    private String prop2;

    @Override
    protected void setMembers(Object obj) {
        MockServiceProviderProperties props =
                (MockServiceProviderProperties) obj;
        this.setProp0(props.getProp0());
        this.setProp1(props.getProp1());
        this.setProp2(props.getProp2());
    }

    public String getProp0() {
        return prop0;
    }

    public void setProp0(String prop0) {
        this.prop0 = prop0;
    }

    public String getProp1() {
        return prop1;
    }

    public void setProp1(String prop1) {
        this.prop1 = prop1;
    }

    public String getProp2() {
        return prop2;
    }

    public void setProp2(String prop2) {
        this.prop2 = prop2;
    }

}
