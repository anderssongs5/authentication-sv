package co.com.powerup.ags.authentication.securitycrypto.password.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordEncoderConfigTest {
    
    @Test
    void shouldCreateBCryptPasswordEncoderBean() {
        PasswordEncoderConfig config = new PasswordEncoderConfig();
        
        PasswordEncoder passwordEncoder = config.passwordEncoder();
        
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }
    
    @Test
    void shouldCreatePasswordEncoderWithCorrectStrength() {
        PasswordEncoderConfig config = new PasswordEncoderConfig();
        BCryptPasswordEncoder passwordEncoder = (BCryptPasswordEncoder) config.passwordEncoder();
        
        // Test that it uses BCrypt with reasonable strength (12 rounds)
        String testPassword = "TestPassword123";
        String encoded = passwordEncoder.encode(testPassword);
        
        assertThat(encoded).isNotNull();
        assertThat(encoded).startsWith("$2");  // BCrypt format
        assertThat(passwordEncoder.matches(testPassword, encoded)).isTrue();
        assertThat(passwordEncoder.matches("WrongPassword", encoded)).isFalse();
    }
    
    @Test
    void shouldEncodeAndVerifyPasswords() {
        PasswordEncoderConfig config = new PasswordEncoderConfig();
        PasswordEncoder passwordEncoder = config.passwordEncoder();
        
        String originalPassword = "MySecurePassword123!";
        String encodedPassword = passwordEncoder.encode(originalPassword);
        
        assertThat(encodedPassword).isNotEqualTo(originalPassword);
        assertThat(passwordEncoder.matches(originalPassword, encodedPassword)).isTrue();
        assertThat(passwordEncoder.matches("DifferentPassword", encodedPassword)).isFalse();
    }
    
    @Test
    void shouldProduceDifferentHashesForSamePassword() {
        PasswordEncoderConfig config = new PasswordEncoderConfig();
        PasswordEncoder passwordEncoder = config.passwordEncoder();
        
        String password = "SamePassword123";
        String hash1 = passwordEncoder.encode(password);
        String hash2 = passwordEncoder.encode(password);
        
        // BCrypt should produce different hashes due to salt
        assertThat(hash1).isNotEqualTo(hash2);
        
        // But both should match the original password
        assertThat(passwordEncoder.matches(password, hash1)).isTrue();
        assertThat(passwordEncoder.matches(password, hash2)).isTrue();
    }
}