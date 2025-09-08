package co.com.powerup.ags.authentication.model.user.valueobjects;

import co.com.powerup.ags.authentication.model.user.gateways.PasswordEncoder;

import java.util.Objects;

public record Password(String hashedPassword) {
    
    public Password(String hashedPassword) {
        this.hashedPassword = Objects.requireNonNull(hashedPassword, "Hashed password cannot be null");
        if (hashedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be empty");
        }
    }
    
    public static Password fromPlainText(String plainTextPassword, PasswordEncoder passwordEncoder) {
        if (plainTextPassword == null || plainTextPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        String trimmedPassword = plainTextPassword.trim();
        validatePasswordStrength(trimmedPassword);
        
        String hashedPassword = passwordEncoder.encode(trimmedPassword);
        
        return new Password(hashedPassword);
    }
    
    public static Password fromStoredData(String hashedPassword) {
        return new Password(hashedPassword);
    }
    
    public boolean matches(String plainTextPassword, PasswordEncoder passwordEncoder) {
        if (plainTextPassword == null) {
            return false;
        }
        
        return passwordEncoder.matches(plainTextPassword.trim(), this.hashedPassword);
    }
    
    private static void validatePasswordStrength(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        
        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        
        if (!(hasUppercase && hasLowercase && hasDigit)) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter, one lowercase letter, and one digit");
        }
    }
    
    @Override
    public String toString() {
        return "Password{hashedPassword='[PROTECTED]'}";
    }
}