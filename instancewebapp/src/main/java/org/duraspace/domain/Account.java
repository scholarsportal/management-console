
package org.duraspace.domain;

import java.io.Serializable;

public class Account implements Serializable {

    private static final long serialVersionUID = 3008516494814826947L;

    private String customerId;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }


}
