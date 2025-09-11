package co.com.powerup.ags.authentication.usecase.auth;

import co.com.powerup.ags.authentication.model.auth.TokenDTO;
import co.com.powerup.ags.authentication.model.auth.gateways.AuthGateway;
import co.com.powerup.ags.authentication.model.common.exception.InvalidAuthorizationException;
import co.com.powerup.ags.authentication.model.common.exception.InvalidCredentialsException;
import co.com.powerup.ags.authentication.model.role.Role;
import co.com.powerup.ags.authentication.model.role.gateways.RoleRepository;
import co.com.powerup.ags.authentication.model.user.EnrichedUser;
import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.gateways.PasswordEncoder;
import co.com.powerup.ags.authentication.model.user.gateways.UserRepository;
import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.Password;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;
import co.com.powerup.ags.authentication.usecase.auth.dto.LoginCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private AuthGateway authGateway;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    private AuthUseCase authUseCase;
    
    private LoginCommand loginCommand;
    private User userResponse;
    private Password password;
    private EnrichedUser enrichedUser;
    private TokenDTO tokenDTO;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        authUseCase = new AuthUseCase(userRepository, roleRepository, passwordEncoder, authGateway);
        
        loginCommand = new LoginCommand(
                new Email("user@test.com"),
                "Secret123*"
        );
        
        password = new Password("Secret123*");
        
        userResponse = new User(
                "123e4567-e89b-12d3-a456-426614174000",
                "Steven",
                "Garcia",
                "Carrera 60 # 53-14",
                new PhoneNumber("1234567890"),
                LocalDate.of(1990, 10, 1),
                new Email("user@test.com"),
                new BigDecimal("50000.00"),
                "123456789",
                password,
                1
        );
        
        User user = new User(
                "123e4567-e89b-12d3-a456-426614174000",
                "Steven",
                "Garcia",
                "Carrera 60 # 53-14",
                new PhoneNumber("1234567890"),
                LocalDate.of(1990, 10, 1),
                new Email("user@test.com"),
                new BigDecimal("50000.00"),
                "123456789",
                Password.fromStoredData("hashedSecret123*"),
                1
        );
        
        Role role = new Role(1, "ADMIN", "Administrator role");
        enrichedUser = new EnrichedUser(user, role);
        
        tokenDTO = new TokenDTO("eyJhbGciOiJIUzI1NiJ9.test.token", "Bearer", 3600L);
        
        adminRole = new Role(1, "ADMIN", "Administrator with full system access.");
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Mono.just(userResponse));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(authGateway.generateToken(any(EnrichedUser.class))).thenReturn(Mono.just(tokenDTO));
        when(roleRepository.getRoleById(userResponse.roleId())).thenReturn(Mono.just(adminRole));
        
        Mono<TokenDTO> result = authUseCase.authenticateUser(loginCommand);
        
        StepVerifier.create(result)
                .assertNext(token -> {
                    assertThat(token).isNotNull();
                    assertThat(token.token()).isEqualTo("eyJhbGciOiJIUzI1NiJ9.test.token");
                    assertThat(token.tokenType()).isEqualTo("Bearer");
                    assertThat(token.expiresIn()).isEqualTo(3600L);
                })
                .verifyComplete();
                
        verify(userRepository, times(2)).findByEmail("user@test.com");
        verify(passwordEncoder).matches(anyString(), anyString());
        verify(authGateway).generateToken(any(EnrichedUser.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Mono.error(new InvalidCredentialsException("User not found")));
        
        Mono<TokenDTO> result = authUseCase.authenticateUser(loginCommand);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof InvalidCredentialsException &&
                        throwable.getMessage().contains("User not found"))
                .verify();
                
        verify(userRepository).findByEmail("user@test.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(authGateway, never()).generateToken(any(EnrichedUser.class));
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Mono.just(userResponse));
        when(passwordEncoder.matches(eq("wrongpassword"), anyString())).thenReturn(false);
        
        LoginCommand wrongPasswordRequest = new LoginCommand(
                new Email("user@test.com"),
                "wrongpassword"
        );
        
        Mono<TokenDTO> result = authUseCase.authenticateUser(wrongPasswordRequest);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof InvalidCredentialsException &&
                        throwable.getMessage().contains("Invalid credentials"))
                .verify();
                
        verify(userRepository).findByEmail("user@test.com");
        verify(passwordEncoder).matches(eq("wrongpassword"), anyString());
        verify(authGateway, never()).generateToken(any(EnrichedUser.class));
        verify(roleRepository, never()).getRoleById(anyInt());
    }

    @Test
    void shouldGetClaimsSuccessfully() {
        String token = "eyJhbGciOiJIUzI1NiJ9.test.token";
        Map<String, Object> expectedClaims = Map.of(
                "sub", "user@test.com",
                "role", "ADMIN",
                "iat", 1630500000,
                "exp", 1630586400
        );
        
        when(authGateway.validateToken(token)).thenReturn(Mono.just(true));
        when(authGateway.getClaims(token)).thenReturn(Mono.just(expectedClaims));
        
        Mono<Map<String, Object>> result = authUseCase.getClaims(token);
        
        StepVerifier.create(result)
                .assertNext(claims -> {
                    assertThat(claims).isNotNull();
                    assertThat(claims).containsEntry("sub", "user@test.com");
                    assertThat(claims).containsEntry("role", "ADMIN");
                    assertThat(claims).containsEntry("iat", 1630500000);
                    assertThat(claims).containsEntry("exp", 1630586400);
                })
                .verifyComplete();
                
        verify(authGateway).validateToken(token);
        verify(authGateway).getClaims(token);
    }

    @Test
    void shouldHandleExceptionWhenGettingClaims() {
        String invalidToken = "invalid.token";
        
        when(authGateway.validateToken(invalidToken)).thenReturn(Mono.just(false));
        
        Mono<Map<String, Object>> result = authUseCase.getClaims(invalidToken);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof InvalidAuthorizationException)
                .verify();
                
        verify(authGateway).validateToken(invalidToken);
        verify(authGateway, never()).getClaims(invalidToken);
    }

    @Test
    void shouldHandleTokenGenerationFailure() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Mono.just(userResponse));
        when(passwordEncoder.matches(eq("Secret123*"), anyString())).thenReturn(true);
        when(roleRepository.getRoleById(userResponse.roleId())).thenReturn(Mono.just(adminRole));
        when(authGateway.generateToken(any(EnrichedUser.class)))
                .thenReturn(Mono.error(new RuntimeException("Token generation failed")));
        
        Mono<TokenDTO> result = authUseCase.authenticateUser(loginCommand);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Token generation failed"))
                .verify();
                
        verify(userRepository, times(2)).findByEmail("user@test.com");
        verify(passwordEncoder).matches(eq("Secret123*"), anyString());
        verify(roleRepository).getRoleById(userResponse.roleId());
        verify(authGateway).generateToken(any(EnrichedUser.class));
    }

    @Test
    void shouldHandleDatabaseErrorDuringUserRetrieval() {
        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Mono.error(new RuntimeException("Database connection failed")));
        
        Mono<TokenDTO> result = authUseCase.authenticateUser(loginCommand);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database connection failed"))
                .verify();
                
        verify(userRepository).findByEmail("user@test.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(authGateway, never()).generateToken(any(EnrichedUser.class));
    }
}