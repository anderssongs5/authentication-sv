package co.com.powerup.ags.authentication.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterRest {
    
    public static final String ID_PATH_PARAM = "/{id}";
    
    @Bean
    public RouterFunction<ServerResponse> routerFunction(HandlerV1 handlerV1) {
        return RouterFunctions
            .route()
            .path("/api/v1/users",
                    builder -> builder
                            .GET("", handlerV1::getAllUsers)
                            .POST("", handlerV1::createUser)
                            .GET(ID_PATH_PARAM, handlerV1::getUserById)
                            .PUT(ID_PATH_PARAM, handlerV1::updateUser)
                            .DELETE(ID_PATH_PARAM, handlerV1::deleteUser)
                            .GET("/search", handlerV1::getUserByEmail))
            .build();
    }
}
