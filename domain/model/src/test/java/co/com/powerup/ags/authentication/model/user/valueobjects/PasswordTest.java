package co.com.powerup.ags.authentication.model.user.valueobjects;

import co.com.powerup.ags.authentication.model.user.gateways.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordTest {
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Test
    void shouldCreatePasswordFromPlainText() {
        String plainText = "ValidPass123";
        String hashedPassword = "hashedPassword123";
        
        when(passwordEncoder.encode(plainText)).thenReturn(hashedPassword);
        
        Password password = Password.fromPlainText(plainText, passwordEncoder);
        
        assertThat(password).isNotNull();
        assertThat(password.hashedPassword()).isEqualTo(hashedPassword);
    }
    
    @Test
    void shouldCreatePasswordFromStoredData() {
        String storedHash = "storedHashedPassword123";
        
        Password password = Password.fromStoredData(storedHash);
        
        assertThat(password).isNotNull();
        assertThat(password.hashedPassword()).isEqualTo(storedHash);
    }
    
    @Test
    void shouldMatchCorrectPlainTextPassword() {
        String plainText = "ValidPass123";
        String hashedPassword = "hashedPassword123";
        
        when(passwordEncoder.encode(plainText)).thenReturn(hashedPassword);
        when(passwordEncoder.matches(eq(plainText), eq(hashedPassword))).thenReturn(true);
        
        Password password = Password.fromPlainText(plainText, passwordEncoder);
        
        assertThat(password.matches(plainText, passwordEncoder)).isTrue();
    }
    
    @Test
    void shouldNotMatchIncorrectPlainTextPassword() {
        String plainText = "ValidPass123";
        String wrongPassword = "WrongPass123";
        String hashedPassword = "hashedPassword123";
        
        when(passwordEncoder.encode(plainText)).thenReturn(hashedPassword);
        when(passwordEncoder.matches(eq(wrongPassword), eq(hashedPassword))).thenReturn(false);
        
        Password password = Password.fromPlainText(plainText, passwordEncoder);
        
        assertThat(password.matches(wrongPassword, passwordEncoder)).isFalse();
    }
    
    @Test
    void shouldNotMatchNullPassword() {
        String plainText = "ValidPass123";
        String hashedPassword = "hashedPassword123";
        
        when(passwordEncoder.encode(plainText)).thenReturn(hashedPassword);
        
        Password password = Password.fromPlainText(plainText, passwordEncoder);
        
        assertThat(password.matches(null, passwordEncoder)).isFalse();
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldThrowExceptionForInvalidPlainTextPassword(String invalidPassword) {
        assertThatThrownBy(() -> Password.fromPlainText(invalidPassword, passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password cannot be null or empty");
    }
    
    @Test
    void shouldThrowExceptionForNullPasswordEncoder() {
        assertThatThrownBy(() -> Password.fromPlainText("ValidPass123", null))
                .isInstanceOf(NullPointerException.class);
    }
    
    @Test
    void shouldThrowExceptionForNullHashedPassword() {
        assertThatThrownBy(() -> Password.fromStoredData(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Hashed password cannot be null");
    }
    
    @Test
    void shouldThrowExceptionForEmptyHashedPassword() {
        assertThatThrownBy(() -> Password.fromStoredData(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Hashed password cannot be empty");
    }
    
    @Test
    void shouldThrowExceptionForBlankHashedPassword() {
        assertThatThrownBy(() -> Password.fromStoredData("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Hashed password cannot be empty");
    }
    
    @Test
    void shouldValidatePasswordStrength_TooShort() {
        String shortPassword = "Short1";
        
        assertThatThrownBy(() -> Password.fromPlainText(shortPassword, passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password must be at least 8 characters long");
    }
    
    @Test
    void shouldValidatePasswordStrength_NoUppercase() {
        String noUppercase = "validpass123";
        
        assertThatThrownBy(() -> Password.fromPlainText(noUppercase, passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password must contain at least one uppercase letter, one lowercase letter, and one digit");
    }
    
    @Test
    void shouldValidatePasswordStrength_NoLowercase() {
        String noLowercase = "VALIDPASS123";
        
        assertThatThrownBy(() -> Password.fromPlainText(noLowercase, passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password must contain at least one uppercase letter, one lowercase letter, and one digit");
    }
    
    @Test
    void shouldValidatePasswordStrength_NoDigit() {
        String noDigit = "ValidPassword";
        
        assertThatThrownBy(() -> Password.fromPlainText(noDigit, passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password must contain at least one uppercase letter, one lowercase letter, and one digit");
    }
    
    @Test
    void shouldAcceptValidPasswordWithMinimumRequirements() {
        String validPassword = "Valid123";
        String hashedPassword = "hashedPassword123";
        
        when(passwordEncoder.encode(validPassword)).thenReturn(hashedPassword);
        
        Password password = Password.fromPlainText(validPassword, passwordEncoder);
        
        assertThat(password).isNotNull();
        assertThat(password.hashedPassword()).isEqualTo(hashedPassword);
    }
    
    @Test
    void shouldAcceptValidPasswordWithSpecialCharacters() {
        String validPassword = "Valid123!@#";
        String hashedPassword = "hashedPassword123";
        
        when(passwordEncoder.encode(validPassword)).thenReturn(hashedPassword);
        
        Password password = Password.fromPlainText(validPassword, passwordEncoder);
        
        assertThat(password).isNotNull();
        assertThat(password.hashedPassword()).isEqualTo(hashedPassword);
    }
    
    @Test
    void shouldTrimWhitespaceFromPlainTextPassword() {
        String passwordWithSpaces = "  Valid123  ";
        String trimmedPassword = "Valid123";
        String hashedPassword = "hashedPassword123";
        
        when(passwordEncoder.encode(trimmedPassword)).thenReturn(hashedPassword);
        when(passwordEncoder.matches(eq(trimmedPassword), eq(hashedPassword))).thenReturn(true);
        
        Password password = Password.fromPlainText(passwordWithSpaces, passwordEncoder);
        
        assertThat(password.matches(trimmedPassword, passwordEncoder)).isTrue();
    }
    
    @Test
    void shouldNotRevealPasswordInToString() {
        String plainText = "ValidPass123";
        String hashedPassword = "hashedPassword123";
        
        when(passwordEncoder.encode(plainText)).thenReturn(hashedPassword);
        
        Password password = Password.fromPlainText(plainText, passwordEncoder);
        String toString = password.toString();
        
        assertThat(toString).doesNotContain(hashedPassword);
        assertThat(toString).doesNotContain(plainText);
        assertThat(toString).isEqualTo("Password{hashedPassword='[PROTECTED]'}");
    }
    
    @Test
    void shouldBeEqualWhenHashedPasswordsAreTheSame() {
        String hashedPassword = "sameHashedPassword";
        
        Password password1 = Password.fromStoredData(hashedPassword);
        Password password2 = Password.fromStoredData(hashedPassword);
        
        assertThat(password1).isEqualTo(password2);
        assertThat(password1.hashCode()).isEqualTo(password2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenHashedPasswordsAreDifferent() {
        Password password1 = Password.fromStoredData("hashedPassword1");
        Password password2 = Password.fromStoredData("hashedPassword2");
        
        assertThat(password1).isNotEqualTo(password2);
    }
}