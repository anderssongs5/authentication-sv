package co.com.powerup.ags.authentication.securitycrypto;

import co.com.powerup.ags.authentication.model.user.gateways.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncoderAdapter implements PasswordEncoder {
    
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public String encode(String plainTextPassword) {
        if (plainTextPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        
        return passwordEncoder.encode(plainTextPassword);
    }
    
    @Override
    public boolean matches(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            return passwordEncoder.matches(plainTextPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}