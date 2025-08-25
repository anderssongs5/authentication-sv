package co.com.powerup.ags.authentication.usecase.user.mapper;

import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;
import co.com.powerup.ags.authentication.usecase.user.dto.CreateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UpdateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UserResponse;

public class UserMapper {
    
    private UserMapper() {
        super();
    }
    
    public static User commandToUser(CreateUserCommand command) {
        return new User(
                null,
                command.name(),
                command.lastName(),
                command.address(),
                new PhoneNumber(command.phoneNumber()),
                command.birthDate(),
                new Email(command.email()),
                command.baseSalary()
        );
    }
    
    public static User commandToUser(UpdateUserCommand command) {
        return new User(
                command.id(),
                command.name(),
                command.lastName(),
                command.address(),
                new PhoneNumber(command.phoneNumber()),
                command.birthDate(),
                new Email(command.email()),
                command.baseSalary()
        );
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
                user.baseSalary()
        );
    }
}