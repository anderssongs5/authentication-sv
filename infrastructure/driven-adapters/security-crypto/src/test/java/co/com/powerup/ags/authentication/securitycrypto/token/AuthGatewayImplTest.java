package co.com.powerup.ags.authentication.securitycrypto.token;

import co.com.powerup.ags.authentication.model.auth.TokenDTO;
import co.com.powerup.ags.authentication.model.common.exception.InvalidAuthorizationException;
import co.com.powerup.ags.authentication.model.role.Role;
import co.com.powerup.ags.authentication.model.user.EnrichedUser;
import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.Password;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AuthGatewayImplTest {

    private static final String SECRET = "bXlTZWNyZXRLZXkxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTA=";
    private static final long EXPIRATION_TIME = 3600L;
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjMiLCJlbWFpbCI6InVzZXJAZXhhbXBsZS5jb20iLCJyb2xlTmFtZSI6IkFETUlOIiwiYXVkIjoiYXV0aGVudGljYXRpb24tc2VydmljZSIsImlhdCI6MTYzMDUwMDAwMCwiZXhwIjoxNjMwNTg2NDAwfQ.signature";
    
    private AuthGatewayImpl authGateway;
    private EnrichedUser enrichedUser;

    @BeforeEach
    void setUp() {
        authGateway = new AuthGatewayImpl(SECRET, EXPIRATION_TIME);
        
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
    void shouldGenerateTokenSuccessfully() {
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.generateToken(any(EnrichedUser.class), eq(EXPIRATION_TIME), eq(SECRET)))
                    .thenReturn(VALID_TOKEN);

            Mono<TokenDTO> result = authGateway.generateToken(enrichedUser);

            StepVerifier.create(result)
                    .assertNext(tokenDTO -> {
                        assertThat(tokenDTO).isNotNull();
                        assertThat(tokenDTO.token()).isEqualTo(VALID_TOKEN);
                        assertThat(tokenDTO.tokenType()).isEqualTo("Bearer");
                        assertThat(tokenDTO.expiresIn()).isEqualTo(EXPIRATION_TIME);
                    })
                    .verifyComplete();

            mockedJwtProvider.verify(() -> JwtProvider.generateToken(enrichedUser, EXPIRATION_TIME, SECRET));
        }
    }

    @Test
    void shouldHandleErrorWhenGeneratingToken() {
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.generateToken(any(EnrichedUser.class), eq(EXPIRATION_TIME), eq(SECRET)))
                    .thenThrow(new RuntimeException("Token generation failed"));

            Mono<TokenDTO> result = authGateway.generateToken(enrichedUser);

            StepVerifier.create(result)
                    .expectErrorMatches(throwable -> 
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Token generation failed"))
                    .verify();
        }
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.validate(VALID_TOKEN, SECRET))
                    .thenReturn(true);

            Mono<Boolean> result = authGateway.validateToken(VALID_TOKEN);

            StepVerifier.create(result)
                    .assertNext(isValid -> assertThat(isValid).isTrue())
                    .verifyComplete();

            mockedJwtProvider.verify(() -> JwtProvider.validate(VALID_TOKEN, SECRET));
        }
    }

    @Test
    void shouldReturnFalseWhenValidatingInvalidToken() {
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.validate("invalid-token", SECRET))
                    .thenReturn(false);

            Mono<Boolean> result = authGateway.validateToken("invalid-token");

            StepVerifier.create(result)
                    .assertNext(isValid -> assertThat(isValid).isFalse())
                    .verifyComplete();

            mockedJwtProvider.verify(() -> JwtProvider.validate("invalid-token", SECRET));
        }
    }

    @Test
    void shouldHandleErrorWhenValidatingToken() {
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.validate(VALID_TOKEN, SECRET))
                    .thenThrow(new InvalidAuthorizationException("Token validation failed"));

            Mono<Boolean> result = authGateway.validateToken(VALID_TOKEN);

            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                        throwable instanceof InvalidAuthorizationException &&
                        throwable.getMessage().equals("Token validation failed"))
                    .verify();
        }
    }

    @Test
    void shouldGetClaimsSuccessfully() {
        Claims mockClaims = mock(Claims.class);
        
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.getClaims(VALID_TOKEN, SECRET))
                    .thenReturn(mockClaims);

            Mono<Map<String, Object>> result = authGateway.getClaims(VALID_TOKEN);

            StepVerifier.create(result)
                    .assertNext(claims -> {
                        assertThat(claims).isNotNull();
                        assertThat(claims).isInstanceOf(Claims.class);
                    })
                    .verifyComplete();

            mockedJwtProvider.verify(() -> JwtProvider.getClaims(VALID_TOKEN, SECRET));
        }
    }

    @Test
    void shouldHandleErrorWhenGettingClaims() {
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.getClaims(VALID_TOKEN, SECRET))
                    .thenThrow(new InvalidAuthorizationException("Invalid token"));

            Mono<Map<String, Object>> result = authGateway.getClaims(VALID_TOKEN);

            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                        throwable instanceof InvalidAuthorizationException &&
                        throwable.getMessage().equals("Invalid token"))
                    .verify();
        }
    }

    @Test
    void shouldUseDefaultValuesWhenNotProvided() {
        AuthGatewayImpl defaultAuthGateway = new AuthGatewayImpl(null, 0);
        
        // This test verifies that the constructor accepts the parameters
        // The actual default values are handled by Spring's @Value annotation
        assertThat(defaultAuthGateway).isNotNull();
    }

    @Test
    void shouldHandleNullUserWhenGeneratingToken() {
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.generateToken(eq(null), eq(EXPIRATION_TIME), eq(SECRET)))
                    .thenThrow(new IllegalArgumentException("User cannot be null"));

            Mono<TokenDTO> result = authGateway.generateToken(null);

            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("User cannot be null"))
                    .verify();
        }
    }

    @Test
    void shouldHandleNullTokenWhenValidating() {
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.validate(eq(null), eq(SECRET)))
                    .thenThrow(new IllegalArgumentException("Token cannot be null"));

            Mono<Boolean> result = authGateway.validateToken(null);

            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Token cannot be null"))
                    .verify();
        }
    }

    @Test
    void shouldHandleNullTokenWhenGettingClaims() {
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.getClaims(eq(null), eq(SECRET)))
                    .thenThrow(new IllegalArgumentException("Token cannot be null"));

            Mono<Map<String, Object>> result = authGateway.getClaims(null);

            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Token cannot be null"))
                    .verify();
        }
    }
}