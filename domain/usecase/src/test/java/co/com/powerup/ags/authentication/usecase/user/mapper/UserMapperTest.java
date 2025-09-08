package co.com.powerup.ags.authentication.usecase.user.mapper;

import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.gateways.PasswordEncoder;
import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.Password;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;
import co.com.powerup.ags.authentication.usecase.user.dto.CreateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UpdateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private static final String USER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String USER_NAME = "Steven";
    private static final String USER_LAST_NAME = "Garcia";
    private static final String USER_ADDRESS = "Carrera 60 # 53-14";
    private static final String USER_PHONE_NUMBER = "1234567890";
    private static final LocalDate USER_BIRTH_DATE = LocalDate.of(1990, 10, 1);
    private static final String USER_EMAIL = "steven.garcia@test.com";
    private static final BigDecimal USER_BASE_SALARY = new BigDecimal("50000.00");
    private static final String USER_ID_NUMBER = "123456789";
    private static final String USER_PASSWORD = "ValidPass123";
    private static final Integer USER_ROLE_ID = 1;

    private CreateUserCommand createUserCommand;
    private UpdateUserCommand updateUserCommand;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        
        createUserCommand = new CreateUserCommand(
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                USER_ID_NUMBER,
                USER_PASSWORD,
                USER_ROLE_ID
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
                USER_ID_NUMBER,
                Password.fromPlainText(USER_PASSWORD, passwordEncoder),
                USER_ROLE_ID
        );
    }

    @Test
    void shouldMapCreateUserCommandToUser() {
        Mono<User> result = UserMapper.commandToUser(createUserCommand, passwordEncoder);

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
                    assertThat(user.password()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void shouldMapUpdateUserCommandToUser() {
        Mono<User> result = UserMapper.commandToUser(updateUserCommand, user);

        StepVerifier.create(result)
                .assertNext(mappedUser -> {
                    assertThat(mappedUser).isNotNull();
                    assertThat(mappedUser.id()).isEqualTo(updateUserCommand.id());
                    assertThat(mappedUser.name()).isEqualTo(updateUserCommand.name());
                    assertThat(mappedUser.lastName()).isEqualTo(updateUserCommand.lastName());
                    assertThat(mappedUser.address()).isEqualTo(updateUserCommand.address());
                    assertThat(mappedUser.phoneNumber()).isNotNull();
                    assertThat(mappedUser.phoneNumber().value()).isEqualTo(updateUserCommand.phoneNumber());
                    assertThat(mappedUser.birthDate()).isEqualTo(updateUserCommand.birthDate());
                    assertThat(mappedUser.email()).isNotNull();
                    assertThat(mappedUser.email().value()).isEqualTo(updateUserCommand.email());
                    assertThat(mappedUser.baseSalary()).isEqualTo(updateUserCommand.baseSalary());
                    assertThat(mappedUser.idNumber()).isEqualTo(updateUserCommand.idNumber());
                    assertThat(mappedUser.password()).isEqualTo(user.password()); // Password preserved
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
    void shouldCreatePasswordValueObjectFromCreateCommand() {
        Mono<User> result = UserMapper.commandToUser(createUserCommand, passwordEncoder);

        StepVerifier.create(result)
                .assertNext(user -> {
                    assertThat(user.password()).isInstanceOf(Password.class);
                    assertThat(user.password().hashedPassword()).isEqualTo("hashedPassword");
                })
                .verifyComplete();
    }

    @Test
    void shouldPreservePasswordFromExistingUserInUpdateCommand() {
        Password originalPassword = user.password();
        
        Mono<User> result = UserMapper.commandToUser(updateUserCommand, user);

        StepVerifier.create(result)
                .assertNext(updatedUser -> {
                    assertThat(updatedUser.password()).isEqualTo(originalPassword);
                })
                .verifyComplete();
    }

    @Test
    void shouldCreatePhoneNumberValueObjectFromCreateCommand() {
        Mono<User> result = UserMapper.commandToUser(createUserCommand, passwordEncoder);

        StepVerifier.create(result)
                .assertNext(user -> {
                    assertThat(user.phoneNumber()).isInstanceOf(PhoneNumber.class);
                    assertThat(user.phoneNumber().value()).isEqualTo(USER_PHONE_NUMBER);
                })
                .verifyComplete();
    }

    @Test
    void shouldCreateEmailValueObjectFromCreateCommand() {
        Mono<User> result = UserMapper.commandToUser(createUserCommand, passwordEncoder);

        StepVerifier.create(result)
                .assertNext(user -> {
                    assertThat(user.email()).isInstanceOf(Email.class);
                    assertThat(user.email().value()).isEqualTo(USER_EMAIL);
                })
                .verifyComplete();
    }

    @Test
    void shouldCreatePhoneNumberValueObjectFromUpdateCommand() {
        Mono<User> result = UserMapper.commandToUser(updateUserCommand, user);

        StepVerifier.create(result)
                .assertNext(user -> {
                    assertThat(user.phoneNumber()).isInstanceOf(PhoneNumber.class);
                    assertThat(user.phoneNumber().value()).isEqualTo(USER_PHONE_NUMBER);
                })
                .verifyComplete();
    }

    @Test
    void shouldCreateEmailValueObjectFromUpdateCommand() {
        Mono<User> result = UserMapper.commandToUser(updateUserCommand, user);

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
        Mono<User> result = UserMapper.commandToUser(createUserCommand, passwordEncoder);

        StepVerifier.create(result)
                .assertNext(user -> assertThat(user.id()).isNull())
                .verifyComplete();
    }

    @Test
    void shouldPreserveIdForUpdateUserCommand() {
        Mono<User> result = UserMapper.commandToUser(updateUserCommand, user);

        StepVerifier.create(result)
                .assertNext(user -> assertThat(user.id()).isEqualTo(USER_ID))
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
                USER_ID_NUMBER,
                USER_PASSWORD,
                USER_ROLE_ID
        );

        Mono<User> result = UserMapper.commandToUser(invalidEmailCommand, passwordEncoder);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("Email does not have a valid format"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenCreateUserCommandHasInvalidPassword() {
        CreateUserCommand invalidPasswordCommand = new CreateUserCommand(
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                USER_ID_NUMBER,
                "weak",
                USER_ROLE_ID
        );

        Mono<User> result = UserMapper.commandToUser(invalidPasswordCommand, passwordEncoder);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("Password must be at least 8 characters long"))
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
                USER_ID_NUMBER,
                USER_PASSWORD,
                USER_ROLE_ID
        );

        Mono<User> result = UserMapper.commandToUser(invalidPhoneCommand, passwordEncoder);

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
                USER_ID_NUMBER,
                USER_PASSWORD,
                USER_ROLE_ID
        );

        Mono<User> result = UserMapper.commandToUser(nullNameCommand, passwordEncoder);

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
                USER_ID_NUMBER,
                USER_PASSWORD,
                USER_ROLE_ID
        );

        Mono<User> result = UserMapper.commandToUser(underAgeCommand, passwordEncoder);

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
                USER_ID_NUMBER,
                USER_PASSWORD,
                USER_ROLE_ID
        );

        Mono<User> result = UserMapper.commandToUser(negativeSalaryCommand, passwordEncoder);

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
                null,
                USER_PASSWORD,
                USER_ROLE_ID
        );

        Mono<User> result = UserMapper.commandToUser(nullIdNumberCommand, passwordEncoder);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("ID number cannot be null or empty"))
                .verify();
    }

    @Test
    void shouldMapErrorWhenCreateUserCommandHasNullPassword() {
        CreateUserCommand nullPasswordCommand = new CreateUserCommand(
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                USER_ID_NUMBER,
                null,
                USER_ROLE_ID
        );

        Mono<User> result = UserMapper.commandToUser(nullPasswordCommand, passwordEncoder);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User validation failed") &&
                        throwable.getMessage().contains("Password cannot be null or empty"))
                .verify();
    }
}