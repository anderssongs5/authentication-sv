package co.com.powerup.ags.authentication.usecase.auth;

import co.com.powerup.ags.authentication.model.auth.TokenDTO;
import co.com.powerup.ags.authentication.model.common.exception.InvalidAuthorizationException;
import co.com.powerup.ags.authentication.usecase.auth.dto.LoginCommand;
import co.com.powerup.ags.authentication.model.auth.gateways.AuthGateway;
import co.com.powerup.ags.authentication.model.common.exception.InvalidCredentialsException;
import co.com.powerup.ags.authentication.model.role.gateways.RoleRepository;
import co.com.powerup.ags.authentication.model.user.EnrichedUser;
import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.gateways.PasswordEncoder;
import co.com.powerup.ags.authentication.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import javax.security.auth.login.CredentialException;
import java.util.Map;

@RequiredArgsConstructor
public class AuthUseCase {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthGateway authGateway;
    
    public Mono<TokenDTO> authenticateUser(LoginCommand loginCommand) {
        return verifyPassword(loginCommand.email().value(), loginCommand.password())
                .flatMap(isValid -> {
                    if (Boolean.FALSE.equals(isValid)) {
                        return Mono.error(new InvalidCredentialsException("Invalid credentials"));
                    }
                    return userRepository.findByEmail(loginCommand.email().value())
                            .flatMap(this::enrichUserWithFullRole);
                })
                .flatMap(authGateway::generateToken);
    }
    
    private Mono<EnrichedUser> enrichUserWithFullRole(User user) {
        return roleRepository.getRoleById(user.roleId())
                .map(fullRole -> new EnrichedUser(
                        user,
                        fullRole
                ));
    }
    
    private Mono<Boolean> verifyPassword(String email, String plainTextPassword) {
        return Mono.justOrEmpty(email)
                .filter(e -> e != null && !e.trim().isEmpty())
                .switchIfEmpty(Mono.error(new CredentialException("Email cannot be null or empty")))
                .flatMap(userRepository::findByEmail)
                .switchIfEmpty(Mono.error(new CredentialException("User not found with email: " + email)))
                .map(user -> user.password().matches(plainTextPassword, passwordEncoder));
    }
    
    public Mono<Map<String, Object>> getClaims(String token) {
        return authGateway.validateToken(token).flatMap(valid -> {
            if (Boolean.FALSE.equals(valid)) {
                return Mono.error(new InvalidAuthorizationException("Invalid token"));
            }
            
            return authGateway.getClaims(token);
        });
    }
}
