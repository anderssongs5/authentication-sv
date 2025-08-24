package co.com.powerup.ags.authentication.api.helper;

import co.com.powerup.ags.authentication.usecase.user.IUserService;
import co.com.powerup.ags.authentication.usecase.user.dto.CreateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UpdateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserHelper {
    
    private final IUserService userService;
    
    public UserHelper(IUserService userService) {
        this.userService = userService;
    }
    
    @Transactional
    public Mono<UserResponse> createUser(CreateUserCommand command) {
        return userService.createUser(command);
    }
    
    public Flux<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }
    
    public Mono<UserResponse> getUserById(String id) {
        return userService.getUserById(id);
    }
    
    public Mono<UserResponse> updateUser(UpdateUserCommand command) {
        return userService.updateUser(command);
    }
    
    public Mono<Void> deleteUser(String id) {
        return userService.deleteUser(id);
    }
    
    public Mono<UserResponse> getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }
}