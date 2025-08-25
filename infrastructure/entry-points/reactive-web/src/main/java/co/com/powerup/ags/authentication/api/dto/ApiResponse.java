package co.com.powerup.ags.authentication.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public record ApiResponse<T>(
        @Schema(description = "Response message", example = "Operation completed successfully")
        String message,
        
        @Schema(description = "Response data payload")
        T data,
        
        @Schema(description = "Response status", example = "success", allowableValues = {"success", "error", "conflict"})
        String status
) {
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data, "success");
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, null, "error");
    }
    
    public static <T> ApiResponse<T> conflict(String message) {
        return new ApiResponse<>(message, null, "conflict");
    }
}