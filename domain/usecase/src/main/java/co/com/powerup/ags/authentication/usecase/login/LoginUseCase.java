package co.com.powerup.ags.authentication.usecase.login;

import co.com.powerup.ags.authentication.model.auth.TokenDTO;
import co.com.powerup.ags.authentication.usecase.login.dto.LoginCommand;
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

@RequiredArgsConstructor
public class LoginUseCase {
    
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
                        user.id(),
                        user.name(),
                        user.lastName(),
                        user.address(),
                        user.phoneNumber(),
                        user.birthDate(),
                        user.email(),
                        user.baseSalary(),
                        user.idNumber(),
                        user.password(),
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
}
