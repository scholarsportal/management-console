
package org.duraspace.mainwebapp.domain.model;

import java.io.Writer;

import java.util.Set;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class StorageAcct {

    private String id;

    private String ownerId; // reference to parent CustomerAcct.

    private boolean isPrimary;

    private String storageProviderId;

    private Credential storageProviderCred;

    public boolean hasOwner(String customerId) {
        return ownerId.equals(customerId);
    }

    public String toXml() {
        return getXStream().toXML(this);
    }

    private XStream getXStream() {
        XStream xstream = new XStream(new DomDriver() {

            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new CompactWriter(out);
            }
        });
        xstream.alias("storageAcct", StorageAcct.class);
        xstream.alias("storageAccts", Set.class);
        xstream.useAttributeFor("ownerId", String.class);
        xstream.useAttributeFor("isPrimary", boolean.class);
        return xstream;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public boolean getIsPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getStorageProviderId() {
        return storageProviderId;
    }

    public void setStorageProviderId(String storageProviderId) {
        this.storageProviderId = storageProviderId;
    }

    public Credential getStorageProviderCred() {
        return storageProviderCred;
    }

    public void setStorageProviderCred(Credential storageProviderCred) {
        this.storageProviderCred = storageProviderCred;
    }

}
