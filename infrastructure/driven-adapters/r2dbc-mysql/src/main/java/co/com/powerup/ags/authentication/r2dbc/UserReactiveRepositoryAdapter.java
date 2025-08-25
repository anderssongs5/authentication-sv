package co.com.powerup.ags.authentication.r2dbc;

import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.gateways.UserRepository;
import co.com.powerup.ags.authentication.r2dbc.entity.UserEntity;
import co.com.powerup.ags.authentication.r2dbc.helper.ReactiveAdapterOperations;
import co.com.powerup.ags.authentication.r2dbc.mapper.UserEntityMapper;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, String, UserReactiveRepository>
        implements UserRepository {

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }
    
    @Override
    public Mono<User> save(User user) {
        return super.repository.existsById(user.id())
                .flatMap(exists -> {
                    UserEntity toSave = Boolean.TRUE.equals(exists) ? UserEntityMapper.INSTANCE.toExistingEntity(user) :
                            UserEntityMapper.INSTANCE.toEntity(user);
                    return super.repository.save(toSave);
                })
                .map(UserEntityMapper.INSTANCE::toDomain);
    }
    
    @Override
    public Flux<User> findAll() {
        return super.repository.findAll().map(UserEntityMapper.INSTANCE::toDomain);
    }
    
    @Override
    public Mono<User> findById(String id) {
        return super.repository.findById(id).map(UserEntityMapper.INSTANCE::toDomain);
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
    
    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(this::toEntity);
    }
    
    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
