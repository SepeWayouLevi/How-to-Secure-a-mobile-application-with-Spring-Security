package org.example.accessbasededonnees.Login;


import jakarta.validation.constraints.NotNull;


public class LoginInformations {
    @NotNull
    private String email;

    @NotNull
    private String password;

    public LoginInformations(String email, String password) {
        this.password = password;
        this.email = email;
    }

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




}
