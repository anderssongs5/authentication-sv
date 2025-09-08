package co.com.powerup.ags.authentication.model.user.valueobjects;

import java.util.Objects;
import java.util.regex.Pattern;

public record PhoneNumber(String value) {
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d+$");
    
    public PhoneNumber(String value) {
        this.value = validate(value);
    }
    
    private String validate(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number must have a value");
        }
        
        String trimmedNumber = phoneNumber.trim();
        
        if (!PHONE_PATTERN.matcher(trimmedNumber).matches()) {
            throw new IllegalArgumentException("Phone number must contain only numbers: " + phoneNumber);
        }
        
        return trimmedNumber;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    @SuppressWarnings("NullableProblems")
    public String toString() {
        return value;
    }
}