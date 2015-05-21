/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import java.text.MessageFormat;

import org.hibernate.validator.constraints.NotBlank;


/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 27, 2012
 */
public class AccountClusterForm {
    @NotBlank(message="Please specify a name for the cluster.")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        String template = "{0}(name={1})";
        return MessageFormat.format(template, 
                                    getClass().getSimpleName(),
                                    this.name);
    }
 
}
