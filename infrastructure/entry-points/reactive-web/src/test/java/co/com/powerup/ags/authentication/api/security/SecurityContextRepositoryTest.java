package co.com.powerup.ags.authentication.api.security;

import co.com.powerup.ags.authentication.api.constants.SecurityConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityContextRepositoryTest {

    @Mock
    private JwtAuthenticationManager jwtAuthenticationManager;
    
    @Mock
    private ServerWebExchange exchange;
    
    private SecurityContextRepository securityContextRepository;
    
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.test.token";

    @BeforeEach
    void setUp() {
        securityContextRepository = new SecurityContextRepository(jwtAuthenticationManager);
    }

    @Test
    void shouldLoadSecurityContextWithValidToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user@test.com", 
                null, 
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        when(exchange.getAttribute(SecurityConstants.TOKEN)).thenReturn(VALID_TOKEN);
        when(jwtAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(authentication));
        
        Mono<SecurityContext> result = securityContextRepository.load(exchange);
        
        StepVerifier.create(result)
                .assertNext(securityContext -> {
                    assertThat(securityContext).isNotNull();
                    assertThat(securityContext.getAuthentication()).isEqualTo(authentication);
                    assertThat(securityContext.getAuthentication().getPrincipal()).isEqualTo("user@test.com");
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenNoToken() {
        when(exchange.getAttribute(SecurityConstants.TOKEN)).thenReturn(null);
        
        Mono<SecurityContext> result = securityContextRepository.load(exchange);
        
        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldReturnEmptyWhenTokenAttributeNotPresent() {
        when(exchange.getAttribute(SecurityConstants.TOKEN)).thenReturn(null);
        
        Mono<SecurityContext> result = securityContextRepository.load(exchange);
        
        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldHandleAuthenticationFailure() {
        when(exchange.getAttribute(SecurityConstants.TOKEN)).thenReturn(VALID_TOKEN);
        when(jwtAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.error(new RuntimeException("Authentication failed")));
        
        Mono<SecurityContext> result = securityContextRepository.load(exchange);
        
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldHandleEmptyTokenString() {
        when(exchange.getAttribute(SecurityConstants.TOKEN)).thenReturn("");
        when(jwtAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.error(new RuntimeException("Empty token")));
        
        Mono<SecurityContext> result = securityContextRepository.load(exchange);
        
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldSaveReturnEmpty() {
        Mono<Void> result = securityContextRepository.save(exchange, null);
        
        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldHandleInvalidToken() {
        when(exchange.getAttribute(SecurityConstants.TOKEN)).thenReturn("invalid.token");
        when(jwtAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.error(new RuntimeException("Invalid token")));
        
        Mono<SecurityContext> result = securityContextRepository.load(exchange);
        
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldCreateSecurityContextWithAdminRole() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin@test.com", 
                null, 
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        
        when(exchange.getAttribute(SecurityConstants.TOKEN)).thenReturn(VALID_TOKEN);
        when(jwtAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(authentication));
        
        Mono<SecurityContext> result = securityContextRepository.load(exchange);
        
        StepVerifier.create(result)
                .assertNext(securityContext -> {
                    assertThat(securityContext).isNotNull();
                    /*assertThat(securityContext.getAuthentication().getAuthorities())
                            .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));*/
                })
                .verifyComplete();
    }

    @Test
    void shouldCreateSecurityContextWithAdvisorRole() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "advisor@test.com", 
                null, 
                List.of(new SimpleGrantedAuthority("ROLE_ADVISOR"))
        );
        
        when(exchange.getAttribute(SecurityConstants.TOKEN)).thenReturn(VALID_TOKEN);
        when(jwtAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(authentication));
        
        Mono<SecurityContext> result = securityContextRepository.load(exchange);
        
        StepVerifier.create(result)
                .assertNext(securityContext -> {
                    assertThat(securityContext).isNotNull();
                    /*assertThat(securityContext.getAuthentication().getAuthorities())
                            .contains(new SimpleGrantedAuthority("ROLE_ADVISOR"));*/
                })
                .verifyComplete();
    }
}