
package org.duraspace.mainwebapp.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.ApplicationConfig;
import org.duraspace.mainwebapp.domain.cmd.ComputeCreateCmd;
import org.duraspace.mainwebapp.domain.cmd.DuraAcctWrapper;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.mgmt.ComputeAcctManager;
import org.duraspace.mainwebapp.mgmt.DuraSpaceAcctManager;
import org.duraspace.serviceprovider.domain.ComputeProviderType;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ComputeCreateController
        extends SimpleFormController {

    protected final Logger log = Logger.getLogger(getClass());

    private final String defaultComputePropsResource =
            "defaultEC2Config.properties";

    private DuraSpaceAcctManager duraSpaceAcctManager;

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
                DuraAcctWrapper.buildWrapper(getDuraSpaceAcctManager(),
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

        int acctId = getDuraSpaceAcctManager().saveComputeAcct(computeAcct);

        getDuraSpaceAcctManager().saveCredentialForComputeAcct(computeCred,
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
        if (getDuraSpaceAcctManager()
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

    public DuraSpaceAcctManager getDuraSpaceAcctManager() {
        return duraSpaceAcctManager;
    }

    public void setDuraSpaceAcctManager(DuraSpaceAcctManager duraSpaceAcctManager) {
        this.duraSpaceAcctManager = duraSpaceAcctManager;
    }

    public ComputeAcctManager getComputeAcctManager() {
        return computeAcctManager;
    }

    public void setComputeAcctManager(ComputeAcctManager computeAcctManager) {
        this.computeAcctManager = computeAcctManager;
    }

}