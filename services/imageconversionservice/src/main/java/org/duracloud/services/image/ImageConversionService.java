package org.duracloud.services.image;

import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.client.ContentStore;
import org.duracloud.services.BaseService;
import org.duracloud.services.ComputeService;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Dictionary;
import java.util.Map;

/**
 * Service which converts image files from one format to another
 *
 * @author Bill Branan
 *         Date: Jan 27, 2010
 */
public class ImageConversionService extends BaseService implements ComputeService, ManagedService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ConversionThread conversionThread;

    private String duraStoreHost;
    private String duraStorePort;
    private String duraStoreContext;
    private String toFormat;
    private String sourceSpaceId;
    private String destSpaceId;
    private String namePrefix;
    private String nameSuffix;

    @Override
    public void start() throws Exception {
        System.out.println("Starting Image Conversion Service");
        this.setServiceStatus(ServiceStatus.STARTING);

        File workDir = new File(getServiceWorkDir());
        ContentStoreManager storeManager =
            new ContentStoreManagerImpl(duraStoreHost,
                                        duraStorePort,
                                        duraStoreContext);
        ContentStore contentStore = storeManager.getPrimaryContentStore();

        conversionThread = new ConversionThread(contentStore,
                                                workDir,
                                                toFormat,
                                                sourceSpaceId,
                                                destSpaceId,
                                                namePrefix,
                                                nameSuffix);
        conversionThread.start();

        this.setServiceStatus(ServiceStatus.STARTED);        
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Stopping Image Conversion Service");    
        this.setServiceStatus(ServiceStatus.STOPPING);
        if(conversionThread != null) {
            conversionThread.stopConversion();
        }
        this.setServiceStatus(ServiceStatus.STOPPED);
    }

    @Override
    public Map<String, String> getServiceProps() {
        Map<String, String> props = super.getServiceProps();
        if(conversionThread != null) {
            props.put("conversionStatus",
                      conversionThread.getConversionStatus());
        }
        return props;
    }

    @SuppressWarnings("unchecked")
    public void updated(Dictionary config) throws ConfigurationException {
        // Implementation not needed. Update performed through setters.
    }

    public String getDuraStoreHost() {
        return duraStoreHost;
    }

    public void setDuraStoreHost(String duraStoreHost) {
        this.duraStoreHost = duraStoreHost;
    }

    public String getDuraStorePort() {
        return duraStorePort;
    }

    public void setDuraStorePort(String duraStorePort) {
        this.duraStorePort = duraStorePort;
    }

    public String getDuraStoreContext() {
        return duraStoreContext;
    }

    public void setDuraStoreContext(String duraStoreContext) {
        this.duraStoreContext = duraStoreContext;
    }

    public String getToFormat() {
        return toFormat;
    }

    public void setToFormat(String toFormat) {
        this.toFormat = toFormat;
    }

    public String getSourceSpaceId() {
        return sourceSpaceId;
    }

    public void setSourceSpaceId(String sourceSpaceId) {
        this.sourceSpaceId = sourceSpaceId;
    }

    public String getDestSpaceId() {
        return destSpaceId;
    }

    public void setDestSpaceId(String destSpaceId) {
        this.destSpaceId = destSpaceId;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public String getNameSuffix() {
        return nameSuffix;
    }

    public void setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }
}
