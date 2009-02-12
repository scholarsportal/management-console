package org.duraspace.serviceprovider.mgmt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public abstract class ServiceProperties {

    public void load(InputStream in) {
        XStream xstream = new XStream(new DomDriver());
        setMembers(xstream.fromXML(in));
    }

    protected abstract void setMembers(Object props);

    public void store(OutputStream out) throws IOException {
        String xml = thisAsXml();
        out.write(xml.getBytes());
    }

    private String thisAsXml() {
        XStream xstream = new XStream(new DomDriver());
        return xstream.toXML(this);
    }


}
