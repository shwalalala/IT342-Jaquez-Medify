package edu.cit.jaquez.medify.dto;


public class AdminLoginRequestDto {

    private String email;
    private String password;

    public AdminLoginRequestDto() {}

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