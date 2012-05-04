/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.config;

import org.springframework.format.support.DefaultFormattingConversionService;
/**
 * 
 * @author Daniel Bernstein
 *
 */
public class ApplicationFormattingConversionService
    extends DefaultFormattingConversionService {

    public ApplicationFormattingConversionService(){
        super(true);
        addConverter(new StringTrimmerConverter());
    }
}
