package co.com.powerup.ags.authentication.securitycrypto.token;

import co.com.powerup.ags.authentication.model.role.Role;
import co.com.powerup.ags.authentication.model.user.EnrichedUser;
import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.Password;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest {

    private static final String SECRET = "bXlTZWNyZXRLZXkxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTA=";
    private static final Long EXPIRATION_TIME = 3600L;
    private static final String INVALID_SECRET = "bXlXcm9uZ1NlY3JldEtleTEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MCo=";
    
    private EnrichedUser enrichedUser;

    @BeforeEach
    void setUp() {
        User user = new User(
                "123e4567-e89b-12d3-a456-426614174000",
                "Steven",
                "Garcia",
                "Carrera 60 # 53-14",
                new PhoneNumber("1234567890"),
                LocalDate.of(1990, 10, 1),
                new Email("steven.garcia@test.com"),
                new BigDecimal("50000.00"),
                "123456789",
                Password.fromStoredData("hashedPassword123"),
                1
        );
        
        Role role = new Role(1, "ADMIN", "Administrator with full system access.");
        enrichedUser = new EnrichedUser(user, role);
    }

    @Test
    void shouldGenerateValidJwtToken() {
        String token = JwtProvider.generateToken(enrichedUser, EXPIRATION_TIME, SECRET);
        
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void shouldGenerateTokenWithCorrectClaims() {
        String token = JwtProvider.generateToken(enrichedUser, EXPIRATION_TIME, SECRET);
        Claims claims = JwtProvider.getClaims(token, SECRET);
        
        assertThat(claims.getSubject()).isEqualTo("steven.garcia@test.com");
        assertThat(claims.get("role")).isEqualTo("ADMIN");
        assertThat(claims.getIssuedAt()).isBeforeOrEqualTo(new Date());
        assertThat(claims.getExpiration()).isAfter(new Date());
    }

    @Test
    void shouldValidateValidToken() {
        String token = JwtProvider.generateToken(enrichedUser, EXPIRATION_TIME, SECRET);
        
        boolean isValid = JwtProvider.validate(token, SECRET);
        
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldNotValidateTokenWithWrongSecret() {
        String token = JwtProvider.generateToken(enrichedUser, EXPIRATION_TIME, SECRET);
        
        boolean isValid = JwtProvider.validate(token, INVALID_SECRET);
        
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldNotValidateMalformedToken() {
        String malformedToken = "invalid.token.format";
        
        boolean isValid = JwtProvider.validate(malformedToken, SECRET);
        
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldNotValidateNullToken() {
        boolean isValid = JwtProvider.validate(null, SECRET);
        
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldNotValidateEmptyToken() {
        boolean isValid = JwtProvider.validate("", SECRET);
        
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldNotValidateExpiredToken() {
        String expiredToken = JwtProvider.generateToken(enrichedUser, -1L, SECRET);
        
        boolean isValid = JwtProvider.validate(expiredToken, SECRET);
        
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldExtractSubjectFromValidToken() {
        String token = JwtProvider.generateToken(enrichedUser, EXPIRATION_TIME, SECRET);
        
        String subject = JwtProvider.getSubject(token, SECRET);
        
        assertThat(subject).isEqualTo("steven.garcia@test.com");
    }

    @Test
    void shouldThrowExceptionWhenExtractingSubjectFromInvalidToken() {
        String invalidToken = "invalid.token.format";
        
        assertThatThrownBy(() -> JwtProvider.getSubject(invalidToken, SECRET))
                .isInstanceOf(MalformedJwtException.class);
    }

    @Test
    void shouldExtractClaimsFromValidToken() {
        String token = JwtProvider.generateToken(enrichedUser, EXPIRATION_TIME, SECRET);
        
        Claims claims = JwtProvider.getClaims(token, SECRET);
        
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo("steven.garcia@test.com");
        assertThat(claims.get("role")).isEqualTo("ADMIN");
    }

    @Test
    void shouldThrowExceptionWhenExtractingClaimsFromInvalidToken() {
        String invalidToken = "invalid.token.format";
        
        assertThatThrownBy(() -> JwtProvider.getClaims(invalidToken, SECRET))
                .isInstanceOf(MalformedJwtException.class);
    }

    @Test
    void shouldThrowExceptionWhenExtractingClaimsFromExpiredToken() {
        String expiredToken = JwtProvider.generateToken(enrichedUser, -1L, SECRET);
        
        assertThatThrownBy(() -> JwtProvider.getClaims(expiredToken, SECRET))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void shouldThrowExceptionWhenGeneratingTokenWithNullUser() {
        assertThatThrownBy(() -> JwtProvider.generateToken(null, EXPIRATION_TIME, SECRET))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowExceptionWhenGeneratingTokenWithNullSecret() {
        assertThatThrownBy(() -> JwtProvider.generateToken(enrichedUser, EXPIRATION_TIME, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldHandleUnsupportedJwtException() {
        String unsupportedToken = "eyJhbGciOiJub25lIn0.eyJzdWIiOiJ0ZXN0In0.";
        
        boolean isValid = JwtProvider.validate(unsupportedToken, SECRET);
        
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldGenerateTokensWithDifferentExpirationTimes() {
        String shortToken = JwtProvider.generateToken(enrichedUser, 1L, SECRET);
        String longToken = JwtProvider.generateToken(enrichedUser, 7200L, SECRET);
        
        Claims shortClaims = JwtProvider.getClaims(shortToken, SECRET);
        Claims longClaims = JwtProvider.getClaims(longToken, SECRET);
        
        assertThat(shortClaims.getExpiration()).isBefore(longClaims.getExpiration());
    }

    @Test
    void shouldGenerateUniqueTokensForSameUser() {
        String token1 = JwtProvider.generateToken(enrichedUser, EXPIRATION_TIME, SECRET);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String token2 = JwtProvider.generateToken(enrichedUser, EXPIRATION_TIME, SECRET);
        
        assertThat(token1).isNotEqualTo(token2);
    }
}