package co.com.powerup.ags.authentication.usecase.user;

import co.com.powerup.ags.authentication.model.common.exception.DataAlreadyExistsException;
import co.com.powerup.ags.authentication.model.common.exception.EntityNotFoundException;
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
        // Given
        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(validUser));

        // When
        Mono<UserResponse> result = userUseCase.createUser(validCreateUserCommand);

        // Then
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

        // Verify interactions
        verify(userRepository).existsByEmail(USER_EMAIL);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowDataAlreadyExistsExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(Mono.just(true));

        // When
        Mono<UserResponse> result = userUseCase.createUser(validCreateUserCommand);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof DataAlreadyExistsException &&
                        throwable.getMessage().equals("User already exists with email: " + USER_EMAIL))
                .verify();

        // Verify interactions
        verify(userRepository).existsByEmail(USER_EMAIL);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldGenerateUuidForNewUser() {
        // Given
        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertThat(savedUser.id()).isNotNull();
            assertThat(UUID.fromString(savedUser.id())).isNotNull(); // Validates UUID format
            return Mono.just(savedUser);
        });

        // When
        Mono<UserResponse> result = userUseCase.createUser(validCreateUserCommand);

        // Then
        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse.id()).isNotNull();
                    assertThat(UUID.fromString(userResponse.id())).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleRepositoryErrorDuringExistenceCheck() {
        // Given
        when(userRepository.existsByEmail(USER_EMAIL))
                .thenReturn(Mono.error(new RuntimeException("Unexpected database error")));

        // When
        Mono<UserResponse> result = userUseCase.createUser(validCreateUserCommand);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Unexpected database error"))
                .verify();
    }

    @Test
    void shouldHandleRepositoryErrorDuringSave() {
        // Given
        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class)))
                .thenReturn(Mono.error(new RuntimeException("Save failed")));

        // When
        Mono<UserResponse> result = userUseCase.createUser(validCreateUserCommand);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Save failed"))
                .verify();
    }

    @Test
    void shouldUpdateUserSuccessfullyWhenUserExists() {
        // Given
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(validUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(validUser));

        // When
        Mono<UserResponse> result = userUseCase.updateUser(validUpdateUserCommand);

        // Then
        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse).isNotNull();
                    assertThat(userResponse.id()).isEqualTo(USER_ID);
                    assertThat(userResponse.name()).isEqualTo(USER_NAME);
                    assertThat(userResponse.email()).isEqualTo(USER_EMAIL);
                })
                .verifyComplete();

        // Verify interactions
        verify(userRepository).findById(USER_ID);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUserDoesNotExistForUpdate() {
        // Given
        when(userRepository.findById(USER_ID)).thenReturn(Mono.empty());

        // When
        Mono<UserResponse> result = userUseCase.updateUser(validUpdateUserCommand);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                        throwable.getMessage().equals("User not found with ID: " + USER_ID))
                .verify();

        // Verify interactions
        verify(userRepository).findById(USER_ID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldHandleRepositoryErrorDuringFindForUpdate() {
        // Given
        when(userRepository.findById(USER_ID))
                .thenReturn(Mono.error(new RuntimeException("Find failed")));

        // When
        Mono<UserResponse> result = userUseCase.updateUser(validUpdateUserCommand);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Find failed"))
                .verify();
    }

    @Test
    void shouldReturnUserSuccessfullyWhenUserExists() {
        // Given
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(validUser));

        // When
        Mono<UserResponse> result = userUseCase.getUserById(USER_ID);

        // Then
        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse).isNotNull();
                    assertThat(userResponse.id()).isEqualTo(USER_ID);
                    assertThat(userResponse.name()).isEqualTo(USER_NAME);
                    assertThat(userResponse.email()).isEqualTo(USER_EMAIL);
                })
                .verifyComplete();

        // Verify interactions
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGetUserByIdAndUserDoesNotExist() {
        // Given
        when(userRepository.findById(USER_ID)).thenReturn(Mono.empty());

        // When
        Mono<UserResponse> result = userUseCase.getUserById(USER_ID);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                        throwable.getMessage().equals("User not found with ID: " + USER_ID))
                .verify();

        // Verify interactions
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenIdIsNull() {
        // When
        Mono<UserResponse> result = userUseCase.getUserById(null);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("User ID cannot be null"))
                .verify();

        // Verify interactions
        verify(userRepository, never()).findById(any());
    }

    @Test
    void shouldHandleRepositoryErrorDuringGetUserById() {
        // Given
        when(userRepository.findById(USER_ID))
                .thenReturn(Mono.error(new RuntimeException("Database connection failed")));

        // When
        Mono<UserResponse> result = userUseCase.getUserById(USER_ID);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database connection failed"))
                .verify();
    }

    @Test
    void shouldDeleteUserSuccessfullyWhenUserExists() {
        // Given
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(validUser));
        when(userRepository.deleteById(USER_ID)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = userUseCase.deleteUser(USER_ID);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        // Verify interactions
        verify(userRepository).findById(USER_ID);
        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenDeleteUserAndUserDoesNotExist() {
        // Given
        when(userRepository.findById(USER_ID)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = userUseCase.deleteUser(USER_ID);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                        throwable.getMessage().equals("User not found with ID: " + USER_ID))
                .verify();

        // Verify interactions
        verify(userRepository).findById(USER_ID);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void shouldHandleRepositoryErrorDuringDelete() {
        // Given
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(validUser));
        when(userRepository.deleteById(USER_ID))
                .thenReturn(Mono.error(new RuntimeException("Delete failed")));

        // When
        Mono<Void> result = userUseCase.deleteUser(USER_ID);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Delete failed"))
                .verify();
    }

    @Test
    void shouldReturnAllUsersSuccessfully() {
        // Given
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

        // When
        Flux<UserResponse> result = userUseCase.getAllUsers();

        // Then
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

        // Verify interactions
        verify(userRepository).findAll();
    }

    @Test
    void shouldReturnEmptyFluxWhenNoUsersExist() {
        // Given
        when(userRepository.findAll()).thenReturn(Flux.empty());

        // When
        Flux<UserResponse> result = userUseCase.getAllUsers();

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        // Verify interactions
        verify(userRepository).findAll();
    }

    @Test
    void shouldHandleRepositoryErrorDuringFindAll() {
        // Given
        when(userRepository.findAll())
                .thenReturn(Flux.error(new RuntimeException("Database unavailable")));

        // When
        Flux<UserResponse> result = userUseCase.getAllUsers();

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database unavailable"))
                .verify();
    }

    @Test
    void shouldReturnUserSuccessfullyWhenEmailExists() {
        // Given
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Mono.just(validUser));

        // When
        Mono<UserResponse> result = userUseCase.getUserByEmail(USER_EMAIL);

        // Then
        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse).isNotNull();
                    assertThat(userResponse.id()).isEqualTo(USER_ID);
                    assertThat(userResponse.email()).isEqualTo(USER_EMAIL);
                })
                .verifyComplete();

        // Verify interactions
        verify(userRepository).findByEmail(USER_EMAIL);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenEmailDoesNotExist() {
        // Given
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Mono.empty());

        // When
        Mono<UserResponse> result = userUseCase.getUserByEmail(USER_EMAIL);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                        throwable.getMessage().equals("User not found with email: " + USER_EMAIL))
                .verify();

        // Verify interactions
        verify(userRepository).findByEmail(USER_EMAIL);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenEmailIsNull() {
        // When
        Mono<UserResponse> result = userUseCase.getUserByEmail(null);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Email cannot be null or empty"))
                .verify();

        // Verify interactions
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenEmailIsEmpty() {
        // When
        Mono<UserResponse> result = userUseCase.getUserByEmail("");

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Email cannot be null or empty"))
                .verify();

        // Verify interactions
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenEmailIsWhitespace() {
        // When
        Mono<UserResponse> result = userUseCase.getUserByEmail("   ");

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Email cannot be null or empty"))
                .verify();

        // Verify interactions
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void shouldHandleRepositoryErrorDuringFindByEmail() {
        // Given
        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Mono.error(new RuntimeException("Query failed")));

        // When
        Mono<UserResponse> result = userUseCase.getUserByEmail(USER_EMAIL);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Query failed"))
                .verify();
    }
}