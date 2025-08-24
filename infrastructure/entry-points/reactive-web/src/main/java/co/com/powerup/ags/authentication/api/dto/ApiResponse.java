package co.com.powerup.ags.authentication.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        String message,
        T data,
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