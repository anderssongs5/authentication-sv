package co.com.powerup.ags.authentication.securitycrypto.token;

import co.com.powerup.ags.authentication.model.auth.TokenDTO;
import co.com.powerup.ags.authentication.model.auth.gateways.AuthGateway;
import co.com.powerup.ags.authentication.model.user.EnrichedUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class AuthGatewayImpl implements AuthGateway {

    private final String secret;
    private final long expirationTime;

    public AuthGatewayImpl(
            @Value("${security.jwt.secret:bXlTZWNyZXRLZXkxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTA=}") String secret,
            @Value("${security.jwt.expiration:3600}") long expirationTime) {
        this.secret = secret;
        this.expirationTime = expirationTime;
    }
    
    @Override
    public Mono<TokenDTO> generateToken(EnrichedUser user) {
        return Mono.fromCallable(() -> {
            String token = JwtProvider.generateToken(user, expirationTime, secret);
            return new TokenDTO(token, "Bearer", expirationTime);
        });
    }
    
    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> JwtProvider.validate(token, secret));
    }
    
    @Override
    public Mono<Map<String, Object>> getClaims(String token) {
        return Mono.fromCallable(() -> JwtProvider.getClaims(token, secret));
    }
}