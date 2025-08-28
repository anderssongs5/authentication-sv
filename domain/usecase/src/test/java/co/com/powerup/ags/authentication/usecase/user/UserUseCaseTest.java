package co.com.powerup.ags.authentication.usecase.user;

import co.com.powerup.ags.authentication.model.common.exception.DataAlreadyExistsException;
import co.com.powerup.ags.authentication.model.common.exception.UserNotFoundException;
import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.gateways.UserRepository;
import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;
import co.com.powerup.ags.authentication.usecase.user.dto.CreateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UpdateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private IUserService userUseCase;

    private static final String USER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String USER_NAME = "Steven";
    private static final String USER_LAST_NAME = "Garcia";
    private static final String USER_ADDRESS = "Carrera 60 # 53-14";
    private static final String USER_PHONE_NUMBER = "1234567890";
    private static final LocalDate USER_BIRTH_DATE = LocalDate.of(1990, 10, 1);
    private static final String USER_EMAIL = "steven.garcia@test.com";
    private static final BigDecimal USER_BASE_SALARY = new BigDecimal("50000.00");

    private CreateUserCommand validCreateUserCommand;
    private UpdateUserCommand validUpdateUserCommand;
    private User validUser;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCase(userRepository);
        
        validCreateUserCommand = new CreateUserCommand(
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY
        );

        validUpdateUserCommand = new UpdateUserCommand(
                USER_ID,
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY
        );

        validUser = new User(
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
    void shouldCreateUserSuccessfullyWhenEmailDoesNotExist() {
        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(validUser));

        Mono<UserResponse> result = userUseCase.createUser(validCreateUserCommand);

        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse).isNotNull();
                    assertThat(userResponse.id()).isEqualTo(USER_ID);
                    assertThat(userResponse.name()).isEqualTo(USER_NAME);
                    assertThat(userResponse.lastName()).isEqualTo(USER_LAST_NAME);
                    assertThat(userResponse.address()).isEqualTo(USER_ADDRESS);
                    assertThat(userResponse.phoneNumber()).isEqualTo(USER_PHONE_NUMBER);
                    assertThat(userResponse.birthDate()).isEqualTo(USER_BIRTH_DATE);
                    assertThat(userResponse.email()).isEqualTo(USER_EMAIL);
                    assertThat(userResponse.baseSalary()).isEqualByComparingTo(USER_BASE_SALARY);
                })
                .verifyComplete();

        verify(userRepository).existsByEmail(USER_EMAIL);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowDataAlreadyExistsExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(Mono.just(true));

        Mono<UserResponse> result = userUseCase.createUser(validCreateUserCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof DataAlreadyExistsException &&
                        throwable.getMessage().equals("User already exists with email: " + USER_EMAIL))
                .verify();

        verify(userRepository).existsByEmail(USER_EMAIL);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldGenerateUuidForNewUser() {
        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertThat(savedUser.id()).isNotNull();
            assertThat(UUID.fromString(savedUser.id())).isNotNull();
            return Mono.just(savedUser);
        });

        Mono<UserResponse> result = userUseCase.createUser(validCreateUserCommand);

        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse.id()).isNotNull();
                    assertThat(UUID.fromString(userResponse.id())).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleRepositoryErrorDuringExistenceCheck() {
        when(userRepository.existsByEmail(USER_EMAIL))
                .thenReturn(Mono.error(new RuntimeException("Unexpected database error")));

        Mono<UserResponse> result = userUseCase.createUser(validCreateUserCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Unexpected database error"))
                .verify();
    }

    @Test
    void shouldHandleRepositoryErrorDuringSave() {
        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class)))
                .thenReturn(Mono.error(new RuntimeException("Save failed")));

        Mono<UserResponse> result = userUseCase.createUser(validCreateUserCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Save failed"))
                .verify();
    }

    @Test
    void shouldUpdateUserSuccessfullyWhenUserExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(validUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(validUser));

        Mono<UserResponse> result = userUseCase.updateUser(validUpdateUserCommand);

        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse).isNotNull();
                    assertThat(userResponse.id()).isEqualTo(USER_ID);
                    assertThat(userResponse.name()).isEqualTo(USER_NAME);
                    assertThat(userResponse.email()).isEqualTo(USER_EMAIL);
                })
                .verifyComplete();

        verify(userRepository).findById(USER_ID);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUserDoesNotExistForUpdate() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.empty());

        Mono<UserResponse> result = userUseCase.updateUser(validUpdateUserCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                        throwable.getMessage().equals("User not found with ID: " + USER_ID))
                .verify();

        verify(userRepository).findById(USER_ID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldHandleRepositoryErrorDuringFindForUpdate() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Mono.error(new RuntimeException("Find failed")));

        Mono<UserResponse> result = userUseCase.updateUser(validUpdateUserCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Find failed"))
                .verify();
    }

    @Test
    void shouldReturnUserSuccessfullyWhenUserExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(validUser));

        Mono<UserResponse> result = userUseCase.getUserById(USER_ID);

        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse).isNotNull();
                    assertThat(userResponse.id()).isEqualTo(USER_ID);
                    assertThat(userResponse.name()).isEqualTo(USER_NAME);
                    assertThat(userResponse.email()).isEqualTo(USER_EMAIL);
                })
                .verifyComplete();

        verify(userRepository).findById(USER_ID);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGetUserByIdAndUserDoesNotExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.empty());

        Mono<UserResponse> result = userUseCase.getUserById(USER_ID);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                        throwable.getMessage().equals("User not found with ID: " + USER_ID))
                .verify();

        verify(userRepository).findById(USER_ID);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenIdIsNull() {
        Mono<UserResponse> result = userUseCase.getUserById(null);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("User ID cannot be null"))
                .verify();

        verify(userRepository, never()).findById(any());
    }

    @Test
    void shouldHandleRepositoryErrorDuringGetUserById() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Mono.error(new RuntimeException("Database connection failed")));

        Mono<UserResponse> result = userUseCase.getUserById(USER_ID);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database connection failed"))
                .verify();
    }

    @Test
    void shouldDeleteUserSuccessfullyWhenUserExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(validUser));
        when(userRepository.deleteById(USER_ID)).thenReturn(Mono.empty());

        Mono<Void> result = userUseCase.deleteUser(USER_ID);

        StepVerifier.create(result)
                .verifyComplete();

        verify(userRepository).findById(USER_ID);
        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenDeleteUserAndUserDoesNotExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.empty());

        Mono<Void> result = userUseCase.deleteUser(USER_ID);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                        throwable.getMessage().equals("User not found with ID: " + USER_ID))
                .verify();

        verify(userRepository).findById(USER_ID);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void shouldHandleRepositoryErrorDuringDelete() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(validUser));
        when(userRepository.deleteById(USER_ID))
                .thenReturn(Mono.error(new RuntimeException("Delete failed")));

        Mono<Void> result = userUseCase.deleteUser(USER_ID);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Delete failed"))
                .verify();
    }

    @Test
    void shouldReturnAllUsersSuccessfully() {
        User user2 = new User(
                "different-id",
                "Jane",
                "Smith",
                "456 Oak St",
                new PhoneNumber("9876543210"),
                LocalDate.of(1985, 5, 20),
                new Email("jane.smith@example.com"),
                new BigDecimal("75000.00")
        );

        when(userRepository.findAll()).thenReturn(Flux.just(validUser, user2));

        Flux<UserResponse> result = userUseCase.getAllUsers();

        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse.id()).isEqualTo(USER_ID);
                    assertThat(userResponse.name()).isEqualTo(USER_NAME);
                })
                .assertNext(userResponse -> {
                    assertThat(userResponse.id()).isEqualTo("different-id");
                    assertThat(userResponse.name()).isEqualTo("Jane");
                })
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void shouldReturnEmptyFluxWhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(Flux.empty());

        Flux<UserResponse> result = userUseCase.getAllUsers();

        StepVerifier.create(result)
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void shouldHandleRepositoryErrorDuringFindAll() {
        when(userRepository.findAll())
                .thenReturn(Flux.error(new RuntimeException("Database unavailable")));

        Flux<UserResponse> result = userUseCase.getAllUsers();

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database unavailable"))
                .verify();
    }

    @Test
    void shouldReturnUserSuccessfullyWhenEmailExists() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Mono.just(validUser));

        Mono<UserResponse> result = userUseCase.getUserByEmail(USER_EMAIL);

        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse).isNotNull();
                    assertThat(userResponse.id()).isEqualTo(USER_ID);
                    assertThat(userResponse.email()).isEqualTo(USER_EMAIL);
                })
                .verifyComplete();

        verify(userRepository).findByEmail(USER_EMAIL);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenEmailDoesNotExist() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Mono.empty());

        Mono<UserResponse> result = userUseCase.getUserByEmail(USER_EMAIL);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                        throwable.getMessage().equals("User not found with email: " + USER_EMAIL))
                .verify();

        verify(userRepository).findByEmail(USER_EMAIL);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenEmailIsNull() {
        Mono<UserResponse> result = userUseCase.getUserByEmail(null);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Email cannot be null or empty"))
                .verify();

        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenEmailIsEmpty() {
        Mono<UserResponse> result = userUseCase.getUserByEmail("");

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Email cannot be null or empty"))
                .verify();

        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenEmailIsWhitespace() {
        Mono<UserResponse> result = userUseCase.getUserByEmail("   ");

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Email cannot be null or empty"))
                .verify();

        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void shouldHandleRepositoryErrorDuringFindByEmail() {
        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Mono.error(new RuntimeException("Query failed")));

        Mono<UserResponse> result = userUseCase.getUserByEmail(USER_EMAIL);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Query failed"))
                .verify();
    }
}