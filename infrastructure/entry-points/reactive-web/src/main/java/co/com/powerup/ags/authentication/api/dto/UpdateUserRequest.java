package co.com.powerup.ags.authentication.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateUserRequest {
    
    private String name;
    private String lastName;
    private String address;
    private String phoneNumber;
    private LocalDate birthDate;
    private String email;
    private BigDecimal baseSalary;
    
    public UpdateUserRequest() {
    }
    
    public UpdateUserRequest(String name, String lastName, String address, 
                           String phoneNumber, LocalDate birthDate, String email, BigDecimal baseSalary) {
        this.name = name;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.email = email;
        this.baseSalary = baseSalary;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public BigDecimal getBaseSalary() {
        return baseSalary;
    }
    
    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }
}