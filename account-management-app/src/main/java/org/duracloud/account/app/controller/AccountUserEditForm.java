package org.duracloud.account.app.controller;

import org.hibernate.validator.constraints.NotBlank;

public class AccountUserEditForm {
    @NotBlank(message="Role is required")
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
