package co.com.powerup.ags.authentication.api.config;

import co.com.powerup.ags.authentication.api.exception.AccessDeniedException;
import co.com.powerup.ags.authentication.api.security.JwtFilter;
import co.com.powerup.ags.authentication.api.security.SecurityContextRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SecurityWebFilterConfigTest {

    @Mock
    private SecurityContextRepository securityContextRepository;
    
    @Mock
    private JwtFilter jwtFilter;
    
    @Mock
    private ServerWebExchange exchange;
    
    private SecurityWebFilterConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityWebFilterConfig(securityContextRepository);
    }

    @Test
    void shouldCreateAccessDeniedHandler() {
        ServerAccessDeniedHandler handler = securityConfig.accessDeniedHandler();
        
        assertThat(handler).isNotNull();
    }

    @Test
    void shouldHandleAccessDenied() {
        ServerAccessDeniedHandler handler = securityConfig.accessDeniedHandler();
        AuthorizationDeniedException denied = mock(AuthorizationDeniedException.class);
        
        Mono<Void> result = handler.handle(exchange, denied);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof AccessDeniedException &&
                        throwable.getMessage().equals("Access denied. Insufficient permissions."))
                .verify();
    }

    @Test
    void shouldCreateSecurityWebFilterChain() {
        ServerHttpSecurity http = ServerHttpSecurity.http();
        
        SecurityWebFilterChain filterChain = securityConfig.filterChain(http, jwtFilter);
        
        assertThat(filterChain).isNotNull();
    }

    @Test
    void shouldConfigureFilterChainWithCorrectSettings() {
        ServerHttpSecurity http = ServerHttpSecurity.http();
        
        SecurityWebFilterChain filterChain = securityConfig.filterChain(http, jwtFilter);
        
        assertThat(filterChain).isNotNull();
        assertThat(filterChain.getWebFilters()).isNotNull();
    }

    @Test
    void shouldCreateConfigurationWithDependencies() {
        assertThat(securityConfig).isNotNull();
    }

    @Test
    void shouldHandleNullSecurityContextRepository() {
        SecurityWebFilterConfig config = new SecurityWebFilterConfig(null);
        
        assertThat(config).isNotNull();
    }

    @Test
    void shouldCreateAccessDeniedHandlerWithCorrectMessage() {
        ServerAccessDeniedHandler handler = securityConfig.accessDeniedHandler();
        AuthorizationDeniedException denied = mock(AuthorizationDeniedException.class);
        
        Mono<Void> result = handler.handle(exchange, denied);
        
        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable).isInstanceOf(AccessDeniedException.class);
                    assertThat(throwable.getMessage()).isEqualTo("Access denied. Insufficient permissions.");
                })
                .verify();
    }

    @Test
    void shouldBuildFilterChainSuccessfully() {
        ServerHttpSecurity http = ServerHttpSecurity.http();
        
        SecurityWebFilterChain result = securityConfig.filterChain(http, jwtFilter);
        
        assertThat(result).isNotNull();
        assertThat(result.getWebFilters()).isNotNull();
    }
}