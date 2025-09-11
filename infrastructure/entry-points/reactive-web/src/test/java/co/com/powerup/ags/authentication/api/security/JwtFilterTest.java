package co.com.powerup.ags.authentication.api.security;

import co.com.powerup.ags.authentication.api.constants.SecurityConstants;
import co.com.powerup.ags.authentication.api.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {
    
    @InjectMocks
    private JwtFilter jwtFilter;
    
    @Mock
    private ServerWebExchange exchange;
    
    @Mock
    private ServerHttpRequest request;
    
    @Mock
    private WebFilterChain chain;
    
    @Mock
    private RequestPath requestPath;
    
    @Mock
    private HttpHeaders headers;
    
    private Map<String, Object> attributes;
    
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    private static final String BEARER_PREFIX = "Bearer ";
    
    @BeforeEach
    void setUp() {
        when(exchange.getRequest()).thenReturn(request);
        
        attributes = new HashMap<>();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/v3/api-docs.yaml",
            "/v3/api-docs/**",
            "/webjars/swagger-ui/**",
            "/api/v1/login",
            "/api/v1/introspect"
    })
    void shouldPassThroughWhenPathIsExcluded(String excludedPath) {
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        when(request.getPath()).thenReturn(requestPath);
        when(requestPath.value()).thenReturn(excludedPath);

        Mono<Void> result = jwtFilter.filter(exchange, chain);

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(exchange).getRequest();
        verify(request).getPath();
        verify(requestPath).value();
        verify(chain).filter(exchange);
        verify(exchange, never()).getAttributes();
    }
    
    @Test
    void shouldAddTokenAttributeWhenValidHeaderIsPresent() {
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        when(request.getPath()).thenReturn(requestPath);
        when(requestPath.value()).thenReturn("/api/v1/users");
        when(request.getHeaders()).thenReturn(headers);
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(exchange.getAttributes()).thenReturn(attributes);

        Mono<Void> result = jwtFilter.filter(exchange, chain);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        verify(exchange).getRequest();
        verify(request).getPath();
        verify(requestPath).value();
        verify(chain).filter(exchange);
        assert(attributes.containsKey(SecurityConstants.TOKEN));
        assert(attributes.get(SecurityConstants.TOKEN).equals(VALID_TOKEN));
    }
    
    @Test
    void shouldReturnErrorWhenNoAuthorizationHeaderIsPresent() {
        when(request.getPath()).thenReturn(requestPath);
        when(requestPath.value()).thenReturn("/api/v1/users");
        when(request.getHeaders()).thenReturn(headers);
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        Mono<Void> result = jwtFilter.filter(exchange, chain);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof UnauthorizedException &&
                        throwable.getMessage().equals("No token was found"))
                .verify();

        verify(chain, never()).filter(exchange);
    }
    
    @Test
    void shouldReturnErrorWhenHeaderHasWrongFormat() {
        when(request.getPath()).thenReturn(requestPath);
        when(requestPath.value()).thenReturn("/api/v1/users");
        when(request.getHeaders()).thenReturn(headers);
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn("InvalidTokenFormat");

        Mono<Void> result = jwtFilter.filter(exchange, chain);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof UnauthorizedException &&
                        throwable.getMessage().equals("Invalid authorization header format"))
                .verify();

        verify(chain, never()).filter(exchange);
    }
}