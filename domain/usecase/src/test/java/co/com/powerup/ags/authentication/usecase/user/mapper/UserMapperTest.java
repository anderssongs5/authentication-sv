package co.com.powerup.ags.authentication.usecase.user.mapper;

import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;
import co.com.powerup.ags.authentication.usecase.user.dto.CreateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UpdateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private static final String USER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String USER_NAME = "Steven";
    private static final String USER_LAST_NAME = "Garcia";
    private static final String USER_ADDRESS = "Carrera 60 # 53-14";
    private static final String USER_PHONE_NUMBER = "1234567890";
    private static final LocalDate USER_BIRTH_DATE = LocalDate.of(1990, 10, 1);
    private static final String USER_EMAIL = "steven.garcia@test.com";
    private static final BigDecimal USER_BASE_SALARY = new BigDecimal("50000.00");

    private CreateUserCommand createUserCommand;
    private UpdateUserCommand updateUserCommand;
    private User user;

    @BeforeEach
    void setUp() {
        createUserCommand = new CreateUserCommand(
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY
        );

        updateUserCommand = new UpdateUserCommand(
                USER_ID,
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY
        );

        user = new User(
                USER_ID,
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                new PhoneNumber(USER_PHONE_NUMBER),
                USER_BIRTH_DATE,
                new Email(USER_EMAIL),
                USER_BASE_SALARY
        );
    }

    @Test
    void shouldMapCreateUserCommandToUser() {
        User result = UserMapper.commandToUser(createUserCommand);

        assertThat(result).isNotNull();
        assertThat(result.id()).isNull();
        assertThat(result.name()).isEqualTo(createUserCommand.name());
        assertThat(result.lastName()).isEqualTo(createUserCommand.lastName());
        assertThat(result.address()).isEqualTo(createUserCommand.address());
        assertThat(result.phoneNumber()).isNotNull();
        assertThat(result.phoneNumber().value()).isEqualTo(createUserCommand.phoneNumber());
        assertThat(result.birthDate()).isEqualTo(createUserCommand.birthDate());
        assertThat(result.email()).isNotNull();
        assertThat(result.email().value()).isEqualTo(createUserCommand.email());
        assertThat(result.baseSalary()).isEqualTo(createUserCommand.baseSalary());
    }

    @Test
    void shouldMapUpdateUserCommandToUser() {
        User result = UserMapper.commandToUser(updateUserCommand);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(updateUserCommand.id());
        assertThat(result.name()).isEqualTo(updateUserCommand.name());
        assertThat(result.lastName()).isEqualTo(updateUserCommand.lastName());
        assertThat(result.address()).isEqualTo(updateUserCommand.address());
        assertThat(result.phoneNumber()).isNotNull();
        assertThat(result.phoneNumber().value()).isEqualTo(updateUserCommand.phoneNumber());
        assertThat(result.birthDate()).isEqualTo(updateUserCommand.birthDate());
        assertThat(result.email()).isNotNull();
        assertThat(result.email().value()).isEqualTo(updateUserCommand.email());
        assertThat(result.baseSalary()).isEqualTo(updateUserCommand.baseSalary());
    }

    @Test
    void shouldMapUserToResponse() {
        UserResponse result = UserMapper.userToResponse(user);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(user.id());
        assertThat(result.name()).isEqualTo(user.name());
        assertThat(result.lastName()).isEqualTo(user.lastName());
        assertThat(result.address()).isEqualTo(user.address());
        assertThat(result.phoneNumber()).isEqualTo(user.phoneNumber().value());
        assertThat(result.birthDate()).isEqualTo(user.birthDate());
        assertThat(result.email()).isEqualTo(user.email().value());
        assertThat(result.baseSalary()).isEqualTo(user.baseSalary());
    }

    @Test
    void shouldCreatePhoneNumberValueObjectFromCreateCommand() {
        User result = UserMapper.commandToUser(createUserCommand);

        assertThat(result.phoneNumber()).isInstanceOf(PhoneNumber.class);
        assertThat(result.phoneNumber().value()).isEqualTo(USER_PHONE_NUMBER);
    }

    @Test
    void shouldCreateEmailValueObjectFromCreateCommand() {
        User result = UserMapper.commandToUser(createUserCommand);

        assertThat(result.email()).isInstanceOf(Email.class);
        assertThat(result.email().value()).isEqualTo(USER_EMAIL);
    }

    @Test
    void shouldCreatePhoneNumberValueObjectFromUpdateCommand() {
        User result = UserMapper.commandToUser(updateUserCommand);

        assertThat(result.phoneNumber()).isInstanceOf(PhoneNumber.class);
        assertThat(result.phoneNumber().value()).isEqualTo(USER_PHONE_NUMBER);
    }

    @Test
    void shouldCreateEmailValueObjectFromUpdateCommand() {
        User result = UserMapper.commandToUser(updateUserCommand);

        assertThat(result.email()).isInstanceOf(Email.class);
        assertThat(result.email().value()).isEqualTo(USER_EMAIL);
    }

    @Test
    void shouldExtractValueObjectValuesInResponse() {
        UserResponse result = UserMapper.userToResponse(user);

        assertThat(result.phoneNumber()).isInstanceOf(String.class);
        assertThat(result.email()).isInstanceOf(String.class);
        assertThat(result.phoneNumber()).isEqualTo(USER_PHONE_NUMBER);
        assertThat(result.email()).isEqualTo(USER_EMAIL);
    }

    @Test
    void shouldSetNullIdForCreateUserCommand() {
        User result = UserMapper.commandToUser(createUserCommand);

        assertThat(result.id()).isNull();
    }

    @Test
    void shouldPreserveIdForUpdateUserCommand() {
        User result = UserMapper.commandToUser(updateUserCommand);

        assertThat(result.id()).isEqualTo(USER_ID);
    }

    @Test
    void shouldPreserveAllDataTypesInMappings() {
        User userFromCreate = UserMapper.commandToUser(createUserCommand);
        User userFromUpdate = UserMapper.commandToUser(updateUserCommand);
        UserResponse response = UserMapper.userToResponse(user);

        assertThat(userFromCreate.birthDate()).isInstanceOf(LocalDate.class);
        assertThat(userFromCreate.baseSalary()).isInstanceOf(BigDecimal.class);
        assertThat(userFromUpdate.birthDate()).isInstanceOf(LocalDate.class);
        assertThat(userFromUpdate.baseSalary()).isInstanceOf(BigDecimal.class);
        assertThat(response.birthDate()).isInstanceOf(LocalDate.class);
        assertThat(response.baseSalary()).isInstanceOf(BigDecimal.class);

        assertThat(userFromCreate.birthDate()).isEqualTo(USER_BIRTH_DATE);
        assertThat(userFromCreate.baseSalary()).isEqualTo(USER_BASE_SALARY);
        assertThat(userFromUpdate.birthDate()).isEqualTo(USER_BIRTH_DATE);
        assertThat(userFromUpdate.baseSalary()).isEqualTo(USER_BASE_SALARY);
        assertThat(response.birthDate()).isEqualTo(USER_BIRTH_DATE);
        assertThat(response.baseSalary()).isEqualTo(USER_BASE_SALARY);
    }

    @Test
    void shouldHandleRoundTripMappingFromCreateCommand() {
        User userFromCommand = UserMapper.commandToUser(createUserCommand);
        UserResponse response = UserMapper.userToResponse(userFromCommand);

        assertThat(response.name()).isEqualTo(createUserCommand.name());
        assertThat(response.lastName()).isEqualTo(createUserCommand.lastName());
        assertThat(response.address()).isEqualTo(createUserCommand.address());
        assertThat(response.phoneNumber()).isEqualTo(createUserCommand.phoneNumber());
        assertThat(response.birthDate()).isEqualTo(createUserCommand.birthDate());
        assertThat(response.email()).isEqualTo(createUserCommand.email());
        assertThat(response.baseSalary()).isEqualTo(createUserCommand.baseSalary());
    }

    @Test
    void shouldHandleRoundTripMappingFromUpdateCommand() {
        User userFromCommand = UserMapper.commandToUser(updateUserCommand);
        UserResponse response = UserMapper.userToResponse(userFromCommand);

        assertThat(response.id()).isEqualTo(updateUserCommand.id());
        assertThat(response.name()).isEqualTo(updateUserCommand.name());
        assertThat(response.lastName()).isEqualTo(updateUserCommand.lastName());
        assertThat(response.address()).isEqualTo(updateUserCommand.address());
        assertThat(response.phoneNumber()).isEqualTo(updateUserCommand.phoneNumber());
        assertThat(response.birthDate()).isEqualTo(updateUserCommand.birthDate());
        assertThat(response.email()).isEqualTo(updateUserCommand.email());
        assertThat(response.baseSalary()).isEqualTo(updateUserCommand.baseSalary());
    }

    @Test
    void shouldCreateValidDomainObjectsWithValidation() {
        User userFromCreate = UserMapper.commandToUser(createUserCommand);
        User userFromUpdate = UserMapper.commandToUser(updateUserCommand);

        assertThat(userFromCreate.name()).isNotNull();
        assertThat(userFromCreate.lastName()).isNotNull();
        assertThat(userFromCreate.phoneNumber()).isNotNull();
        assertThat(userFromCreate.email()).isNotNull();
        assertThat(userFromCreate.baseSalary()).isNotNull();

        assertThat(userFromUpdate.id()).isNotNull();
        assertThat(userFromUpdate.name()).isNotNull();
        assertThat(userFromUpdate.lastName()).isNotNull();
        assertThat(userFromUpdate.phoneNumber()).isNotNull();
        assertThat(userFromUpdate.email()).isNotNull();
        assertThat(userFromUpdate.baseSalary()).isNotNull();
    }
}