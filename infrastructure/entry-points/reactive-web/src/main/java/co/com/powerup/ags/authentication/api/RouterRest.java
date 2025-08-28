package co.com.powerup.ags.authentication.api;

import co.com.powerup.ags.authentication.api.dto.ErrorResponse;
import co.com.powerup.ags.authentication.api.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
                    method = RequestMethod.POST,
                    beanClass = HandlerV1.class,
                    beanMethod = "createUser",
                    operation = @Operation(
                            operationId = "createUser",
                            summary = "Create a new user",
                            description = "Creates a new user with the provided information",
                            tags = {"Users"},
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    description = "User creation data",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = CreateUserRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "User created successfully",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = SuccessResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Success Response",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users",
                                                                        "requestId": "7bf1f546-2",
                                                                        "data": {
                                                                            "id": "123e4567-e89b-12d3-a456-426614174000",
                                                                            "name": "Steven",
                                                                            "lastName": "Garcia",
                                                                            "address": "Carrera 60 # 53-14",
                                                                            "phoneNumber": "1234567890",
                                                                            "birthDate": "1990-10-01",
                                                                            "email": "steven.garcia@test.com",
                                                                            "baseSalary": 50000.00
                                                                        }
                                                                    }
                                                                    """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid input data",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Validation Error",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users",
                                                                        "status": 400,
                                                                        "error": "Bad Request",
                                                                        "requestId": "7bf1f546-2",
                                                                        "code": "INVALID_INPUT",
                                                                        "message": "Name cannot be null or empty"
                                                                    }
                                                                    """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "User already exists",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Conflict Error",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users",
                                                                        "status": 409,
                                                                        "error": "Conflict",
                                                                        "requestId": "7bf1f546-2",
                                                                        "code": "DATA_ALREADY_EXISTS",
                                                                        "message": "User already exists with email: steven.garcia@test.com"
                                                                    }
                                                                    """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Internal Server Error",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users",
                                                                        "status": 500,
                                                                        "error": "Internal Server Error",
                                                                        "requestId": "7bf1f546-2",
                                                                        "code": "UNEXPECTED_ERROR",
                                                                        "message": "An unexpected error occurred"
                                                                    }
                                                                    """
                                                    )
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/users/{id}",
                    method = RequestMethod.GET,
                    beanClass = HandlerV1.class,
                    beanMethod = "getUserById",
                    operation = @Operation(
                            operationId = "getUserById",
                            summary = "Get user by ID",
                            description = "Retrieves a user by their unique identifier",
                            tags = {"Users"},
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            description = "User unique identifier",
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", example = "123e4567-e89b-12d3-a456-426614174000")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "User found successfully",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = SuccessResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Success Response",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users/123e4567-e89b-12d3-a456-426614174000",
                                                                        "requestId": "7bf1f546-2",
                                                                        "data": {
                                                                            "id": "123e4567-e89b-12d3-a456-426614174000",
                                                                            "name": "Steven",
                                                                            "lastName": "Garcia",
                                                                            "address": "Carrera 60 # 53-14",
                                                                            "phoneNumber": "1234567890",
                                                                            "birthDate": "1990-10-01",
                                                                            "email": "steven.garcia@test.com",
                                                                            "baseSalary": 50000.00
                                                                        }
                                                                    }
                                                                    """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "User not found",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Not Found Error",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users/123e4567-e89b-12d3-a456-426614174000",
                                                                        "status": 404,
                                                                        "error": "Not Found",
                                                                        "requestId": "7bf1f546-2",
                                                                        "code": "ENTITY_NOT_FOUND",
                                                                        "message": "User not found with ID: 123e4567-e89b-12d3-a456-426614174000"
                                                                    }
                                                                    """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Internal Server Error",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users/123e4567-e89b-12d3-a456-426614174000",
                                                                        "status": 500,
                                                                        "error": "Internal Server Error",
                                                                        "requestId": "7bf1f546-2",
                                                                        "code": "UNEXPECTED_ERROR",
                                                                        "message": "An unexpected error occurred"
                                                                    }
                                                                    """
                                                    )
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/users/{id}",
                    method = RequestMethod.PUT,
                    beanClass = HandlerV1.class,
                    beanMethod = "updateUser",
                    operation = @Operation(
                            operationId = "updateUser",
                            summary = "Update user",
                            description = "Updates an existing user with the provided information",
                            tags = {"Users"},
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            description = "User unique identifier",
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", example = "123e4567-e89b-12d3-a456-426614174000")
                                    )
                            },
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    description = "User update data",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = UpdateUserRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "User updated successfully",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = SuccessResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Success Response",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users/123e4567-e89b-12d3-a456-426614174000",
                                                                        "requestId": "7bf1f546-2",
                                                                        "data": {
                                                                            "id": "123e4567-e89b-12d3-a456-426614174000",
                                                                            "name": "Steven Updated",
                                                                            "lastName": "Garcia Updated",
                                                                            "address": "Carrera 60 # 53-14",
                                                                            "phoneNumber": "1234567890",
                                                                            "birthDate": "1990-10-01",
                                                                            "email": "steven.garcia@test.com",
                                                                            "baseSalary": 75000.00
                                                                        }
                                                                    }
                                                                    """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid input data",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Validation Error",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users/123e4567-e89b-12d3-a456-426614174000",
                                                                        "status": 400,
                                                                        "error": "Bad Request",
                                                                        "requestId": "7bf1f546-2",
                                                                        "code": "INVALID_INPUT",
                                                                        "message": "Base salary must be greater than 0 when provided"
                                                                    }
                                                                    """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "User not found",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Not Found Error",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users/123e4567-e89b-12d3-a456-426614174000",
                                                                        "status": 404,
                                                                        "error": "Not Found",
                                                                        "requestId": "7bf1f546-2",
                                                                        "code": "ENTITY_NOT_FOUND",
                                                                        "message": "User not found with ID: 123e4567-e89b-12d3-a456-426614174000"
                                                                    }
                                                                    """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Internal Server Error",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users/123e4567-e89b-12d3-a456-426614174000",
                                                                        "status": 500,
                                                                        "error": "Internal Server Error",
                                                                        "requestId": "7bf1f546-2",
                                                                        "code": "UNEXPECTED_ERROR",
                                                                        "message": "An unexpected error occurred"
                                                                    }
                                                                    """
                                                    )
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/users/{id}",
                    method = RequestMethod.DELETE,
                    beanClass = HandlerV1.class,
                    beanMethod = "deleteUser",
                    operation = @Operation(
                            operationId = "deleteUser",
                            summary = "Delete user",
                            description = "Deletes an existing user by their unique identifier",
                            tags = {"Users"},
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            description = "User unique identifier",
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", example = "123e4567-e89b-12d3-a456-426614174000")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "204",
                                            description = "User deleted successfully"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "User not found",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Not Found Error",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users/123e4567-e89b-12d3-a456-426614174000",
                                                                        "status": 404,
                                                                        "error": "Not Found",
                                                                        "requestId": "7bf1f546-2",
                                                                        "code": "ENTITY_NOT_FOUND",
                                                                        "message": "User not found with ID: 123e4567-e89b-12d3-a456-426614174000"
                                                                    }
                                                                    """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Internal Server Error",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users/123e4567-e89b-12d3-a456-426614174000",
                                                                        "status": 500,
                                                                        "error": "Internal Server Error",
                                                                        "requestId": "7bf1f546-2",
                                                                        "code": "UNEXPECTED_ERROR",
                                                                        "message": "An unexpected error occurred"
                                                                    }
                                                                    """
                                                    )
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/users",
                    method = RequestMethod.GET,
                    beanClass = HandlerV1.class,
                    beanMethod = "getAllUsers",
                    operation = @Operation(
                            operationId = "getAllUsers",
                            summary = "Get all users",
                            description = "Retrieves a list of all users",
                            tags = {"Users"},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Users retrieved successfully",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = SuccessResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Success Response",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users",
                                                                        "requestId": "7bf1f546-2",
                                                                        "data": [
                                                                            {
                                                                                "id": "123e4567-e89b-12d3-a456-426614174000",
                                                                                "name": "Steven",
                                                                                "lastName": "Garcia",
                                                                                "address": "Carrera 60 # 53-14",
                                                                                "phoneNumber": "1234567890",
                                                                                "birthDate": "1990-10-01",
                                                                                "email": "steven.garcia@test.com",
                                                                                "baseSalary": 50000.00
                                                                            },
                                                                            {
                                                                                "id": "456e7890-e89b-12d3-a456-426614174001",
                                                                                "name": "Jane",
                                                                                "lastName": "Smith",
                                                                                "address": "456 Oak St",
                                                                                "phoneNumber": "9876543210",
                                                                                "birthDate": "1985-05-20",
                                                                                "email": "jane.smith@test.com",
                                                                                "baseSalary": 75000.00
                                                                            }
                                                                        ]
                                                                    }
                                                                    """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Internal Server Error",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users",
                                                                        "status": 500,
                                                                        "error": "Internal Server Error",
                                                                        "requestId": "7bf1f546-2",
                                                                        "code": "UNEXPECTED_ERROR",
                                                                        "message": "An unexpected error occurred"
                                                                    }
                                                                    """
                                                    )
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/users/search",
                    method = RequestMethod.GET,
                    beanClass = HandlerV1.class,
                    beanMethod = "getUserByIdNumber",
                    operation = @Operation(
                            operationId = "getUserByIdNumber",
                            summary = "Search user by ID number",
                            description = "Retrieves a user by their ID number",
                            tags = {"Users"},
                            parameters = {
                                    @Parameter(
                                            name = "idNumber",
                                            description = "User ID number",
                                            required = true,
                                            in = ParameterIn.QUERY,
                                            schema = @Schema(type = "string", example = "123456789")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "User found successfully",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = SuccessResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Success Response",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users/search?idNumber=123456789",
                                                                        "requestId": "7bf1f546-2",
                                                                        "data": {
                                                                            "id": "123e4567-e89b-12d3-a456-426614174000",
                                                                            "name": "Steven",
                                                                            "lastName": "Garcia",
                                                                            "address": "Carrera 60 # 53-14",
                                                                            "phoneNumber": "1234567890",
                                                                            "birthDate": "1990-10-01",
                                                                            "email": "steven.garcia@test.com",
                                                                            "baseSalary": 50000.00,
                                                                            "idNumber": "123456789"
                                                                        }
                                                                    }
                                                                    """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "User not found",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Not Found Error",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users/search?idNumber=123456789",
                                                                        "status": 404,
                                                                        "error": "Not Found",
                                                                        "requestId": "7bf1f546-2",
                                                                        "code": "ENTITY_NOT_FOUND",
                                                                        "message": "User not found with id number: 123456789"
                                                                    }
                                                                    """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(
                                                            name = "Internal Server Error",
                                                            value = """
                                                                    {
                                                                        "timestamp": "2025-08-26T08:48:10.161513",
                                                                        "path": "/api/v1/users/search?idNumber=123456789",
                                                                        "status": 500,
                                                                        "error": "Internal Server Error",
                                                                        "requestId": "7bf1f546-2",
                                                                        "code": "UNEXPECTED_ERROR",
                                                                        "message": "An unexpected error occurred"
                                                                    }
                                                                    """
                                                    )
                                            )
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
                                .GET("/search", handlerV1::getUserByIdNumber)
                                .GET(ID_PATH_PARAM, handlerV1::getUserById)
                                .PUT(ID_PATH_PARAM, handlerV1::updateUser)
                                .DELETE(ID_PATH_PARAM, handlerV1::deleteUser))
                .build();
    }
}
