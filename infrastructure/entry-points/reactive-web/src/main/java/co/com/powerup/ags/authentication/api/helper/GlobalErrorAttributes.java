package co.com.powerup.ags.authentication.api.helper;

import co.com.powerup.ags.authentication.api.exception.AccessDeniedException;
import co.com.powerup.ags.authentication.api.exception.UnauthorizedException;
import co.com.powerup.ags.authentication.model.common.exception.DataAlreadyExistsException;
import co.com.powerup.ags.authentication.model.common.exception.InvalidCredentialsException;
import co.com.powerup.ags.authentication.model.common.exception.RoleNotFoundException;
import co.com.powerup.ags.authentication.model.common.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalErrorAttributes.class);
    public static final String BAD_REQUEST = "Bad Request";
    
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest serverRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(serverRequest, options);
        Throwable error = getError(serverRequest);

        errorAttributes.remove("trace");
        errorAttributes.remove("exception");
        
        String path = getPath(serverRequest);
        
        switch (error) {
            case DataAlreadyExistsException dataAlreadyExistsException ->
                    setErrorAttributes(errorAttributes, HttpStatus.CONFLICT, "DATA_ALREADY_EXISTS",
                            "Conflict", dataAlreadyExistsException.getMessage(), path);
            case UserNotFoundException userNotFoundException ->
                    setErrorAttributes(errorAttributes, HttpStatus.NOT_FOUND, "DATA_NOT_FOUND",
                            "Not Found", userNotFoundException.getMessage(), path);
            case RoleNotFoundException roleNotFoundException ->
                    setErrorAttributes(errorAttributes, HttpStatus.BAD_REQUEST, "DATA_NOT_FOUND",
                            BAD_REQUEST, roleNotFoundException.getMessage(), path);
            case InvalidCredentialsException invalidCredentialsException ->
                    setErrorAttributes(errorAttributes, HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
                            "Unauthorized", invalidCredentialsException.getMessage(), path);
            case AccessDeniedException accessDeniedException -> {
                log.warn("Access denied", accessDeniedException);
                setErrorAttributes(errorAttributes, HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                        "Forbidden", "Access denied. You don't have sufficient permissions to access this resource.", path);
            }
            case IllegalArgumentException illegalArgumentException ->
                    setErrorAttributes(errorAttributes, HttpStatus.BAD_REQUEST, "INVALID_INPUT",
                            BAD_REQUEST, error.getMessage(), path);
            case UnauthorizedException unauthorizedException -> {
                log.warn("Authorization failed", unauthorizedException);
                setErrorAttributes(errorAttributes, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED",
                        "Unauthorized", "Authorization is invalid", path);
            }
            case null, default -> {
                log.error("Unexpected error", error);
                
                setErrorAttributes(errorAttributes, HttpStatus.INTERNAL_SERVER_ERROR, "UNEXPECTED_ERROR",
                        "Internal Server Error", "An unexpected error occurred, please contact administrators.",
                        getPath(serverRequest));
            }
        }
        
        errorAttributes.put("timestamp", LocalDateTime.now());
        errorAttributes.put("requestId", serverRequest.exchange().getRequest().getId());
        return errorAttributes;
    }
    
    private static String getPath(ServerRequest serverRequest) {
        return serverRequest.path() + (serverRequest.uri().getQuery() != null ? "?" + serverRequest.uri().getQuery() : "");
    }
    
    private void setErrorAttributes(Map<String, Object> errorAttributes, HttpStatus status,
                                    String code, String error, String message, String path) {
        errorAttributes.put("status", status.value());
        errorAttributes.put("code", code);
        errorAttributes.put("error", error);
        errorAttributes.put("message", message);
        errorAttributes.put("path", path);
    }
}
