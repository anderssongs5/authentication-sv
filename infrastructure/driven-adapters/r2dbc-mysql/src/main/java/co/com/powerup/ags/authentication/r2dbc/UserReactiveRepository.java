package co.com.powerup.ags.authentication.r2dbc;

import co.com.powerup.ags.authentication.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, String>, ReactiveQueryByExampleExecutor<UserEntity> {

    Mono<UserEntity> findByEmail(String email);
    
    Mono<Boolean> existsByEmail(String email);
    
    Mono<Boolean> existsById(String id);
    
    Mono<UserEntity> findByIdNumber(String idNumber);
    
    Mono<Boolean> existsByEmailOrIdNumber(String email, String idNumber);
}
