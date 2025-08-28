package co.com.powerup.ags.authentication.model.user.gateways;

import co.com.powerup.ags.authentication.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    
    Mono<User> save(User user);
    
    Flux<User> findAll();
    
    Mono<User> findById(String id);
    
    Mono<User> findByEmail(String email);
    
    Mono<Boolean> existsByEmail(String email);
    
    Mono<Void> deleteById(String id);
    
    Mono<User> findByIdNumber(String idNumber);
    
    Mono<Boolean> existsByEmailOrIdNumber(String email, String idNumber);
}
