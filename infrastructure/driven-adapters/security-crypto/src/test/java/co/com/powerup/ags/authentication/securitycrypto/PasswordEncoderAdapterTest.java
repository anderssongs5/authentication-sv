package co.com.powerup.ags.authentication.securitycrypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class PasswordEncoderAdapterTest {
    
    @Mock
    private PasswordEncoder springPasswordEncoder;
    
    private PasswordEncoderAdapter passwordEncoderAdapter;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoderAdapter = new PasswordEncoderAdapter(springPasswordEncoder);
    }
    
    @Test
    void shouldEncodePasswordSuccessfully() {
        String plainText = "ValidPassword123";
        String expectedHash = "hashedPassword123";
        
        when(springPasswordEncoder.encode(plainText)).thenReturn(expectedHash);
        
        String result = passwordEncoderAdapter.encode(plainText);
        
        assertThat(result).isEqualTo(expectedHash);
    }
    
    @Test
    void shouldThrowExceptionWhenEncodingNullPassword() {
        assertThatThrownBy(() -> passwordEncoderAdapter.encode(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password cannot be null");
    }
    
    @Test
    void shouldReturnTrueWhenPasswordsMatch() {
        String plainText = "ValidPassword123";
        String hashedPassword = "hashedPassword123";
        
        when(springPasswordEncoder.matches(eq(plainText), eq(hashedPassword))).thenReturn(true);
        
        boolean result = passwordEncoderAdapter.matches(plainText, hashedPassword);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldReturnFalseWhenPasswordsDoNotMatch() {
        String plainText = "ValidPassword123";
        String wrongPassword = "WrongPassword123";
        String hashedPassword = "hashedPassword123";
        
        when(springPasswordEncoder.matches(eq(wrongPassword), eq(hashedPassword))).thenReturn(false);
        
        boolean result = passwordEncoderAdapter.matches(wrongPassword, hashedPassword);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldReturnFalseWhenPlainTextPasswordIsNull() {
        String hashedPassword = "hashedPassword123";
        
        boolean result = passwordEncoderAdapter.matches(null, hashedPassword);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldReturnFalseWhenHashedPasswordIsNull() {
        String plainText = "ValidPassword123";
        
        boolean result = passwordEncoderAdapter.matches(plainText, null);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldReturnFalseWhenBothPasswordsAreNull() {
        boolean result = passwordEncoderAdapter.matches(null, null);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldHandleExceptionDuringMatching() {
        String plainText = "ValidPassword123";
        String hashedPassword = "hashedPassword123";
        
        when(springPasswordEncoder.matches(eq(plainText), eq(hashedPassword)))
                .thenThrow(new RuntimeException("Encoding error"));
        
        boolean result = passwordEncoderAdapter.matches(plainText, hashedPassword);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldEncodeEmptyPassword() {
        String emptyPassword = "";
        String expectedHash = "hashedEmptyPassword";
        
        when(springPasswordEncoder.encode(emptyPassword)).thenReturn(expectedHash);
        
        String result = passwordEncoderAdapter.encode(emptyPassword);
        
        assertThat(result).isEqualTo(expectedHash);
    }
    
    @Test
    void shouldMatchEmptyPasswords() {
        String emptyPassword = "";
        String hashedEmptyPassword = "hashedEmptyPassword";
        
        when(springPasswordEncoder.matches(eq(emptyPassword), eq(hashedEmptyPassword))).thenReturn(true);
        
        boolean result = passwordEncoderAdapter.matches(emptyPassword, hashedEmptyPassword);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldEncodePasswordWithSpecialCharacters() {
        String passwordWithSpecial = "P@ssw0rd!@#$%^&*()";
        String expectedHash = "hashedSpecialPassword";
        
        when(springPasswordEncoder.encode(passwordWithSpecial)).thenReturn(expectedHash);
        
        String result = passwordEncoderAdapter.encode(passwordWithSpecial);
        
        assertThat(result).isEqualTo(expectedHash);
    }
    
    @Test
    void shouldMatchPasswordWithSpecialCharacters() {
        String passwordWithSpecial = "P@ssw0rd!@#$%^&*()";
        String hashedSpecialPassword = "hashedSpecialPassword";
        
        when(springPasswordEncoder.matches(eq(passwordWithSpecial), eq(hashedSpecialPassword))).thenReturn(true);
        
        boolean result = passwordEncoderAdapter.matches(passwordWithSpecial, hashedSpecialPassword);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldEncodeVeryLongPassword() {
        String longPassword = "a".repeat(1000);
        String expectedHash = "hashedLongPassword";
        
        when(springPasswordEncoder.encode(longPassword)).thenReturn(expectedHash);
        
        String result = passwordEncoderAdapter.encode(longPassword);
        
        assertThat(result).isEqualTo(expectedHash);
    }
    
    @Test
    void shouldMatchVeryLongPassword() {
        String longPassword = "a".repeat(1000);
        String hashedLongPassword = "hashedLongPassword";
        
        when(springPasswordEncoder.matches(eq(longPassword), eq(hashedLongPassword))).thenReturn(true);
        
        boolean result = passwordEncoderAdapter.matches(longPassword, hashedLongPassword);
        
        assertThat(result).isTrue();
    }
}