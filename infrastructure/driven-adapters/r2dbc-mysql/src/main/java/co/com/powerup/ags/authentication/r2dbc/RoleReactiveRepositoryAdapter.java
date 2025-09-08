package co.com.powerup.ags.authentication.r2dbc;

import co.com.powerup.ags.authentication.model.role.Role;
import co.com.powerup.ags.authentication.model.role.gateways.RoleRepository;
import co.com.powerup.ags.authentication.r2dbc.entity.RoleEntity;
import co.com.powerup.ags.authentication.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RoleReactiveRepositoryAdapter extends ReactiveAdapterOperations<Role, RoleEntity, Integer, RoleReactiveRepository>
        implements RoleRepository {

    public RoleReactiveRepositoryAdapter(RoleReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Role.class));
    }
    
    @Override
    public Mono<Role> getRoleById(Integer id) {
        return super.findById(id);
    }
}
