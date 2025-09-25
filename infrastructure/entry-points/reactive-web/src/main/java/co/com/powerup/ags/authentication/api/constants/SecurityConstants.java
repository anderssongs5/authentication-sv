package co.com.powerup.ags.authentication.api.constants;

import java.util.Set;
import java.util.stream.Collectors;

public class SecurityConstants {
    
    private SecurityConstants() {
        super();
    }

    private static final Set<String> EXCLUDED_URLS = Set.of(
            "/v3/api-docs.yaml",
            "/v3/api-docs/**",
            "/webjars/swagger-ui/**",
            "/api/v1/login",
            "/api/v1/introspect",
            "/api/v1/users/search",
            "/actuator/**",
            "/prometheus/**");

    public static final Set<String> EXCLUDED_PATTERNS = EXCLUDED_URLS
            .stream().map(url -> "/auth" + url).collect(Collectors.toSet());
    
    public static final String TOKEN = "token";
    
    public static final String BEARER_PREFIX = "Bearer ";
    
    public static final String ROLE_PREFIX = "ROLE_";
    
    public static final String SUBJECT_CLAIM = "sub";
    
    public static final String ROLE_CLAIM = "role";
}
