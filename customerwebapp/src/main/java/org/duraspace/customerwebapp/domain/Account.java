
package org.duraspace.customerwebapp.domain;

import java.io.Serializable;

public class Account implements Serializable {

    private static final long serialVersionUID = 3008516494814826947L;

    private String accountId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

}
