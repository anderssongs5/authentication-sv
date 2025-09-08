package co.com.powerup.ags.authentication.usecase.user.mapper;

import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;
import co.com.powerup.ags.authentication.usecase.user.dto.CreateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UpdateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
    private static final String USER_ID_NUMBER = "123456789";

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
                USER_BASE_SALARY,
                USER_ID_NUMBER
        );

        updateUserCommand = new UpdateUserCommand(
                USER_ID,
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                USER_ID_NUMBER
        );

        user = new User(
                USER_ID,
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                new PhoneNumber(USER_PHONE_NUMBER),
                USER_BIRTH_DATE,
                new Email(USER_EMAIL),
                USER_BASE_SALARY,
                USER_ID_NUMBER
        );
    }

    @Test
    void shouldMapCreateUserCommandToUser() {
        Mono<User> result = UserMapper.commandToUser(createUserCommand);

        StepVerifier.create(result)
                .assertNext(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.id()).isNull();
                    assertThat(user.name()).isEqualTo(createUserCommand.name());
                    assertThat(user.lastName()).isEqualTo(createUserCommand.lastName());
                    assertThat(user.address()).isEqualTo(createUserCommand.address());
                    assertThat(user.phoneNumber()).isNotNull();
                    assertThat(user.phoneNumber().value()).isEqualTo(createUserCommand.phoneNumber());
                    assertThat(user.birthDate()).isEqualTo(createUserCommand.birthDate());
                    assertThat(user.email()).isNotNull();
                    assertThat(user.email().value()).isEqualTo(createUserCommand.email());
                    assertThat(user.baseSalary()).isEqualTo(createUserCommand.baseSalary());
                    assertThat(user.idNumber()).isEqualTo(createUserCommand.idNumber());
                })
                .verifyComplete();
    }

    @Test
    void shouldMapUpdateUserCommandToUser() {
        Mono<User> result = UserMapper.commandToUser(updateUserCommand);

        StepVerifier.create(result)
                .assertNext(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.id()).isEqualTo(updateUserCommand.id());
                    assertThat(user.name()).isEqualTo(updateUserCommand.name());
                    assertThat(user.lastName()).isEqualTo(updateUserCommand.lastName());
                    assertThat(user.address()).isEqualTo(updateUserCommand.address());
                    assertThat(user.phoneNumber()).isNotNull();
                    assertThat(user.phoneNumber().value()).isEqualTo(updateUserCommand.phoneNumber());
                    assertThat(user.birthDate()).isEqualTo(updateUserCommand.birthDate());
                    assertThat(user.email()).isNotNull();
                    assertThat(user.email().value()).isEqualTo(updateUserCommand.email());
                    assertThat(user.baseSalary()).isEqualTo(updateUserCommand.baseSalary());
                    assertThat(user.idNumber()).isEqualTo(updateUserCommand.idNumber());
                })
                .verifyComplete();
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
        assertThat(result.idNumber()).isEqualTo(user.idNumber());
    }

    @Test
    void shouldCreatePhoneNumberValueObjectFromCreateCommand() {
        Mono<User> result = UserMapper.commandToUser(createUserCommand);

        StepVerifier.create(result)
                .assertNext(user -> {
                    assertThat(user.phoneNumber()).isInstanceOf(PhoneNumber.class);
                    assertThat(user.phoneNumber().value()).isEqualTo(USER_PHONE_NUMBER);
                })
                .verifyComplete();
    }

    @Test
    void shouldCreateEmailValueObjectFromCreateCommand() {
        Mono<User> result = UserMapper.commandToUser(createUserCommand);

        StepVerifier.create(result)
                .assertNext(user -> {
                    assertThat(user.email()).isInstanceOf(Email.class);
                    assertThat(user.email().value()).isEqualTo(USER_EMAIL);
                })
                .verifyComplete();
    }

    @Test
    void shouldCreatePhoneNumberValueObjectFromUpdateCommand() {
        Mono<User> result = UserMapper.commandToUser(updateUserCommand);

        StepVerifier.create(result)
                .assertNext(user -> {
                    assertThat(user.phoneNumber()).isInstanceOf(PhoneNumber.class);
                    assertThat(user.phoneNumber().value()).isEqualTo(USER_PHONE_NUMBER);
                })
                .verifyComplete();
    }

    @Test
    void shouldCreateEmailValueObjectFromUpdateCommand() {
        Mono<User> result = UserMapper.commandToUser(updateUserCommand);

        StepVerifier.create(result)
                .assertNext(user -> {
                    assertThat(user.email()).isInstanceOf(Email.class);
                    assertThat(user.email().value()).isEqualTo(USER_EMAIL);
                })
                .verifyComplete();
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
        Mono<User> result = UserMapper.commandToUser(createUserCommand);

        StepVerifier.create(result)
                .assertNext(user -> assertThat(user.id()).isNull())
                .verifyComplete();
    }

    @Test
    void shouldPreserveIdForUpdateUserCommand() {
        Mono<User> result = UserMapper.commandToUser(updateUserCommand);

        StepVerifier.create(result)
                .assertNext(user -> assertThat(user.id()).isEqualTo(USER_ID))
                .verifyComplete();
    }

    @Test
    void shouldPreserveAllDataTypesInMappings() {
        Mono<User> userFromCreateMono = UserMapper.commandToUser(createUserCommand);
        Mono<User> userFromUpdateMono = UserMapper.commandToUser(updateUserCommand);
        UserResponse response = UserMapper.userToResponse(user);

        StepVerifier.create(userFromCreateMono)
                .assertNext(userFromCreate -> {
                    assertThat(userFromCreate.birthDate()).isInstanceOf(LocalDate.class);
                    assertThat(userFromCreate.baseSalary()).isInstanceOf(BigDecimal.class);
                    assertThat(userFromCreate.birthDate()).isEqualTo(USER_BIRTH_DATE);
                    assertThat(userFromCreate.baseSalary()).isEqualTo(USER_BASE_SALARY);
                })
                .verifyComplete();

        StepVerifier.create(userFromUpdateMono)
                .assertNext(userFromUpdate -> {
                    assertThat(userFromUpdate.birthDate()).isInstanceOf(LocalDate.class);
                    assertThat(userFromUpdate.baseSalary()).isInstanceOf(BigDecimal.class);
                    assertThat(userFromUpdate.birthDate()).isEqualTo(USER_BIRTH_DATE);
                    assertThat(userFromUpdate.baseSalary()).isEqualTo(USER_BASE_SALARY);
                })
                .verifyComplete();

        assertThat(response.birthDate()).isInstanceOf(LocalDate.class);
        assertThat(response.baseSalary()).isInstanceOf(BigDecimal.class);
        assertThat(response.birthDate()).isEqualTo(USER_BIRTH_DATE);
        assertThat(response.baseSalary()).isEqualTo(USER_BASE_SALARY);
    }

    @Test
    void shouldHandleRoundTripMappingFromCreateCommand() {
        Mono<UserResponse> result = UserMapper.commandToUser(createUserCommand)
                .map(UserMapper::userToResponse);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.name()).isEqualTo(createUserCommand.name());
                    assertThat(response.lastName()).isEqualTo(createUserCommand.lastName());
                    assertThat(response.address()).isEqualTo(createUserCommand.address());
                    assertThat(response.phoneNumber()).isEqualTo(createUserCommand.phoneNumber());
                    assertThat(response.birthDate()).isEqualTo(createUserCommand.birthDate());
                    assertThat(response.email()).isEqualTo(createUserCommand.email());
                    assertThat(response.baseSalary()).isEqualTo(createUserCommand.baseSalary());
                    assertThat(response.idNumber()).isEqualTo(createUserCommand.idNumber());
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleRoundTripMappingFromUpdateCommand() {
        Mono<UserResponse> result = UserMapper.commandToUser(updateUserCommand)
                .map(UserMapper::userToResponse);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.id()).isEqualTo(updateUserCommand.id());
                    assertThat(response.name()).isEqualTo(updateUserCommand.name());
                    assertThat(response.lastName()).isEqualTo(updateUserCommand.lastName());
                    assertThat(response.address()).isEqualTo(updateUserCommand.address());
                    assertThat(response.phoneNumber()).isEqualTo(updateUserCommand.phoneNumber());
                    assertThat(response.birthDate()).isEqualTo(updateUserCommand.birthDate());
                    assertThat(response.email()).isEqualTo(updateUserCommand.email());
                    assertThat(response.baseSalary()).isEqualTo(updateUserCommand.baseSalary());
                    assertThat(response.idNumber()).isEqualTo(updateUserCommand.idNumber());
                })
                .verifyComplete();
    }

    @Test
    void shouldCreateValidDomainObjectsWithValidation() {
        Mono<User> userFromCreateMono = UserMapper.commandToUser(createUserCommand);
        Mono<User> userFromUpdateMono = UserMapper.commandToUser(updateUserCommand);

        StepVerifier.create(userFromCreateMono)
                .assertNext(userFromCreate -> {
                    assertThat(userFromCreate.name()).isNotNull();
                    assertThat(userFromCreate.lastName()).isNotNull();
                    assertThat(userFromCreate.phoneNumber()).isNotNull();
                    assertThat(userFromCreate.email()).isNotNull();
                    assertThat(userFromCreate.baseSalary()).isNotNull();
                })
                .verifyComplete();

        StepVerifier.create(userFromUpdateMono)
                .assertNext(userFromUpdate -> {
                    assertThat(userFromUpdate.id()).isNotNull();
                    assertThat(userFromUpdate.name()).isNotNull();
                    assertThat(userFromUpdate.lastName()).isNotNull();
                    assertThat(userFromUpdate.phoneNumber()).isNotNull();
                    assertThat(userFromUpdate.email()).isNotNull();
                    assertThat(userFromUpdate.baseSalary()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void shouldMapErrorWhenCreateUserCommandHasInvalidEmail() {
        CreateUserCommand invalidEmailCommand = new CreateUserCommand(
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                "invalid-email",
                USER_BASE_SALARY,
                USER_ID_NUMBER
        );

        Mono<User> result = UserMapper.commandToUser(invalidEmailCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("Email does not have a valid format"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenUpdateUserCommandHasInvalidEmail() {
        UpdateUserCommand invalidEmailCommand = new UpdateUserCommand(
                USER_ID,
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                "invalid-email",
                USER_BASE_SALARY,
                USER_ID_NUMBER
        );

        Mono<User> result = UserMapper.commandToUser(invalidEmailCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("Email does not have a valid format"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenCreateUserCommandHasInvalidPhoneNumber() {
        CreateUserCommand invalidPhoneCommand = new CreateUserCommand(
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                "123abc456",
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                USER_ID_NUMBER
        );

        Mono<User> result = UserMapper.commandToUser(invalidPhoneCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("Phone number must contain only numbers"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenUpdateUserCommandHasInvalidPhoneNumber() {
        UpdateUserCommand invalidPhoneCommand = new UpdateUserCommand(
                USER_ID,
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                "123abc456",
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                USER_ID_NUMBER
        );

        Mono<User> result = UserMapper.commandToUser(invalidPhoneCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("Phone number must contain only numbers"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenCreateUserCommandHasNullName() {
        CreateUserCommand nullNameCommand = new CreateUserCommand(
                null,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                USER_ID_NUMBER
        );

        Mono<User> result = UserMapper.commandToUser(nullNameCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("Name cannot be null or empty"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenUpdateUserCommandHasNullName() {
        UpdateUserCommand nullNameCommand = new UpdateUserCommand(
                USER_ID,
                null,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                USER_ID_NUMBER
        );

        Mono<User> result = UserMapper.commandToUser(nullNameCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("Name cannot be null or empty"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenCreateUserCommandHasInvalidAge() {
        CreateUserCommand underAgeCommand = new CreateUserCommand(
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                LocalDate.now().minusYears(17),
                USER_EMAIL,
                USER_BASE_SALARY,
                USER_ID_NUMBER
        );

        Mono<User> result = UserMapper.commandToUser(underAgeCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("User must be at least 18 years old"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenUpdateUserCommandHasInvalidAge() {
        UpdateUserCommand underAgeCommand = new UpdateUserCommand(
                USER_ID,
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                LocalDate.now().minusYears(17),
                USER_EMAIL,
                USER_BASE_SALARY,
                USER_ID_NUMBER
        );

        Mono<User> result = UserMapper.commandToUser(underAgeCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("User must be at least 18 years old"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenCreateUserCommandHasNegativeSalary() {
        CreateUserCommand negativeSalaryCommand = new CreateUserCommand(
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                new BigDecimal("-1000.00"),
                USER_ID_NUMBER
        );

        Mono<User> result = UserMapper.commandToUser(negativeSalaryCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("Base salary cannot be negative"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenUpdateUserCommandHasNegativeSalary() {
        UpdateUserCommand negativeSalaryCommand = new UpdateUserCommand(
                USER_ID,
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                new BigDecimal("-1000.00"),
                USER_ID_NUMBER
        );

        Mono<User> result = UserMapper.commandToUser(negativeSalaryCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("Base salary cannot be negative"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenCreateUserCommandHasNullIdNumber() {
        CreateUserCommand nullIdNumberCommand = new CreateUserCommand(
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                null
        );

        Mono<User> result = UserMapper.commandToUser(nullIdNumberCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("ID number cannot be null or empty"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenUpdateUserCommandHasNullIdNumber() {
        UpdateUserCommand nullIdNumberCommand = new UpdateUserCommand(
                USER_ID,
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                null
        );

        Mono<User> result = UserMapper.commandToUser(nullIdNumberCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("ID number cannot be null or empty"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenCreateUserCommandHasEmptyIdNumber() {
        CreateUserCommand emptyIdNumberCommand = new CreateUserCommand(
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                ""
        );

        Mono<User> result = UserMapper.commandToUser(emptyIdNumberCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("ID number cannot be null or empty"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenUpdateUserCommandHasEmptyIdNumber() {
        UpdateUserCommand emptyIdNumberCommand = new UpdateUserCommand(
                USER_ID,
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                ""
        );

        Mono<User> result = UserMapper.commandToUser(emptyIdNumberCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("ID number cannot be null or empty"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenCreateUserCommandHasBlankIdNumber() {
        CreateUserCommand blankIdNumberCommand = new CreateUserCommand(
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                "   "
        );

        Mono<User> result = UserMapper.commandToUser(blankIdNumberCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("ID number cannot be null or empty"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenUpdateUserCommandHasBlankIdNumber() {
        UpdateUserCommand blankIdNumberCommand = new UpdateUserCommand(
                USER_ID,
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                "   "
        );

        Mono<User> result = UserMapper.commandToUser(blankIdNumberCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("ID number cannot be null or empty"))
                .verify();
    }
}