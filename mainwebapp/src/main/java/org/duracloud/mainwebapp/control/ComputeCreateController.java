package org.duracloud.mainwebapp.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import org.duracloud.common.model.Credential;
import org.duracloud.common.util.ApplicationConfig;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.mainwebapp.domain.cmd.ComputeCreateCmd;
import org.duracloud.mainwebapp.domain.cmd.DuraAcctWrapper;
import org.duracloud.mainwebapp.domain.model.ComputeAcct;
import org.duracloud.mainwebapp.mgmt.ComputeAcctManager;
import org.duracloud.mainwebapp.mgmt.DuraCloudAcctManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ComputeCreateController
        extends SimpleFormController {

    protected final Logger log = LoggerFactory.getLogger(ComputeCreateController.class);

    private final String defaultComputePropsResource =
            "defaultEC2Config.properties";

    private DuraCloudAcctManager duraCloudAcctManager;

    private ComputeAcctManager computeAcctManager;

    public ComputeCreateController() {
        setCommandClass(ComputeCreateCmd.class);
        setCommandName("computeCreateCmd");
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request)
            throws Exception {
        ComputeCreateCmd cmd =
                (ComputeCreateCmd) super.formBackingObject(request);
        cmd.setComputeCred(new Credential());
        return cmd;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map referenceData(HttpServletRequest request) throws Exception {
        List<String> computeProviderList = new ArrayList<String>();
        for (ComputeProviderType providerType : ComputeProviderType.values()) {
            computeProviderList.add(providerType.toString());
        }

        Map<String, String[]> providers = new HashMap<String, String[]>();
        providers.put("computeProviders", computeProviderList
                .toArray(new String[] {}));
        return providers;
    }

    @Override
    protected ModelAndView onSubmit(Object argCommand,
                                    BindException bindException)
            throws Exception {
        ComputeCreateCmd command = (ComputeCreateCmd) argCommand;

        int duraAcctId = command.getDuraAcctId();
        Credential computeCred = command.getComputeCred();
        String computeAcctNamespace = command.getComputeAcctNamespace();
        String providerType = command.getComputeProviderType();
        String cmd = command.getCmd();
        String imageId = command.getImageId();

        log.info("duraAcctId  : '" + duraAcctId + "'");
        log.info("computeCred : '" + computeCred + "'");
        log.info("namespace   : '" + computeAcctNamespace + "'");
        log.info("providerType:'" + providerType + "'");
        log.info("cmd         :'" + cmd + "'");
        log.info("imageId     :'" + imageId + "'");

        if ("Create".equalsIgnoreCase(cmd)) {
            log.info("Attempt create compute acct, duraAcctId: " + duraAcctId);

            validateInput(duraAcctId,
                          computeAcctNamespace,
                          computeCred,
                          providerType);

            createAndSaveComputeAcct(duraAcctId,
                                     computeAcctNamespace,
                                     providerType,
                                     computeCred,
                                     imageId);
        }

        DuraAcctWrapper wrapper =
                DuraAcctWrapper.buildWrapper(getDuraCloudAcctManager(),
                                             duraAcctId);

        return new ModelAndView(getSuccessView(), "wrapper", wrapper);
    }

    private void createAndSaveComputeAcct(int duraAcctId,
                                          String computeAcctNamespace,
                                          String providerType,
                                          Credential computeCred,
                                          String imageId) throws Exception {

        int computeProviderId =
                getComputeAcctManager()
                        .findComputeProviderIdByProviderType(ComputeProviderType
                                .fromString(providerType));

        ComputeAcct computeAcct = new ComputeAcct();
        computeAcct.setDuraAcctId(duraAcctId);
        computeAcct.setComputeProviderType(providerType);
        computeAcct.setComputeProviderId(computeProviderId);
        computeAcct.setNamespace(computeAcctNamespace);
        computeAcct.setXmlProps(getDefaultProps(imageId));

        int acctId = getDuraCloudAcctManager().saveComputeAcct(computeAcct);

        getDuraCloudAcctManager().saveCredentialForComputeAcct(computeCred,
                                                               acctId);

    }

    private String getDefaultProps(String imageId) throws Exception {
        Properties props =
                ApplicationConfig
                        .getPropsFromXmlResource(defaultComputePropsResource);

        if (!StringUtils.isBlank(imageId)) {
            props.put("imageId", imageId);
        }

        return ApplicationConfig.getXmlFromProps(props);
    }

    private void validateInput(int duraAcctId,
                               String computeAcctNamespace,
                               Credential computeCred,
                               String providerType) throws Exception {
        if (duraAcctId < 1) {
            throw new Exception("Invalid duraAcctId: " + duraAcctId);
        }
        if (StringUtils.isBlank(computeAcctNamespace)) {
            throw new Exception("Account namespace must not be blank.");
        }
        if (getDuraCloudAcctManager()
                .isComputeNamespaceTaken(computeAcctNamespace)) {
            throw new Exception("Namespace is taken: " + computeAcctNamespace);
        }
        if (computeCred == null
                || StringUtils.isBlank(computeCred.getUsername())
                || StringUtils.isBlank(computeCred.getPassword())) {
            throw new Exception("Invalid credential: '" + computeCred + "'");
        }
        if (ComputeProviderType.UNKNOWN.equals(ComputeProviderType
                .fromString(providerType))) {
            throw new Exception("Invalid computeProviderType: '" + providerType
                    + "'");
        }
    }

    public DuraCloudAcctManager getDuraCloudAcctManager() {
        return duraCloudAcctManager;
    }

    public void setDuraCloudAcctManager(DuraCloudAcctManager duraCloudAcctManager) {
        this.duraCloudAcctManager = duraCloudAcctManager;
    }

    public ComputeAcctManager getComputeAcctManager() {
        return computeAcctManager;
    }

    public void setComputeAcctManager(ComputeAcctManager computeAcctManager) {
        this.computeAcctManager = computeAcctManager;
    }

}