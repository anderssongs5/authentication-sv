package co.com.powerup.ags.authentication.r2dbc;

import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.gateways.UserRepository;
import co.com.powerup.ags.authentication.r2dbc.entity.UserEntity;
import co.com.powerup.ags.authentication.r2dbc.helper.ReactiveAdapterOperations;
import co.com.powerup.ags.authentication.r2dbc.mapper.UserEntityMapper;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, String, UserReactiveRepository>
        implements UserRepository {
    
    private static final Logger log = LoggerFactory.getLogger(UserReactiveRepositoryAdapter.class);
    private final TransactionalOperator transactionalOperator;

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper,
                                         TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.transactionalOperator = transactionalOperator;
    }
    
    @Override
    public Mono<User> save(User user) {
        return Mono.justOrEmpty(user)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User cannot be null")))
                .doOnNext(u -> log.debug("Attempting to save user with ID: {}", u.id()))
                .flatMap(u -> super.repository.existsById(u.id())
                        .flatMap(exists -> {
                            UserEntity toSave = Boolean.TRUE.equals(exists) ? 
                                    UserEntityMapper.INSTANCE.toExistingEntity(u) :
                                    UserEntityMapper.INSTANCE.toEntity(u);
                            return super.repository.save(toSave).as(transactionalOperator::transactional);
                        }))
                .map(UserEntityMapper.INSTANCE::toDomain)
                .doOnNext(savedUser -> log.debug("Successfully saved user with ID: {}", savedUser.id()))
                .onErrorMap(DataAccessException.class, 
                    ex -> new RuntimeException("Failed to save user: " + ex.getMessage(), ex));
    }
    
    @Override
    public Flux<User> findAll() {
        return super.repository.findAll()
                .doOnSubscribe(subscription -> log.debug("Retrieving all users from database"))
                .map(UserEntityMapper.INSTANCE::toDomain)
                .doOnNext(user -> log.trace("Retrieved user: {}", user.id()))
                .onErrorMap(DataAccessException.class,
                    ex -> new RuntimeException("Failed to retrieve all users: " + ex.getMessage(), ex));
    }
    
    @Override
    public Mono<User> findById(String id) {
        return Mono.justOrEmpty(id)
                .filter(userId -> !userId.trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User ID cannot be null or empty")))
                .doOnNext(userId -> log.debug("Finding user by ID: {}", userId))
                .flatMap(super.repository::findById)
                .map(UserEntityMapper.INSTANCE::toDomain)
                .doOnNext(user -> log.debug("Found user: {}", user.id()))
                .onErrorMap(DataAccessException.class,
                    ex -> new RuntimeException("Failed to find user by ID: " + ex.getMessage(), ex));
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.justOrEmpty(id)
                .filter(userId -> !userId.trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User ID cannot be null or empty")))
                .doOnNext(userId -> log.debug("Deleting user with ID: {}", userId))
                .flatMap(repository::deleteById)
                .doOnSuccess(unused -> log.debug("Successfully deleted user with ID: {}", id))
                .onErrorMap(DataAccessException.class,
                    ex -> new RuntimeException("Failed to delete user: " + ex.getMessage(), ex));
    }
    
    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return Mono.justOrEmpty(email)
                .filter(mail -> !mail.trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Email cannot be null or empty")))
                .doOnNext(mail -> log.debug("Checking if user exists by email: {}", mail))
                .flatMap(repository::existsByEmail)
                .doOnNext(exists -> log.debug("User exists by email {}: {}", email, exists))
                .onErrorMap(DataAccessException.class,
                    ex -> new RuntimeException("Failed to check user existence by email: " + ex.getMessage(), ex));
    }
    
    @Override
    public Mono<User> findByIdNumber(String idNumber) {
        return Mono.justOrEmpty(idNumber)
                .filter(userNId -> !userNId.trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User Id Number cannot be null or empty")))
                .doOnNext(userId -> log.debug("Finding user by Id Number: {}", userId))
                .flatMap(super.repository::findByIdNumber)
                .map(UserEntityMapper.INSTANCE::toDomain)
                .doOnNext(user -> log.debug("Found user by id number: {}", user.id()))
                .onErrorMap(DataAccessException.class,
                        ex -> new RuntimeException("Failed to find user by Id Number: " + ex.getMessage(), ex));
    }
    
    @Override
    public Mono<Boolean> existsByEmailOrIdNumber(String email, String idNumber) {
        return Mono.defer(() -> {
            boolean isEmailValid = email != null && !email.trim().isEmpty();
            boolean isIdValid = idNumber != null && !idNumber.trim().isEmpty();
            
            if (!isEmailValid && !isIdValid) {
                return Mono.error(new IllegalArgumentException("Either email or ID number must be provided"));
            }
            
            log.debug("Checking if user exists by email: {} or ID number: {}", email, idNumber);
            return repository.existsByEmailOrIdNumber(email, idNumber)
                    .doOnNext(exists -> log.debug("User exists by email {} or ID number {}: {}", email, idNumber, exists))
                    .onErrorMap(DataAccessException.class,
                            ex -> new RuntimeException("Failed to check user existence: " + ex.getMessage(), ex));
        });
    }
}
