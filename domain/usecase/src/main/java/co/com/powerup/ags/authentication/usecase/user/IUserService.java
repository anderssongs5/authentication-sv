package co.com.powerup.ags.authentication.usecase.user;

import co.com.powerup.autenticacion.usecase.user.dto.CreateUserCommand;
import co.com.powerup.autenticacion.usecase.user.dto.UpdateUserCommand;
import co.com.powerup.autenticacion.usecase.user.dto.UserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserService {
    
    Mono<UserResponse> createUser(CreateUserCommand command);
    
    Mono<UserResponse> updateUser(UpdateUserCommand command);
    
    Flux<UserResponse> getAllUsers();
    
    Mono<UserResponse> getUserById(String id);
    
    Mono<Void> deleteUser(String id);
    
    Mono<UserResponse> getUserByEmail(String email);
}
