package co.com.powerup.ags.authentication.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import co.com.powerup.ags.authentication.api.dto.CreateUserRequest;
import co.com.powerup.ags.authentication.api.dto.UpdateUserRequest;

@Configuration
public class RouterRest {
    
    public static final String ID_PATH_PARAM = "/{id}";
    
    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/users", 
            method = RequestMethod.GET,
            operation = @Operation(
                operationId = "getAllUsers",
                summary = "Get all users",
                tags = {"Users"},
                responses = {
                    @ApiResponse(
                        responseCode = "200", 
                        description = "Users retrieved successfully",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/users", 
            method = RequestMethod.POST,
            operation = @Operation(
                operationId = "createUser",
                summary = "Create a new user",
                tags = {"Users"},
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = CreateUserRequest.class))
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "201",
                        description = "User created successfully",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "400", 
                        description = "Invalid request",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "409", 
                        description = "User already exists",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "500", 
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/users/{id}", 
            method = RequestMethod.GET,
            operation = @Operation(
                operationId = "getUserById",
                summary = "Get user by ID",
                tags = {"Users"},
                parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "User ID")
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200", 
                        description = "User retrieved successfully",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "400", 
                        description = "Invalid ID",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "404", 
                        description = "User not found",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "500", 
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/users/{id}", 
            method = RequestMethod.PUT,
            operation = @Operation(
                operationId = "updateUser",
                summary = "Update user",
                tags = {"Users"},
                parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "User ID")
                },
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = UpdateUserRequest.class))
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "200", 
                        description = "User updated successfully",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "400", 
                        description = "Invalid request",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "404", 
                        description = "User not found",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "500", 
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/users/{id}", 
            method = RequestMethod.DELETE,
            operation = @Operation(
                operationId = "deleteUser",
                summary = "Delete user",
                tags = {"Users"},
                parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "User ID")
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200", 
                        description = "User deleted successfully",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "400", 
                        description = "Invalid ID",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "404", 
                        description = "User not found",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "500", 
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/users/search", 
            method = RequestMethod.GET,
            operation = @Operation(
                operationId = "getUserByEmail",
                summary = "Search user by email",
                tags = {"Users"},
                parameters = {
                    @Parameter(name = "email", in = ParameterIn.QUERY, required = true, description = "User email")
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200", 
                        description = "User retrieved successfully",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "400", 
                        description = "Invalid email",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "404", 
                        description = "User not found",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "500", 
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = co.com.powerup.ags.authentication.api.dto.ApiResponse.class))
                    )
                }
            )
        )
    })
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
