package co.com.powerup.ags.authentication.api.constants;

import java.util.Set;

public class SecurityConstants {
    
    private SecurityConstants() {
        super();
    }
    
    public static final Set<String> EXCLUDED_PATTERNS = Set.of(
            "/v3/api-docs.yaml",
            "/v3/api-docs/**",
            "/webjars/swagger-ui/**",
            "/api/v1/login",
            "/api/v1/introspect");
    
    public static final String TOKEN = "token";
    
    public static final String BEARER_PREFIX = "Bearer ";
    
    public static final String ROLE_PREFIX = "ROLE_";
    
    public static final String SUBJECT_CLAIM = "sub";
    
    public static final String ROLE_CLAIM = "role";
}
