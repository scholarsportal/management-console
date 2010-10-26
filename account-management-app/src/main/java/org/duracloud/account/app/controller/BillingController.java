/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
	public static final String BILLING_PATH = "/billing/"; 
	public static final String FULL_BILLING_PATH = ACCOUNTS_PATH + ACCOUNT_PATH + BILLING_PATH; 
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

		        if(bif.getPaymentType().equals(BillingInfoForm.PaymentType.CC)){
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
	
	@RequestMapping(value = { "", "/"  }, method = RequestMethod.GET)
	public String getHome(@PathVariable String accountId, Model model)
			throws AccountNotFoundException {
		loadAccountInfo(accountId, model);
		return "account-billing";
	}

	@RequestMapping(value = { EDIT_PATH }, method = RequestMethod.GET)
	public String getBillingInfoForm( @PathVariable String accountId, Model model)
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
	public String update( @PathVariable String accountId, 
					   @ModelAttribute(BILLING_FORM) @Valid BillingInfoForm billingInfoForm,
					   BindingResult result, 
					   Model model) throws AccountNotFoundException {

		loadAccountInfo(accountId, model);

		if (result.hasErrors()) {
			return EDIT_VIEW;
		}
		return formatAccountRedirect(accountId, BILLING_PATH);

	}
	
	
}
