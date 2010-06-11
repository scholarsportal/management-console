package org.duracloud.mainwebapp.control;

import org.duracloud.mainwebapp.domain.model.Address;
import org.duracloud.mainwebapp.domain.repo.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class AddressController
        extends SimpleFormController {

    protected final Logger log = LoggerFactory.getLogger(AddressController.class);

    private AddressRepository addressRepository;

    public AddressController() {
        setCommandClass(Address.class);
        setCommandName("address");
    }

    @Override
    protected ModelAndView onSubmit(Object command, BindException bindException)
            throws Exception {
        Address addr = (Address) command;

        getAddressRepository().saveAddress(addr);

        log.info("saving address: " + addr);

        return new ModelAndView(getSuccessView());
    }


    public AddressRepository getAddressRepository() {
        return addressRepository;
    }


    public void setAddressRepository(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

}