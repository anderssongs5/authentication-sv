package co.com.powerup.ags.authentication.api.helper;

import co.com.powerup.ags.authentication.model.common.exception.DataAlreadyExistsException;
import co.com.powerup.ags.authentication.model.common.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorAttributes.class);
    
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest serverRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(serverRequest, options);
        Throwable error = getError(serverRequest);

        errorAttributes.remove("trace");
        errorAttributes.remove("exception");
        
        switch (error) {
            case DataAlreadyExistsException dataAlreadyExistsException ->
                    setErrorAttributes(errorAttributes, HttpStatus.CONFLICT, "DATA_ALREADY_EXISTS",
                            "Conflict", dataAlreadyExistsException.getMessage(), serverRequest.path());
            case EntityNotFoundException entityNotFoundException ->
                    setErrorAttributes(errorAttributes, HttpStatus.NOT_FOUND, "DATA_NOT_FOUND",
                            "Not Found", entityNotFoundException.getMessage(), serverRequest.path());
            case IllegalArgumentException illegalArgumentException ->
                    setErrorAttributes(errorAttributes, HttpStatus.BAD_REQUEST, "INVALID_INPUT",
                            "Bad Request", error.getMessage(), serverRequest.path());
            case ResponseStatusException rse -> {
                log.warn("Response status exception", error);
                
                setErrorAttributes(errorAttributes, Objects.requireNonNull(HttpStatus.resolve(rse.getStatusCode().value())),
                        "RESPONSE_STATUS_ERROR", getReasonPhrase(rse.getStatusCode()), rse.getReason(),
                        serverRequest.path());
            }
            case null, default -> {
                log.error("Unexpected error", error);
                
                setErrorAttributes(errorAttributes, HttpStatus.INTERNAL_SERVER_ERROR, "UNEXPECTED_ERROR",
                        "Internal Server Error", "An unexpected error occurred, please contact administrators.",
                        serverRequest.path());
            }
        }
        
        errorAttributes.put("timestamp", LocalDateTime.now());
        return errorAttributes;
    }
    
    private void setErrorAttributes(Map<String, Object> errorAttributes, HttpStatus status,
                                    String code, String error, String message, String path) {
        errorAttributes.put("status", status.value());
        errorAttributes.put("code", code);
        errorAttributes.put("error", error);
        errorAttributes.put("message", message);
        errorAttributes.put("path", path);
    }
    
    private String getReasonPhrase(HttpStatusCode statusCode) {
        HttpStatus httpStatus = HttpStatus.resolve(statusCode.value());
        return httpStatus != null ? httpStatus.getReasonPhrase() : "Unknown Status";
    }
}
