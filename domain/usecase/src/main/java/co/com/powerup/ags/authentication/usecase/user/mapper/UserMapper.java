package co.com.powerup.ags.authentication.usecase.user.mapper;

import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;
import co.com.powerup.ags.authentication.usecase.user.dto.CreateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UpdateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UserResponse;
import reactor.core.publisher.Mono;

public class UserMapper {
    
    private UserMapper() {
        super();
    }
    
    
    public static Mono<User> commandToUser(CreateUserCommand command) {
        return Mono.fromCallable(() -> new User(
                null,
                command.name(),
                command.lastName(),
                command.address(),
                new PhoneNumber(command.phoneNumber()),
                command.birthDate(),
                new Email(command.email()),
                command.baseSalary(),
                command.idNumber()
        ))
        .onErrorMap(IllegalArgumentException.class, 
            ex -> new IllegalArgumentException("User validation failed: " + ex.getMessage()));
    }
    
    public static Mono<User> commandToUser(UpdateUserCommand command) {
        return Mono.fromCallable(() -> new User(
                command.id(),
                command.name(),
                command.lastName(),
                command.address(),
                new PhoneNumber(command.phoneNumber()),
                command.birthDate(),
                new Email(command.email()),
                command.baseSalary(),
                command.idNumber()
        ))
        .onErrorMap(IllegalArgumentException.class,
            ex -> new IllegalArgumentException("User validation failed: " + ex.getMessage()));
    }
    
    public static UserResponse userToResponse(User user) {
        return new UserResponse(
                user.id(),
                user.name(),
                user.lastName(),
                user.address(),
                user.phoneNumber().value(),
                user.birthDate(),
                user.email().value(),
                user.baseSalary(),
                user.idNumber()
        );
    }
}