package co.com.powerup.ags.authentication.model.auth.gateways;

import co.com.powerup.ags.authentication.model.auth.TokenDTO;
import co.com.powerup.ags.authentication.model.user.EnrichedUser;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface AuthGateway {

    Mono<TokenDTO> generateToken(EnrichedUser user);

    Mono<Boolean> validateToken(String token);

    Mono<Map<String, Object>> getClaims(String token);
}