package ru.previzorz.tsm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UserCredentialsUpdateDTO {
    @Email(message = "Invalid email format")
    private String email;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String currentPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
}
