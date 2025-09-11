package co.com.powerup.ags.authentication.r2dbc;

import co.com.powerup.ags.authentication.model.role.Role;
import co.com.powerup.ags.authentication.r2dbc.entity.RoleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleReactiveRepositoryAdapterTest {
    
    private static final Integer ROLE_ID = 1;
    private static final String ROLE_NAME = "ADMIN";
    private static final String ROLE_DESCRIPTION = "Administrator with full system access.";

    @InjectMocks
    RoleReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    RoleReactiveRepository repository;

    @Mock
    ObjectMapper objectMapper;
    
    Role validRole;
    RoleEntity validRoleEntity;
    
    @BeforeEach
    void setUp() {
        validRole = new Role(
                ROLE_ID,
                ROLE_NAME,
                ROLE_DESCRIPTION
        );
        
        validRoleEntity = RoleEntity.builder()
                .id(ROLE_ID)
                .name(ROLE_NAME)
                .description(ROLE_DESCRIPTION)
                .build();
    }

    @Test
    void mustFindValueById() {
        when(repository.findById(ROLE_ID)).thenReturn(Mono.just(validRoleEntity));
        when(objectMapper.map(validRoleEntity, Role.class))
                .thenReturn(validRole);
        
        Mono<Role> result = repositoryAdapter.getRoleById(ROLE_ID);
        
        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(ROLE_ID))
                .verifyComplete();
        
        verify(repository).findById(ROLE_ID);
    }
    
    @Test
    void mustReturnEmptyWhenRoleNotFound() {
        when(repository.findById(ROLE_ID)).thenReturn(Mono.empty());

        Mono<Role> result = repositoryAdapter.getRoleById(ROLE_ID);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        verify(repository).findById(ROLE_ID);
    }
}
