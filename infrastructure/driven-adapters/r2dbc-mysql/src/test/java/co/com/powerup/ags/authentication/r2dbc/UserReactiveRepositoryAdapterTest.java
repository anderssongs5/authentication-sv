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
                USER_BASE_SALARY
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
}
