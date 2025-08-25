package co.com.powerup.ags.authentication.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Schema(description = "Request model for updating an existing user")
public class UpdateUserRequest {
    
    @Schema(description = "User's first name", example = "Steven")
    @Size(min = 1, message = "Name cannot be empty when provided")
    private String name;
    
    @Schema(description = "User's last name", example = "Martinez")
    @Size(min = 1, message = "Last name cannot be empty when provided")
    private String lastName;
    
    @Schema(description = "User's address", example = "123 Main St, City, Country")
    @Size(min = 1, message = "Address cannot be empty when provided")
    private String address;
    
    @Schema(description = "User's phone number", example = "1234567890")
    @Pattern(regexp = "^\\d{1,15}$", message = "Phone number must contain only numbers when provided")
    private String phoneNumber;
    
    @Schema(description = "User's birth date", example = "1990-01-15")
    @Past(message = "Birth date must be in the past when provided")
    private LocalDate birthDate;
    
    @Schema(description = "User's email address", example = "steven.martinez@example.com")
    @Email(message = "Email must have a valid format when provided")
    private String email;
    
    @Schema(description = "User's base salary", example = "50000.00")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base salary must be greater than 0 when provided")
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
    
}