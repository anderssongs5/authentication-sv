package co.com.powerup.ags.authentication.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request model for creating a new user")
public class CreateUserRequest {
    
    @Schema(description = "User's first name", example = "John", required = true)
    private String name;
    
    @Schema(description = "User's last name", example = "Doe", required = true)
    private String lastName;
    
    @Schema(description = "User's address", example = "123 Main St, City, Country", required = true)
    private String address;
    
    @Schema(description = "User's phone number", example = "+1234567890", required = true)
    private String phoneNumber;
    
    @Schema(description = "User's birth date", example = "1990-01-15", required = true)
    private LocalDate birthDate;
    
    @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
    private String email;
    
    @Schema(description = "User's base salary", example = "50000.00", required = true)
    private BigDecimal baseSalary;
    
    public CreateUserRequest() {
    }
    
    public CreateUserRequest(String name, String lastName, String address, 
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