package co.com.powerup.ags.authentication.r2dbc;

import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;
import co.com.powerup.ags.authentication.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {
    
    private static final String USER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String USER_NAME = "Steven";
    private static final String USER_LAST_NAME = "Garcia";
    private static final String USER_ADDRESS = "Carrera 60 # 53-14";
    private static final String USER_PHONE_NUMBER = "1234567890";
    private static final LocalDate USER_BIRTH_DATE = LocalDate.of(1990, 10, 1);
    private static final String USER_EMAIL = "steven.garcia@test.com";
    private static final BigDecimal USER_BASE_SALARY = new BigDecimal("50000.00");
    private static final String USER_ID_NUMBER = "123456789";

    @InjectMocks
    UserReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    UserReactiveRepository repository;

    @Mock
    ObjectMapper mapper;
    
    @Mock
    TransactionalOperator transactionalOperator;
    
    User validUser;
    UserEntity validUserEntity;
    
    @BeforeEach
    void setUp() {
        validUser = new User(
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
        
        validUserEntity = UserEntity.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .lastName(USER_LAST_NAME)
                .address(USER_ADDRESS)
                .phoneNumber(USER_PHONE_NUMBER)
                .birthDate(USER_BIRTH_DATE)
                .email(USER_EMAIL)
                .baseSalary(USER_BASE_SALARY)
                .idNumber(USER_ID_NUMBER)
                .build();
    }

    @Test
    void mustFindValueById() {
        when(repository.findById(USER_ID)).thenReturn(Mono.just(validUserEntity));
        
        Mono<User> result = repositoryAdapter.findById(USER_ID);
        
        StepVerifier.create(result)
                .expectNextMatches(value -> value.id().equals(USER_ID))
                .verifyComplete();
        
        verify(repository).findById(USER_ID);
    }
    
    @Test
    void mustReturnEmptyWhenUserNotFound() {
        when(repository.findById(USER_ID)).thenReturn(Mono.empty());

        Mono<User> result = repositoryAdapter.findById(USER_ID);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        verify(repository).findById(USER_ID);
    }
    
    @Test
    void mustFindAllValues() {
        when(repository.findAll()).thenReturn(Flux.just(validUserEntity));

        Flux<User> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.id().equals(USER_ID))
                .verifyComplete();
        
        verify(repository).findAll();
    }

    @Test
    void mustFindByEmail() {
        when(repository.findByEmail(anyString())).thenReturn(Mono.just(validUserEntity));

        Mono<User> result = repositoryAdapter.findByEmail(USER_EMAIL);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.email().value().equals(USER_EMAIL))
                .verifyComplete();
        
        verify(repository).findByEmail(USER_EMAIL);
    }
    
    @Test
    void mustReturnEmptyWhenEmailNotFound() {
        when(repository.findByEmail(anyString())).thenReturn(Mono.empty());
        
        Mono<User> result = repositoryAdapter.findByEmail(USER_EMAIL);
        
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        verify(repository).findByEmail(USER_EMAIL);
    }
    
    @Test
    void mustExistsByEmail() {
        when(repository.existsByEmail(USER_EMAIL)).thenReturn(Mono.just(Boolean.TRUE));
        
        Mono<Boolean> result = repositoryAdapter.existsByEmail(USER_EMAIL);
        
        StepVerifier.create(result)
                .expectNextMatches(value -> value == Boolean.TRUE)
                .verifyComplete();
        
        verify(repository).existsByEmail(USER_EMAIL);
    }
    
    @Test
    void mustReturnFalseWhenEmailNotFound() {
        when(repository.existsByEmail(USER_EMAIL)).thenReturn(Mono.just(Boolean.FALSE));
        
        Mono<Boolean> result = repositoryAdapter.existsByEmail(USER_EMAIL);
        
        StepVerifier.create(result)
                .expectNextMatches(value -> value == Boolean.FALSE)
                .verifyComplete();
        
        verify(repository).existsByEmail(USER_EMAIL);
    }

    @Test
    void mustSaveNewUser() {
        when(repository.existsById(USER_ID)).thenReturn(Mono.just(false));
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(validUserEntity));
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mono<User> result = repositoryAdapter.save(validUser);

        StepVerifier.create(result)
                .expectNextMatches(savedUser -> 
                    savedUser.id().equals(USER_ID) && 
                    savedUser.name().equals(USER_NAME) &&
                    savedUser.email().value().equals(USER_EMAIL)
                )
                .verifyComplete();
        
        verify(repository).existsById(USER_ID);
        verify(repository).save(any(UserEntity.class));
    }

    @Test
    void mustSaveExistingUser() {
        when(repository.existsById(USER_ID)).thenReturn(Mono.just(true));
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(validUserEntity));
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mono<User> result = repositoryAdapter.save(validUser);

        StepVerifier.create(result)
                .expectNextMatches(savedUser -> 
                    savedUser.id().equals(USER_ID) && 
                    savedUser.name().equals(USER_NAME) &&
                    savedUser.email().value().equals(USER_EMAIL)
                )
                .verifyComplete();
        
        verify(repository).existsById(USER_ID);
        verify(repository).save(any(UserEntity.class));
    }

    @Test
    void mustDeleteUserById() {
        when(repository.deleteById(USER_ID)).thenReturn(Mono.empty());

        Mono<Void> result = repositoryAdapter.deleteById(USER_ID);

        StepVerifier.create(result)
                .verifyComplete();

        verify(repository).deleteById(USER_ID);
    }

    @Test
    void mustThrowErrorWhenDeletingWithNullId() {
        Mono<Void> result = repositoryAdapter.deleteById(null);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never()).deleteById(any(String.class));
    }

    @Test
    void mustThrowErrorWhenDeletingWithEmptyId() {
        Mono<Void> result = repositoryAdapter.deleteById("");

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never()).deleteById(any(String.class));
    }

    @Test
    void mustHandleDataAccessExceptionOnDeleteById() {
        when(repository.deleteById(USER_ID)).thenReturn(Mono.error(new DataAccessResourceFailureException("Database connection failed")));

        Mono<Void> result = repositoryAdapter.deleteById(USER_ID);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).deleteById(USER_ID);
    }

    @Test
    void mustHandleDataAccessExceptionOnFindById() {
        when(repository.findById(USER_ID)).thenReturn(Mono.error(new DataAccessResourceFailureException("Database connection failed")));

        Mono<User> result = repositoryAdapter.findById(USER_ID);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).findById(USER_ID);
    }

    @Test
    void mustHandleDataAccessExceptionOnFindAll() {
        when(repository.findAll()).thenReturn(Flux.error(new DataAccessResourceFailureException("Database connection failed")));

        Flux<User> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).findAll();
    }

    @Test
    void mustHandleDataAccessExceptionOnFindByEmail() {
        when(repository.findByEmail(USER_EMAIL)).thenReturn(Mono.error(new DataAccessResourceFailureException("Database connection failed")));

        Mono<User> result = repositoryAdapter.findByEmail(USER_EMAIL);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).findByEmail(USER_EMAIL);
    }

    @Test
    void mustHandleDataAccessExceptionOnExistsByEmail() {
        when(repository.existsByEmail(USER_EMAIL)).thenReturn(Mono.error(new DataAccessResourceFailureException("Database connection failed")));

        Mono<Boolean> result = repositoryAdapter.existsByEmail(USER_EMAIL);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).existsByEmail(USER_EMAIL);
    }

    @Test
    void mustHandleDataAccessExceptionOnSave() {
        when(repository.existsById(USER_ID)).thenReturn(Mono.error(new DataAccessResourceFailureException("Database connection failed")));

        Mono<User> result = repositoryAdapter.save(validUser);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).existsById(USER_ID);
        verify(repository, never()).save(any(UserEntity.class));
    }

    @Test
    void mustHandleDataAccessExceptionOnSaveOperation() {
        when(repository.existsById(USER_ID)).thenReturn(Mono.just(false));
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.error(new DataAccessResourceFailureException("Database connection failed")));
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mono<User> result = repositoryAdapter.save(validUser);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).existsById(USER_ID);
        verify(repository).save(any(UserEntity.class));
    }

    @Test
    void mustThrowErrorWhenSavingNullUser() {
        Mono<User> result = repositoryAdapter.save(null);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never()).existsById(any(String.class));
        verify(repository, never()).save(any(UserEntity.class));
    }

    @Test
    void mustThrowErrorWhenFindingByNullId() {
        Mono<User> result = repositoryAdapter.findById(null);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never()).findById(any(String.class));
    }

    @Test
    void mustThrowErrorWhenFindingByEmptyId() {
        Mono<User> result = repositoryAdapter.findById("");

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never()).findById(any(String.class));
    }

    @Test
    void mustThrowErrorWhenFindingByNullEmail() {
        Mono<User> result = repositoryAdapter.findByEmail(null);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never()).findByEmail(any());
    }

    @Test
    void mustThrowErrorWhenFindingByEmptyEmail() {
        Mono<User> result = repositoryAdapter.findByEmail("");

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never()).findByEmail(any());
    }

    @Test
    void mustThrowErrorWhenCheckingExistenceByNullEmail() {
        Mono<Boolean> result = repositoryAdapter.existsByEmail(null);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never()).existsByEmail(any());
    }

    @Test
    void mustThrowErrorWhenCheckingExistenceByEmptyEmail() {
        Mono<Boolean> result = repositoryAdapter.existsByEmail("");

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never()).existsByEmail(any());
    }

    @Test
    void mustFindByIdNumber() {
        when(repository.findByIdNumber(USER_ID_NUMBER)).thenReturn(Mono.just(validUserEntity));

        Mono<User> result = repositoryAdapter.findByIdNumber(USER_ID_NUMBER);

        StepVerifier.create(result)
                .expectNextMatches(user -> user.idNumber().equals(USER_ID_NUMBER))
                .verifyComplete();

        verify(repository).findByIdNumber(USER_ID_NUMBER);
    }

    @Test
    void mustReturnEmptyWhenIdNumberNotFound() {
        when(repository.findByIdNumber(USER_ID_NUMBER)).thenReturn(Mono.empty());

        Mono<User> result = repositoryAdapter.findByIdNumber(USER_ID_NUMBER);

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(repository).findByIdNumber(USER_ID_NUMBER);
    }

    @Test
    void mustThrowErrorWhenFindingByNullIdNumber() {
        Mono<User> result = repositoryAdapter.findByIdNumber(null);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never()).findByIdNumber(any());
    }

    @Test
    void mustThrowErrorWhenFindingByEmptyIdNumber() {
        Mono<User> result = repositoryAdapter.findByIdNumber("");

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never()).findByIdNumber(any());
    }

    @Test
    void mustHandleDataAccessExceptionOnFindByIdNumber() {
        when(repository.findByIdNumber(USER_ID_NUMBER)).thenReturn(Mono.error(new DataAccessResourceFailureException("Database connection failed")));

        Mono<User> result = repositoryAdapter.findByIdNumber(USER_ID_NUMBER);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).findByIdNumber(USER_ID_NUMBER);
    }

    @Test
    void mustCheckExistsByEmailOrIdNumber() {
        when(repository.existsByEmailOrIdNumber(USER_EMAIL, USER_ID_NUMBER)).thenReturn(Mono.just(true));

        Mono<Boolean> result = repositoryAdapter.existsByEmailOrIdNumber(USER_EMAIL, USER_ID_NUMBER);

        StepVerifier.create(result)
                .expectNextMatches(exists -> exists)
                .verifyComplete();

        verify(repository).existsByEmailOrIdNumber(USER_EMAIL, USER_ID_NUMBER);
    }

    @Test
    void mustReturnFalseWhenNeitherEmailNorIdNumberExists() {
        when(repository.existsByEmailOrIdNumber(USER_EMAIL, USER_ID_NUMBER)).thenReturn(Mono.just(false));

        Mono<Boolean> result = repositoryAdapter.existsByEmailOrIdNumber(USER_EMAIL, USER_ID_NUMBER);

        StepVerifier.create(result)
                .expectNextMatches(exists -> !exists)
                .verifyComplete();

        verify(repository).existsByEmailOrIdNumber(USER_EMAIL, USER_ID_NUMBER);
    }

    @Test
    void mustCheckExistsByEmailOnly() {
        when(repository.existsByEmailOrIdNumber(USER_EMAIL, null)).thenReturn(Mono.just(true));

        Mono<Boolean> result = repositoryAdapter.existsByEmailOrIdNumber(USER_EMAIL, null);

        StepVerifier.create(result)
                .expectNextMatches(exists -> exists)
                .verifyComplete();

        verify(repository).existsByEmailOrIdNumber(USER_EMAIL, null);
    }

    @Test
    void mustCheckExistsByIdNumberOnly() {
        when(repository.existsByEmailOrIdNumber(null, USER_ID_NUMBER)).thenReturn(Mono.just(true));

        Mono<Boolean> result = repositoryAdapter.existsByEmailOrIdNumber(null, USER_ID_NUMBER);

        StepVerifier.create(result)
                .expectNextMatches(exists -> exists)
                .verifyComplete();

        verify(repository).existsByEmailOrIdNumber(null, USER_ID_NUMBER);
    }

    @Test
    void mustThrowErrorWhenBothEmailAndIdNumberAreNull() {
        Mono<Boolean> result = repositoryAdapter.existsByEmailOrIdNumber(null, null);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never()).existsByEmailOrIdNumber(any(), any());
    }

    @Test
    void mustThrowErrorWhenBothEmailAndIdNumberAreEmpty() {
        Mono<Boolean> result = repositoryAdapter.existsByEmailOrIdNumber("", "");

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never()).existsByEmailOrIdNumber(any(), any());
    }

    @Test
    void mustHandleDataAccessExceptionOnExistsByEmailOrIdNumber() {
        when(repository.existsByEmailOrIdNumber(USER_EMAIL, USER_ID_NUMBER))
                .thenReturn(Mono.error(new DataAccessResourceFailureException("Database connection failed")));

        Mono<Boolean> result = repositoryAdapter.existsByEmailOrIdNumber(USER_EMAIL, USER_ID_NUMBER);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).existsByEmailOrIdNumber(USER_EMAIL, USER_ID_NUMBER);
    }
}
