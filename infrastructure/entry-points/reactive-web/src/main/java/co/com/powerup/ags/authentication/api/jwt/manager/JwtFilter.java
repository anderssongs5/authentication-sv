package co.com.powerup.ags.authentication.api.jwt.manager;

import co.com.powerup.ags.authentication.api.constants.SecurityConstants;
import co.com.powerup.ags.authentication.api.exception.UnauthorizedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static co.com.powerup.ags.authentication.api.constants.SecurityConstants.BEARER_PREFIX;
import static co.com.powerup.ags.authentication.api.constants.SecurityConstants.TOKEN;

@Component
public class JwtFilter implements WebFilter {
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        
        boolean isExcluded = SecurityConstants.EXCLUDED_PATTERNS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
        
        if (isExcluded) {
            return chain.filter(exchange);
        }
        
        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null) {
            return Mono.error(new UnauthorizedException("No token was found"));
        }

        if (!auth.startsWith(BEARER_PREFIX)) {
            return Mono.error(new UnauthorizedException("Invalid authorization header format"));
        }

        String token = auth.replace(BEARER_PREFIX, "");
        exchange.getAttributes().put(TOKEN, token);
        return chain.filter(exchange);
    }
}
