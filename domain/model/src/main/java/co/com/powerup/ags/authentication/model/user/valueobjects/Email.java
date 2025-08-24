package co.com.powerup.ags.authentication.model.user.valueobjects;

import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String value) {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    public Email(String value) {
        this.value = validate(value);
    }
    
    private String validate(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo debe tener un valor");
        }
        
        String trimmedEmail = email.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            throw new IllegalArgumentException("El correo no tiene un formato válido: " + email);
        }
        
        return trimmedEmail;
    }
    
    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }
    
    public String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }
    
    @Override
    @SuppressWarnings("NullableProblems")
    public String toString() {
        return value;
    }
}