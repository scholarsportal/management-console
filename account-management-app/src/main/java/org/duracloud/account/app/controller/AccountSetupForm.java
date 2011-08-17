package org.duracloud.account.app.controller;

import org.hibernate.validator.constraints.NotBlank;

public class AccountSetupForm {
    @NotBlank(message="Primary Storage account's username is required")
    private String primaryStorageUsername;

    @NotBlank(message="Primary Storage account's password is required")
    private String primaryStoragePassword;

    private int secondaryId0;
    private String secondaryUsername0;
    private String secondaryPassword0;

    private int secondaryId1;
    private String secondaryUsername1;
    private String secondaryPassword1;

    private int secondaryId2;
    private String secondaryUsername2;
    private String secondaryPassword2;

    private boolean computeCredentialsSame;

    private String computeUsername;

    private String computePassword;

    @NotBlank(message="Compute provider Elastic IP is required")
    private String computeElasticIP;

    @NotBlank(message="Compute provider Keypair is required")
    private String computeKeypair;

    @NotBlank(message="Compute provider Security group is required")
    private String computeSecurityGroup;

    public String getPrimaryStorageUsername() {
        return primaryStorageUsername;
    }

    public void setPrimaryStorageUsername(String primaryStorageUsername) {
        this.primaryStorageUsername = primaryStorageUsername;
    }

    public String getPrimaryStoragePassword() {
        return primaryStoragePassword;
    }

    public void setPrimaryStoragePassword(String primaryStoragePassword) {
        this.primaryStoragePassword = primaryStoragePassword;
    }

    public int getSecondaryId0() {
        return secondaryId0;
    }

    public void setSecondaryId0(int secondaryId0) {
        this.secondaryId0 = secondaryId0;
    }

    public String getSecondaryUsername0() {
        return secondaryUsername0;
    }

    public void setSecondaryUsername0(String secondaryUsername0) {
        this.secondaryUsername0 = secondaryUsername0;
    }

    public String getSecondaryPassword0() {
        return secondaryPassword0;
    }

    public void setSecondaryPassword0(String secondaryPassword0) {
        this.secondaryPassword0 = secondaryPassword0;
    }

    public int getSecondaryId1() {
        return secondaryId1;
    }

    public void setSecondaryId1(int secondaryId1) {
        this.secondaryId1 = secondaryId1;
    }

    public String getSecondaryUsername1() {
        return secondaryUsername1;
    }

    public void setSecondaryUsername1(String secondaryUsername1) {
        this.secondaryUsername1 = secondaryUsername1;
    }

    public String getSecondaryPassword1() {
        return secondaryPassword1;
    }

    public void setSecondaryPassword1(String secondaryPassword1) {
        this.secondaryPassword1 = secondaryPassword1;
    }

    public int getSecondaryId2() {
        return secondaryId2;
    }

    public void setSecondaryId2(int secondaryId2) {
        this.secondaryId2 = secondaryId2;
    }

    public String getSecondaryUsername2() {
        return secondaryUsername2;
    }

    public void setSecondaryUsername2(String secondaryUsername2) {
        this.secondaryUsername2 = secondaryUsername2;
    }

    public String getSecondaryPassword2() {
        return secondaryPassword2;
    }

    public void setSecondaryPassword2(String secondaryPassword2) {
        this.secondaryPassword2 = secondaryPassword2;
    }

    public boolean isComputeCredentialsSame() {
        return computeCredentialsSame;
    }

    public void setComputeCredentialsSame(boolean computeCredentialsSame) {
        this.computeCredentialsSame = computeCredentialsSame;
    }

    public String getComputeUsername() {
        return computeUsername;
    }

    public void setComputeUsername(String computeUsername) {
        this.computeUsername = computeUsername;
    }

    public String getComputePassword() {
        return computePassword;
    }

    public void setComputePassword(String computePassword) {
        this.computePassword = computePassword;
    }

    public String getComputeElasticIP() {
        return computeElasticIP;
    }

    public void setComputeElasticIP(String computeElasticIP) {
        this.computeElasticIP = computeElasticIP;
    }

    public String getComputeKeypair() {
        return computeKeypair;
    }

    public void setComputeKeypair(String computeKeypair) {
        this.computeKeypair = computeKeypair;
    }

    public String getComputeSecurityGroup() {
        return computeSecurityGroup;
    }

    public void setComputeSecurityGroup(String computeSecurityGroup) {
        this.computeSecurityGroup = computeSecurityGroup;
    }
}
