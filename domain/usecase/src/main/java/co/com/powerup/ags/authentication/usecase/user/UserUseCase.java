package co.com.powerup.ags.authentication.usecase.user;

import co.com.powerup.ags.authentication.model.common.exception.DataAlreadyExistsException;
import co.com.powerup.ags.authentication.model.common.exception.UserNotFoundException;
import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.gateways.UserRepository;
import co.com.powerup.ags.authentication.usecase.user.dto.CreateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UpdateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UserResponse;
import co.com.powerup.ags.authentication.usecase.user.mapper.UserMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class UserUseCase {
    
    public static final String USER_NOT_FOUND_ID = "User not found with ID: ";
    private final UserRepository userRepository;
    
    public UserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<UserResponse> createUser(CreateUserCommand command) {
        return userRepository.existsByEmail(command.email())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(
                                new DataAlreadyExistsException("User already exists with email: " + command.email())
                        );
                    }
                    return UserMapper.commandToUser(command)
                            .map(user -> new User(
                                    UUID.randomUUID().toString(),
                                    user.name(),
                                    user.lastName(),
                                    user.address(),
                                    user.phoneNumber(),
                                    user.birthDate(),
                                    user.email(),
                                    user.baseSalary()
                            ));
                })
                .flatMap(userRepository::save)
                .map(UserMapper::userToResponse);
    }
    
    public Mono<UserResponse> updateUser(UpdateUserCommand command) {
        return userRepository.findById(command.id())
                .switchIfEmpty(Mono.error(new UserNotFoundException(USER_NOT_FOUND_ID + command.id())))
                .flatMap(existingUser -> UserMapper.commandToUser(command))
                .flatMap(userRepository::save)
                .map(UserMapper::userToResponse);
    }
    
    public Flux<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .map(UserMapper::userToResponse);
    }
    
    public Mono<UserResponse> getUserById(String id) {
        return Mono.justOrEmpty(id)
                .filter(userId -> !userId.trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User ID cannot be null or empty")))
                .flatMap(userRepository::findById)
                .switchIfEmpty(Mono.error(new UserNotFoundException(USER_NOT_FOUND_ID + id)))
                .map(UserMapper::userToResponse);
    }
    
    public Mono<Void> deleteUser(String id) {
        return Mono.justOrEmpty(id)
                .filter(userId -> !userId.trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User ID cannot be null or empty")))
                .flatMap(userRepository::findById)
                .switchIfEmpty(Mono.error(new UserNotFoundException(USER_NOT_FOUND_ID + id)))
                .flatMap(user -> userRepository.deleteById(id));
    }
    
    public Mono<UserResponse> getUserByEmail(String email) {
        return Mono.justOrEmpty(email)
                .filter(mail -> !mail.trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Email cannot be null or empty")))
                .flatMap(userRepository::findByEmail)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with email: " + email)))
                .map(UserMapper::userToResponse);
    }
}
