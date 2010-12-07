/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.duracloud.account.app.controller.BillingInfoForm.CreditCardForm;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The default view for this application
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
@Controller
@RequestMapping(BillingController.FULL_BILLING_PATH)
public class BillingController extends AbstractAccountController {
	public static final String FULL_BILLING_PATH = ACCOUNTS_PATH + ACCOUNT_PATH + "/billing"; 
	public static final String EDIT_VIEW = "account-billing-edit";
	public static final String BILLING_FORM = "billingInfoForm";
	
	
	@InitBinder 
	public void initBinder(WebDataBinder binder) {
		binder.setValidator(new org.springframework.validation.Validator(){

			@Override
			public boolean supports(Class<?> clazz) {
				return true;
			}

			@Override
			public void validate(Object target, Errors errors) {
				BillingInfoForm bif = (BillingInfoForm)target;
				ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		        Validator validator = factory.getValidator();
		        
		        Set<ConstraintViolation<BillingInfoForm>> constraintViolations = validator.validate(bif, Default.class);
		        for(ConstraintViolation<BillingInfoForm> cv : constraintViolations){
		        	errors.rejectValue(cv.getPropertyPath().toString(), cv.getMessage(),cv.getMessage());
		        }

		        if(bif.getPaymentMethod().equals(BillingInfoForm.PaymentMethod.CC)){
			        Set<ConstraintViolation<CreditCardForm>> ccConstraints = validator.validate(bif.getCreditCard(), Default.class);
			        errors.pushNestedPath("creditCard");
			        for(ConstraintViolation<CreditCardForm> cv : ccConstraints){
			        	errors.rejectValue(cv.getPropertyPath().toString(), cv.getMessage(), cv.getMessage());
			        }
			        errors.popNestedPath();
		        }
			}
		});
	}
	
	@RequestMapping(value = { EDIT_PATH }, method = RequestMethod.GET)
	public String getBillingInfoForm( @PathVariable int accountId, Model model)
		throws AccountNotFoundException{
		log.info("serving up new "+BILLING_FORM);
		loadAccountInfo(accountId, model);
		//TODO load form
		log.warn("!!!MUST IMPLEMENT LOADING OF BILLING INFO FORM");
		model.addAttribute(BILLING_FORM,
				new BillingInfoForm());
		return EDIT_VIEW;
	}

	@RequestMapping(value = { EDIT_PATH }, method = RequestMethod.POST)
	public String update( @PathVariable int accountId,
					   @ModelAttribute(BILLING_FORM) @Valid BillingInfoForm billingInfoForm,
					   BindingResult result, 
					   Model model) throws AccountNotFoundException {

		loadAccountInfo(accountId, model);

		if (result.hasErrors()) {
			return EDIT_VIEW;
		}
        String accountIdText = Integer.toString(accountId);
		return formatAccountRedirect(accountIdText, AccountDetailsController.ACCOUNT_DETAILS_PATH);

	}
	
	
}
