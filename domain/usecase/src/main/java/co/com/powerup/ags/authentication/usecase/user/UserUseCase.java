package co.com.powerup.ags.authentication.usecase.user;

import co.com.powerup.ags.authentication.model.common.exception.DataAlreadyExistsException;
import co.com.powerup.ags.authentication.model.common.exception.EntityNotFoundException;
import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.gateways.UserRepository;
import co.com.powerup.ags.authentication.usecase.user.dto.CreateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UpdateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UserResponse;
import co.com.powerup.ags.authentication.usecase.user.mapper.UserMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class UserUseCase implements IUserService {
    
    private final UserRepository userRepository;
    
    public UserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public Mono<UserResponse> createUser(CreateUserCommand command) {
        return userRepository.existsByEmail(command.email())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(
                                new DataAlreadyExistsException("User already exists with email: " + command.email())
                        );
                    }
                    User user = UserMapper.commandToUser(command);
                    User userWithId = new User(
                            UUID.randomUUID().toString(),
                            user.name(),
                            user.lastName(),
                            user.address(),
                            user.phoneNumber(),
                            user.birthDate(),
                            user.email(),
                            user.baseSalary()
                    );
                    return userRepository.save(userWithId);
                })
                .map(UserMapper::userToResponse);
    }
    
    @Override
    public Mono<UserResponse> updateUser(UpdateUserCommand command) {
        return userRepository.findById(command.id())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found with ID: " + command.id())))
                .flatMap(existingUser -> {
                    User updatedUser = UserMapper.commandToUser(command);
                    return userRepository.save(updatedUser);
                })
                .map(UserMapper::userToResponse);
    }
    
    @Override
    public Flux<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .map(UserMapper::userToResponse);
    }
    
    @Override
    public Mono<UserResponse> getUserById(String id) {
        if (id == null) {
            return Mono.error(new IllegalArgumentException("User ID cannot be null"));
        }
        
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found with ID: " + id)))
                .map(UserMapper::userToResponse);
    }
    
    @Override
    public Mono<Void> deleteUser(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found with ID: " + id)))
                .flatMap(user -> userRepository.deleteById(id));
    }
    
    @Override
    public Mono<UserResponse> getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Email cannot be null or empty"));
        }
        
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found with email: " + email)))
                .map(UserMapper::userToResponse);
    }
}
