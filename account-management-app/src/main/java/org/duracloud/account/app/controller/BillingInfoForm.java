/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class BillingInfoForm {

	public static enum PaymentMethod {
		CC,
		INVOICE;
	}
	
	public PaymentMethod[] getPaymentMethods(){
		return PaymentMethod.values();
	}
	
	private PaymentMethod paymentMethod = PaymentMethod.CC;
	@NotBlank
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	@NotBlank
	private String city;
	@NotBlank
	private String state;
	@NotBlank
	private String postalCode;
	@NotBlank
	private String country;
	private CreditCardForm creditCard = new CreditCardForm();
	private InvoiceForm invoice = new InvoiceForm();
	
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	public String getAddressLine3() {
		return addressLine3;
	}
	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	

	public CreditCardForm getCreditCard() {
		return creditCard;
	}

	public InvoiceForm getInvoice() {
		return invoice;
	}

	public class InvoiceForm {
		@NotBlank
		private String name;
		@NotBlank
		private String phone;
		public void setName(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getPhone() {
			return phone;
		}
	}

	public class CreditCardForm {
		@NotBlank
		private String nameOnCard;
		@NotBlank
		private String number;
		@NotBlank
		private String expiration;
		@NotBlank
		private String cvc;
		
		public String getNameOnCard() {
			return nameOnCard;
		}
		public void setNameOnCard(String nameOnCard) {
			this.nameOnCard = nameOnCard;
		}

		public String getExpiration() {
			return expiration;
		}
		public void setExpiration(String expiration) {
			this.expiration = expiration;
		}
		public String getCvc() {
			return cvc;
		}
		public void setCvc(String cvc) {
			this.cvc = cvc;
		}
		public void setNumber(String number) {
			this.number = number;
		}
		public String getNumber() {
			return number;
		}
		
	}
	
}
