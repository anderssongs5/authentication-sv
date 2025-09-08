package co.com.powerup.ags.authentication.model.user.gateways;

public interface PasswordEncoder {
    
    String encode(String plainTextPassword);
    
    boolean matches(String plainTextPassword, String hashedPassword);
}