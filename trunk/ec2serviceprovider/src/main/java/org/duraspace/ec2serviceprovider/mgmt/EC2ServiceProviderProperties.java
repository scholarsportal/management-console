
package org.duraspace.ec2serviceprovider.mgmt;

import java.io.Serializable;

import org.duraspace.serviceprovider.mgmt.ServiceProviderProperties;

/**
 * POJO container for EC2-specific configuration information. -load/store is
 * implemented in base class.
 *
 * @author Andrew Woods
 */
public class EC2ServiceProviderProperties
        extends ServiceProviderProperties
        implements Serializable {

    private static final long serialVersionUID = -960605896468718060L;

    private String provider;

    private String signatureMethod;

    private String keyname;

    private String imageId;

    private int minInstanceCount;

    private int maxInstanceCount;

    private int maxAsyncThreads;

    private String webappProtocol;

    private int webappPort;

    private String webappName;

    @Override
    protected void setMembers(Object obj) {
        EC2ServiceProviderProperties props = (EC2ServiceProviderProperties) obj;
        this.setProvider(props.getProvider());
        this.setMaxAsyncThreads(props.getMaxAsyncThreads());
        this.setKeyname(props.getKeyname());
        this.setImageId(props.getImageId());
        this.setMinInstanceCount(props.getMinInstanceCount());
        this.setMaxInstanceCount(props.getMaxInstanceCount());
        this.setSignatureMethod(props.getSignatureMethod());
        this.setWebappProtocol(props.getWebappProtocol());
        this.setWebappPort(props.getWebappPort());
        this.setWebappName(props.getWebappName());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("EC2Props[");
        sb.append("\n\tprovider:" + provider);
        sb.append("\n\tsignatureMethod:" + signatureMethod);
        sb.append("\n\tkeyname:" + keyname);
        sb.append("\n\timageId:" + imageId);
        sb.append("\n\tminInstanceCount:" + minInstanceCount);
        sb.append("\n\tmaxInstanceCount:" + maxInstanceCount);
        sb.append("\n\tmaxAsyncThreads:" + maxAsyncThreads);
        sb.append("\n\twebappProtocol:" + webappProtocol);
        sb.append("\n\twebappPort:" + webappPort);
        sb.append("\n\twebappName:" + webappName);
        sb.append("]");
        return sb.toString();
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getSignatureMethod() {
        return signatureMethod;
    }

    public void setSignatureMethod(String signatureMethod) {
        this.signatureMethod = signatureMethod;
    }

    public String getKeyname() {
        return keyname;
    }

    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public int getMinInstanceCount() {
        return minInstanceCount;
    }

    public void setMinInstanceCount(int minCount) {
        this.minInstanceCount = minCount;
    }

    public int getMaxInstanceCount() {
        return maxInstanceCount;
    }

    public void setMaxInstanceCount(int maxCount) {
        this.maxInstanceCount = maxCount;
    }

    public int getMaxAsyncThreads() {
        return maxAsyncThreads;
    }

    public void setMaxAsyncThreads(int maxAsyncThreads) {
        this.maxAsyncThreads = maxAsyncThreads;
    }

    public String getWebappProtocol() {
        return webappProtocol;
    }

    public void setWebappProtocol(String webappProtocol) {
        this.webappProtocol = webappProtocol;
    }

    public int getWebappPort() {
        return webappPort;
    }

    public void setWebappPort(int port) {
        this.webappPort = port;
    }

    public String getWebappName() {
        return webappName;
    }

    public void setWebappName(String appname) {
        this.webappName = appname;
    }

}
