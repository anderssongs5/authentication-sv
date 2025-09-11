package co.com.powerup.ags.authentication.api.security;

import co.com.powerup.ags.authentication.api.exception.UnauthorizedException;
import co.com.powerup.ags.authentication.usecase.auth.AuthUseCase;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

import static co.com.powerup.ags.authentication.api.constants.SecurityConstants.*;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final AuthUseCase authUseCase;

    public JwtAuthenticationManager(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        
        return authUseCase.getClaims(token)
                .map(claims -> new UsernamePasswordAuthenticationToken(
                        claims.get(SUBJECT_CLAIM).toString(),
                        null,
                        Stream.of(claims.get(ROLE_CLAIM).toString())
                                .map(List::of)
                                .flatMap(roles -> roles.stream()
                                        .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role)))
                                .toList()))
                .cast(Authentication.class)
                .onErrorMap(Exception.class, ex -> new UnauthorizedException("Invalid token"));
    }
}