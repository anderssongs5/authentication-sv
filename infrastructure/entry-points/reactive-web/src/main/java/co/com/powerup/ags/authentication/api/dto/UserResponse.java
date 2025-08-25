package co.com.powerup.ags.authentication.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "User response model")
public record UserResponse(
        @Schema(description = "User unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        String id,
        
        @Schema(description = "User's first name", example = "Steven")
        String name,
        
        @Schema(description = "User's last name", example = "Martinez")
        String lastName,
        
        @Schema(description = "User's address", example = "123 Main St, City, Country")
        String address,
        
        @Schema(description = "User's phone number", example = "+1234567890")
        String phoneNumber,
        
        @Schema(description = "User's birth date", example = "1990-01-15")
        LocalDate birthDate,
        
        @Schema(description = "User's email address", example = "steven.martinez@example.com")
        String email,
        
        @Schema(description = "User's base salary", example = "50000.00")
        BigDecimal baseSalary
) {
    
}