package co.com.powerup.ags.authentication.model.auth.gateways;

import co.com.powerup.ags.authentication.model.auth.TokenDTO;
import co.com.powerup.ags.authentication.model.user.EnrichedUser;
import reactor.core.publisher.Mono;

public interface AuthGateway {

    Mono<TokenDTO> generateToken(EnrichedUser user);

    Mono<Boolean> validateToken(String token);
}