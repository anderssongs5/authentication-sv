package co.com.powerup.ags.authentication.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntrospectRequest {
    
    @NotBlank(message = "Token is required")
    private String token;
}