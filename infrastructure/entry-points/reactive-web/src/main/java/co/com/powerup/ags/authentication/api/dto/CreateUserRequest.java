package co.com.powerup.ags.authentication.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Schema(description = "Request model for creating a new user")
public class CreateUserRequest {
    
    @Schema(description = "User's first name", example = "Steven", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name is required and cannot be empty")
    private String name;
    
    @Schema(description = "User's last name", example = "Martinez", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Last name is required and cannot be empty")
    private String lastName;
    
    @Schema(description = "User's address", example = "123 Main St, City, Country", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Address is required and cannot be empty")
    private String address;
    
    @Schema(description = "User's phone number", example = "1234567890", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Phone number is required and cannot be empty")
    @Pattern(regexp = "^\\d{1,15}$", message = "Phone number must contain only numbers")
    private String phoneNumber;
    
    @Schema(description = "User's birth date", example = "1990-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
    
    @Schema(description = "User's email address", example = "steven.martinez@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required and cannot be empty")
    @Email(message = "Email must have a valid format")
    private String email;
    
    @Schema(description = "User's base salary", example = "50000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base salary must be greater than 0")
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
    
}