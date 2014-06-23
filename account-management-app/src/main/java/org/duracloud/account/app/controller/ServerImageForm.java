package org.duracloud.account.app.controller;

import org.hibernate.validator.constraints.NotBlank;

import java.text.MessageFormat;

public class ServerImageForm {

    @NotBlank(message = "You must specify a provider image id.")
    private String providerImageId;

    @NotBlank(message = "You must specify a version.")
    private String version;

    private String description;

    @NotBlank(message = "You must specify a password.")
    private String password;

    private boolean latest;

    public String getProviderImageId() {
        return providerImageId;
    }

    public void setProviderImageId(String providerImageId) {
        this.providerImageId = providerImageId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    @Override
    public String toString() {
        String template = "{0}(providerAccountId={1}, "+
                          "version={3},description={4},password={5},latest={6})";
        return MessageFormat.format(template, 
                                    getClass().getSimpleName(),
                                    this.providerImageId,
                                    this.version,
                                    this.description,
                                    this.password,
                                    this.latest);
        
    }
}
