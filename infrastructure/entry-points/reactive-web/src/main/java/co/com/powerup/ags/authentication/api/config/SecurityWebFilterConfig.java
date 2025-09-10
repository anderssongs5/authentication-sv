package co.com.powerup.ags.authentication.api.config;

import co.com.powerup.ags.authentication.api.constants.SecurityConstants;
import co.com.powerup.ags.authentication.api.exception.AccessDeniedException;
import co.com.powerup.ags.authentication.api.security.JwtFilter;
import co.com.powerup.ags.authentication.api.security.SecurityContextRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityWebFilterConfig {

    private final SecurityContextRepository securityContextRepository;

    public SecurityWebFilterConfig(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }
    
    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) ->
                Mono.error(new AccessDeniedException("Access denied. Insufficient permissions."));
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http, JwtFilter jwtFilter) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .addFilterAfter(jwtFilter, SecurityWebFiltersOrder.FIRST)
                .securityContextRepository(securityContextRepository)
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(SecurityConstants.EXCLUDED_PATTERNS.toArray(new String[0]))
                        .permitAll()
                        .anyExchange().authenticated()
                );
        return http.build();
    }
}