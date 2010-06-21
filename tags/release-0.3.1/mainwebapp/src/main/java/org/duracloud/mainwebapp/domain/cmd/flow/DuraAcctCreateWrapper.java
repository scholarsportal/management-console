package org.duracloud.mainwebapp.domain.cmd.flow;

import java.io.Serializable;

import org.duracloud.common.model.Credential;
import org.duracloud.mainwebapp.domain.model.Address;
import org.duracloud.mainwebapp.domain.model.User;

public class DuraAcctCreateWrapper
        implements Serializable {

    private static final long serialVersionUID = 1396833795536999050L;

    private User user;

    private Credential duraCred;

    private Address addrShipping;

    private String duraAcctName;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Credential getDuraCred() {
        return duraCred;
    }

    public void setDuraCred(Credential duraCred) {
        this.duraCred = duraCred;
    }

    public Address getAddrShipping() {
        return addrShipping;
    }

    public void setAddrShipping(Address addrShipping) {
        this.addrShipping = addrShipping;
    }

    public String getDuraAcctName() {
        return duraAcctName;
    }

    public void setDuraAcctName(String duraAcctName) {
        this.duraAcctName = duraAcctName;
    }

}
