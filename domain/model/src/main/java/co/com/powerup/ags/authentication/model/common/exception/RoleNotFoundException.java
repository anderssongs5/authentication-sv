package co.com.powerup.ags.authentication.model.common.exception;

public class RoleNotFoundException extends RuntimeException {
    
    public RoleNotFoundException(String message) {
        super(message);
    }
}
