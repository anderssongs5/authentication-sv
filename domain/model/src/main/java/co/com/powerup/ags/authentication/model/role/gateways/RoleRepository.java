package co.com.powerup.ags.authentication.model.role.gateways;

import co.com.powerup.ags.authentication.model.role.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    
    Mono<Role> getRoleById(Integer id);
}
