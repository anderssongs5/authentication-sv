package co.com.powerup.ags.authentication.api.security;

import co.com.powerup.ags.authentication.api.exception.UnauthorizedException;
import co.com.powerup.ags.authentication.usecase.auth.AuthUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationManagerTest {

    @Mock
    private AuthUseCase authUseCase;
    
    private JwtAuthenticationManager authenticationManager;
    
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.test.token";
    private static final String INVALID_TOKEN = "invalid.token";

    @BeforeEach
    void setUp() {
        authenticationManager = new JwtAuthenticationManager(authUseCase);
    }

    @Test
    void shouldAuthenticateWithValidToken() {
        Map<String, Object> claims = Map.of(
                "sub", "user@test.com",
                "role", "ADMIN"
        );
        
        when(authUseCase.getClaims(VALID_TOKEN)).thenReturn(Mono.just(claims));
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, VALID_TOKEN);
        Mono<Authentication> result = authenticationManager.authenticate(authentication);
        
        StepVerifier.create(result)
                .assertNext(auth -> {
                    assertThat(auth).isNotNull();
                    assertThat(auth.getPrincipal()).isEqualTo("user@test.com");
                    assertThat(auth.getCredentials()).isNull();
                    //assertThat(auth.getAuthorities()).contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
                })
                .verifyComplete();
    }

    @Test
    void shouldFailAuthenticationWithInvalidToken() {
        when(authUseCase.getClaims(INVALID_TOKEN))
                .thenReturn(Mono.error(new RuntimeException("Invalid token")));
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, INVALID_TOKEN);
        Mono<Authentication> result = authenticationManager.authenticate(authentication);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UnauthorizedException &&
                        throwable.getMessage().equals("Invalid token"))
                .verify();
    }

    @Test
    void shouldHandleEmptyToken() {
        when(authUseCase.getClaims(""))
                .thenReturn(Mono.error(new RuntimeException("Empty token")));
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, "");
        Mono<Authentication> result = authenticationManager.authenticate(authentication);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UnauthorizedException &&
                        throwable.getMessage().equals("Invalid token"))
                .verify();
    }

    @Test
    void shouldHandleMultipleRoles() {
        Map<String, Object> claims = Map.of(
                "sub", "admin@test.com",
                "role", "ADMIN"
        );
        
        when(authUseCase.getClaims(VALID_TOKEN)).thenReturn(Mono.just(claims));
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, VALID_TOKEN);
        Mono<Authentication> result = authenticationManager.authenticate(authentication);
        
        StepVerifier.create(result)
                .assertNext(auth -> {
                    assertThat(auth).isNotNull();
                    assertThat(auth.getPrincipal()).isEqualTo("admin@test.com");
                    assertThat(auth.getAuthorities()).hasSize(1);
                    //assertThat(auth.getAuthorities()).contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleMissingSubjectClaim() {
        Map<String, Object> claims = Map.of("role", "USER");
        
        when(authUseCase.getClaims(VALID_TOKEN)).thenReturn(Mono.just(claims));
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, VALID_TOKEN);
        Mono<Authentication> result = authenticationManager.authenticate(authentication);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UnauthorizedException &&
                        throwable.getMessage().equals("Invalid token"))
                .verify();
    }

    @Test
    void shouldHandleMissingRoleClaim() {
        Map<String, Object> claims = Map.of("sub", "user@test.com");
        
        when(authUseCase.getClaims(VALID_TOKEN)).thenReturn(Mono.just(claims));
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, VALID_TOKEN);
        Mono<Authentication> result = authenticationManager.authenticate(authentication);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UnauthorizedException &&
                        throwable.getMessage().equals("Invalid token"))
                .verify();
    }

    @Test
    void shouldHandleExpiredToken() {
        when(authUseCase.getClaims(VALID_TOKEN))
                .thenReturn(Mono.error(new RuntimeException("Token expired")));
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, VALID_TOKEN);
        Mono<Authentication> result = authenticationManager.authenticate(authentication);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UnauthorizedException &&
                        throwable.getMessage().equals("Invalid token"))
                .verify();
    }

    @Test
    void shouldHandleUserRole() {
        Map<String, Object> claims = Map.of(
                "sub", "user@test.com",
                "role", "USER"
        );
        
        when(authUseCase.getClaims(VALID_TOKEN)).thenReturn(Mono.just(claims));
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, VALID_TOKEN);
        Mono<Authentication> result = authenticationManager.authenticate(authentication);
        
        StepVerifier.create(result)
                .assertNext(auth -> {
                    assertThat(auth).isNotNull();
                    assertThat(auth.getPrincipal()).isEqualTo("user@test.com");
                    //assertThat(auth.getAuthorities()).contains(new SimpleGrantedAuthority("ROLE_USER"));
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleAdvisorRole() {
        Map<String, Object> claims = Map.of(
                "sub", "advisor@test.com",
                "role", "ADVISOR"
        );
        
        when(authUseCase.getClaims(VALID_TOKEN)).thenReturn(Mono.just(claims));
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, VALID_TOKEN);
        Mono<Authentication> result = authenticationManager.authenticate(authentication);
        
        StepVerifier.create(result)
                .assertNext(auth -> {
                    assertThat(auth).isNotNull();
                    assertThat(auth.getPrincipal()).isEqualTo("advisor@test.com");
                    //assertThat(auth.getAuthorities()).contains(new SimpleGrantedAuthority("ROLE_ADVISOR"));
                })
                .verifyComplete();
    }
}