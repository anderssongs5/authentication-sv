package co.com.powerup.ags.authentication.model.common.exception;

public class InvalidAuthorizationException extends RuntimeException {
    
    public InvalidAuthorizationException(String message) {
        super(message);
    }
}