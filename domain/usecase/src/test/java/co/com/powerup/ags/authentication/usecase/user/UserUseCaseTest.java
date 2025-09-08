package co.com.powerup.ags.authentication.usecase.user;

import co.com.powerup.ags.authentication.model.common.exception.DataAlreadyExistsException;
import co.com.powerup.ags.authentication.model.common.exception.UserNotFoundException;
import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.gateways.PasswordEncoder;
import co.com.powerup.ags.authentication.model.user.gateways.UserRepository;
import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.Password;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserUseCase userUseCase;

    private static final String USER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String USER_NAME = "Steven";
    private static final String USER_LAST_NAME = "Garcia";
    private static final String USER_ADDRESS = "Carrera 60 # 53-14";
    private static final String USER_PHONE_NUMBER = "1234567890";
    private static final LocalDate USER_BIRTH_DATE = LocalDate.of(1990, 10, 1);
    private static final String USER_EMAIL = "steven.garcia@test.com";
    private static final BigDecimal USER_BASE_SALARY = new BigDecimal("50000.00");
    private static final String USER_ID_NUMBER = "123456";
    private static final String USER_PASSWORD = "ValidPass123";

    private CreateUserCommand validCreateUserCommand;
    private UpdateUserCommand validUpdateUserCommand;
    private User validUser;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCase(userRepository, passwordEncoder);
        
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword123");
        
        validCreateUserCommand = new CreateUserCommand(
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                USER_ID_NUMBER,
                USER_PASSWORD
        );

        validUpdateUserCommand = new UpdateUserCommand(
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

        validUser = new User(
                USER_ID,
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                new PhoneNumber(USER_PHONE_NUMBER),
                USER_BIRTH_DATE,
                new Email(USER_EMAIL),
                USER_BASE_SALARY,
                USER_ID_NUMBER,
                Password.fromPlainText(USER_PASSWORD, passwordEncoder)
        );
    }

    @Test
    void shouldCreateUserSuccessfullyWhenEmailAndIdNumberDoNotExist() {
        // Reset the mock to avoid interference from setup
        reset(passwordEncoder);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword123");
        
        when(userRepository.existsByEmailOrIdNumber(USER_EMAIL, USER_ID_NUMBER)).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(validUser));

        Mono<UserResponse> result = userUseCase.createUser(validCreateUserCommand);

        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse).isNotNull();
                    assertThat(userResponse.name()).isEqualTo(USER_NAME);
                    assertThat(userResponse.email()).isEqualTo(USER_EMAIL);
                    assertThat(userResponse.idNumber()).isEqualTo(USER_ID_NUMBER);
                })
                .verifyComplete();
                
        verify(passwordEncoder).encode(USER_PASSWORD);
        verify(userRepository).existsByEmailOrIdNumber(USER_EMAIL, USER_ID_NUMBER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithExistingEmailOrIdNumber() {
        when(userRepository.existsByEmailOrIdNumber(USER_EMAIL, USER_ID_NUMBER)).thenReturn(Mono.just(true));

        Mono<UserResponse> result = userUseCase.createUser(validCreateUserCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof DataAlreadyExistsException &&
                        throwable.getMessage().contains("A user already exists with the provided email"))
                .verify();
                
        verify(userRepository).existsByEmailOrIdNumber(USER_EMAIL, USER_ID_NUMBER);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(validUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(validUser));

        Mono<UserResponse> result = userUseCase.updateUser(validUpdateUserCommand);

        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse).isNotNull();
                    assertThat(userResponse.id()).isEqualTo(USER_ID);
                    assertThat(userResponse.name()).isEqualTo(USER_NAME);
                })
                .verifyComplete();
                
        verify(userRepository).findById(USER_ID);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.empty());

        Mono<UserResponse> result = userUseCase.updateUser(validUpdateUserCommand);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                        throwable.getMessage().contains("User not found with ID: " + USER_ID))
                .verify();
                
        verify(userRepository).findById(USER_ID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldGetAllUsersSuccessfully() {
        when(userRepository.findAll()).thenReturn(Flux.just(validUser));

        Flux<UserResponse> result = userUseCase.getAllUsers();

        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse).isNotNull();
                    assertThat(userResponse.name()).isEqualTo(USER_NAME);
                    assertThat(userResponse.email()).isEqualTo(USER_EMAIL);
                })
                .verifyComplete();
                
        verify(userRepository).findAll();
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(validUser));

        Mono<UserResponse> result = userUseCase.getUserById(USER_ID);

        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse).isNotNull();
                    assertThat(userResponse.id()).isEqualTo(USER_ID);
                    assertThat(userResponse.name()).isEqualTo(USER_NAME);
                })
                .verifyComplete();
                
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void shouldThrowExceptionWhenGettingUserByNonExistentId() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.empty());

        Mono<UserResponse> result = userUseCase.getUserById(USER_ID);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                        throwable.getMessage().contains("User not found with ID: " + USER_ID))
                .verify();
    }

    @Test
    void shouldThrowExceptionWhenGettingUserByNullId() {
        Mono<UserResponse> result = userUseCase.getUserById(null);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User ID cannot be null or empty"))
                .verify();
                
        verify(userRepository, never()).findById(anyString());
    }

    @Test
    void shouldThrowExceptionWhenGettingUserByEmptyId() {
        Mono<UserResponse> result = userUseCase.getUserById("   ");

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("User ID cannot be null or empty"))
                .verify();
                
        verify(userRepository, never()).findById(anyString());
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(validUser));
        when(userRepository.deleteById(USER_ID)).thenReturn(Mono.empty());

        Mono<Void> result = userUseCase.deleteUser(USER_ID);

        StepVerifier.create(result)
                .verifyComplete();
                
        verify(userRepository).findById(USER_ID);
        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.empty());

        Mono<Void> result = userUseCase.deleteUser(USER_ID);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                        throwable.getMessage().contains("User not found with ID: " + USER_ID))
                .verify();
                
        verify(userRepository).findById(USER_ID);
        verify(userRepository, never()).deleteById(anyString());
    }

    @Test
    void shouldGetUserByIdNumberSuccessfully() {
        when(userRepository.findByIdNumber(USER_ID_NUMBER)).thenReturn(Mono.just(validUser));

        Mono<UserResponse> result = userUseCase.getUserByIdNumber(USER_ID_NUMBER);

        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse).isNotNull();
                    assertThat(userResponse.idNumber()).isEqualTo(USER_ID_NUMBER);
                    assertThat(userResponse.name()).isEqualTo(USER_NAME);
                })
                .verifyComplete();
                
        verify(userRepository).findByIdNumber(USER_ID_NUMBER);
    }

    @Test
    void shouldThrowExceptionWhenGettingUserByNonExistentIdNumber() {
        when(userRepository.findByIdNumber(USER_ID_NUMBER)).thenReturn(Mono.empty());

        Mono<UserResponse> result = userUseCase.getUserByIdNumber(USER_ID_NUMBER);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                        throwable.getMessage().contains("User not found with id number: " + USER_ID_NUMBER))
                .verify();
    }

    @Test
    void shouldVerifyPasswordSuccessfully() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Mono.just(validUser));
        when(passwordEncoder.matches(USER_PASSWORD, "hashedPassword123")).thenReturn(true);

        Mono<Boolean> result = userUseCase.verifyPassword(USER_EMAIL, USER_PASSWORD);

        StepVerifier.create(result)
                .assertNext(isValid -> assertThat(isValid).isTrue())
                .verifyComplete();
                
        verify(userRepository).findByEmail(USER_EMAIL);
        verify(passwordEncoder).matches(USER_PASSWORD, "hashedPassword123");
    }

    @Test
    void shouldReturnFalseWhenPasswordDoesNotMatch() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Mono.just(validUser));
        when(passwordEncoder.matches("WrongPassword", "hashedPassword123")).thenReturn(false);

        Mono<Boolean> result = userUseCase.verifyPassword(USER_EMAIL, "WrongPassword");

        StepVerifier.create(result)
                .assertNext(isValid -> assertThat(isValid).isFalse())
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenVerifyingPasswordForNonExistentUser() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Mono.empty());

        Mono<Boolean> result = userUseCase.verifyPassword(USER_EMAIL, USER_PASSWORD);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                        throwable.getMessage().contains("User not found with email: " + USER_EMAIL))
                .verify();
    }

    @Test
    void shouldThrowExceptionWhenVerifyingPasswordWithNullEmail() {
        Mono<Boolean> result = userUseCase.verifyPassword(null, USER_PASSWORD);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Email cannot be null or empty"))
                .verify();
                
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Mono.just(validUser));
        when(passwordEncoder.matches(USER_PASSWORD, "hashedPassword123")).thenReturn(true);

        Mono<UserResponse> result = userUseCase.authenticateUser(USER_EMAIL, USER_PASSWORD);

        StepVerifier.create(result)
                .assertNext(userResponse -> {
                    assertThat(userResponse).isNotNull();
                    assertThat(userResponse.email()).isEqualTo(USER_EMAIL);
                    assertThat(userResponse.name()).isEqualTo(USER_NAME);
                })
                .verifyComplete();
                
        verify(userRepository, times(2)).findByEmail(USER_EMAIL); // Called twice: verify + authenticate
    }

    @Test
    void shouldThrowExceptionWhenAuthenticatingWithWrongPassword() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Mono.just(validUser));
        when(passwordEncoder.matches("WrongPassword", "hashedPassword123")).thenReturn(false);

        Mono<UserResponse> result = userUseCase.authenticateUser(USER_EMAIL, "WrongPassword");

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Invalid credentials"))
                .verify();
    }

    @Test
    void shouldThrowExceptionWhenAuthenticatingNonExistentUser() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Mono.empty());

        Mono<UserResponse> result = userUseCase.authenticateUser(USER_EMAIL, USER_PASSWORD);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                        throwable.getMessage().contains("User not found with email: " + USER_EMAIL))
                .verify();
    }
}