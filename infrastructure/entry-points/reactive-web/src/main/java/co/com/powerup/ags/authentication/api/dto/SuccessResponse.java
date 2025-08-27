package co.com.powerup.ags.authentication.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@JsonDeserialize(builder = SuccessResponse.SuccessResponseBuilder.class)
@Schema(description = "Standard success response wrapper")
public class SuccessResponse<T> {
    
    @Schema(description = "Timestamp of the response", example = "2025-08-26T08:48:10.161513")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime timestamp;
    
    @Schema(description = "Request path", example = "/api/v1/users")
    private String path;
    
    @Schema(description = "Response data")
    private T data;
    
    @Schema(description = "Response message")
    private String message;
    
    @JsonPOJOBuilder(withPrefix = "")
    public static class SuccessResponseBuilder<T> {
        // Lombok generates the builder implementation
    }
}
