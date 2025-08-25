package co.com.powerup.ags.authentication.api;

import co.com.powerup.ags.authentication.api.constants.HandlerMessages;
import co.com.powerup.ags.authentication.api.dto.ApiResponse;
import co.com.powerup.ags.authentication.api.dto.CreateUserRequest;
import co.com.powerup.ags.authentication.api.dto.UpdateUserRequest;
import co.com.powerup.ags.authentication.api.helper.UserHelper;
import co.com.powerup.ags.authentication.api.mapper.UserRequestMapper;
import co.com.powerup.ags.authentication.model.common.exception.DataAlreadyExistsException;
import co.com.powerup.ags.authentication.model.common.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@Component
public class HandlerV1 {
    
    private static final Logger log = LoggerFactory.getLogger(HandlerV1.class);
    public static final String REQUEST_ID = "requestId";
    public static final String OPERATION = "operation";
    
    private final UserRequestMapper requestMapper;
    private final UserHelper userHelper;
    
    public HandlerV1(UserHelper userHelper) {
        this.requestMapper = UserRequestMapper.INSTANCE;
        this.userHelper = userHelper;
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        String requestId = UUID.randomUUID().toString();
        String operation = "getAllUsers";
        
        log.info("Retrieving all users from database, requestId {}", requestId);
        return userHelper.getAllUsers()
                .map(requestMapper::toResponse)
                .collectList()
                .doOnNext(users -> log.info("Retrieved {} users successfully", users.size()))
                .flatMap(users -> ServerResponse.ok().bodyValue(
                        ApiResponse.success(HandlerMessages.USERS_RETRIEVED_SUCCESS, users)))
                .onErrorResume(error -> handleUnexpectedError(operation, requestId, error));
    }

    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        String requestId = UUID.randomUUID().toString();
        String operation = "getUserById";
        
        String id = serverRequest.pathVariable("id");
        log.info("Retrieving user with ID: {}, requestId {}", id, requestId);
        return userHelper.getUserById(id)
                .map(requestMapper::toResponse)
                .doOnNext(user -> log.info("User retrieved successfully: {}", user.id()))
                .flatMap(user -> ServerResponse.ok().bodyValue(
                        ApiResponse.success(HandlerMessages.USER_RETRIEVED_SUCCESS, user)))
                .onErrorResume(IllegalArgumentException.class, 
                    error -> handleValidationError(operation, requestId, error))
                .onErrorResume(EntityNotFoundException.class,
                    error -> handleEntityNotFoundError(operation, requestId, error))
                .onErrorResume(error -> handleUnexpectedError(operation, requestId, error));
    }

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        String requestId = UUID.randomUUID().toString();
        String operation = "createUser";
        
        return serverRequest.bodyToMono(CreateUserRequest.class)
                .map(requestMapper::toCommand)
                .doOnNext(command -> log.info("Creating new user with email: {}, requestId {}",
                        command.email(), requestId))
                .flatMap(userHelper::createUser)
                .map(requestMapper::toResponse)
                .doOnNext(user -> log.info("User created successfully with ID: {}", user.id()))
                .flatMap(user -> {
                    URI location = URI.create("/api/v1/users/" + user.id());
                    return ServerResponse.created(location).bodyValue(
                            ApiResponse.success(HandlerMessages.USER_CREATED_SUCCESS, user));
                })
                .onErrorResume(DataAlreadyExistsException.class,
                    error -> handleConflictError(operation, requestId, error))
                .onErrorResume(IllegalArgumentException.class, 
                    error -> handleValidationError(operation, requestId, error))
                .onErrorResume(error -> handleUnexpectedError(operation, requestId, error));
    }
    
    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
        String requestId = UUID.randomUUID().toString();
        String operation = "updateUser";
        
        String id = serverRequest.pathVariable("id");
        log.info("Updating user with ID: {}, requestId {}", id, requestId);
        return serverRequest.bodyToMono(UpdateUserRequest.class)
                .map(request -> requestMapper.toCommand(request, id))
                .flatMap(userHelper::updateUser)
                .map(requestMapper::toResponse)
                .doOnNext(user -> log.info("User updated successfully with ID: {}", user.id()))
                .flatMap(user -> ServerResponse.ok().bodyValue(
                        ApiResponse.success(HandlerMessages.USER_UPDATED_SUCCESS, user)))
                .onErrorResume(IllegalArgumentException.class, 
                    error -> handleValidationError(operation, requestId, error))
                .onErrorResume(EntityNotFoundException.class, 
                    error -> handleEntityNotFoundError(operation, requestId, error))
                .onErrorResume(error -> {
                    log.error("Unexpected error in {}: {}", operation, error.getMessage(), error);
                    return ServerResponse.badRequest().bodyValue(
                            ApiResponse.error(HandlerMessages.INVALID_REQUEST_FORMAT));
                });
    }
    
    public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
        String requestId = UUID.randomUUID().toString();
        String operation = "deleteUser";
        
        String id = serverRequest.pathVariable("id");
        log.info("Deleting user with ID: {}, requestId {}", id, requestId);
        return userHelper.deleteUser(id)
                .then(Mono.just("deleted"))
                .doOnNext(result -> log.info("User deleted successfully"))
                .flatMap(result -> ServerResponse.ok().bodyValue(
                        ApiResponse.success(HandlerMessages.USER_DELETED_SUCCESS, null)))
                .onErrorResume(IllegalArgumentException.class, 
                    error -> handleValidationError(operation, requestId, error))
                .onErrorResume(EntityNotFoundException.class, 
                    error -> handleEntityNotFoundError(operation, requestId, error))
                .onErrorResume(error -> handleUnexpectedError(operation, requestId, error));
    }
    
    public Mono<ServerResponse> getUserByEmail(ServerRequest serverRequest) {
        String requestId = UUID.randomUUID().toString();
        String operation = "getUserByEmail";
        String email = serverRequest.queryParam("email").orElse("");
        
        log.info("Searching user by email: {}, requestId: {}", email, requestId);
        return userHelper.getUserByEmail(email)
                .map(requestMapper::toResponse)
                .doOnNext(user -> log.info("User retrieved successfully by email: {}", user.id()))
                .flatMap(user -> ServerResponse.ok().bodyValue(
                        ApiResponse.success(HandlerMessages.USER_RETRIEVED_SUCCESS, user)))
                .onErrorResume(EntityNotFoundException.class, 
                    error -> handleEntityNotFoundError(operation, requestId, error))
                .onErrorResume(IllegalArgumentException.class, 
                    error -> handleValidationError(operation, requestId, error))
                .onErrorResume(error -> handleUnexpectedError(operation, requestId, error));
    }
    
    private Mono<ServerResponse> handleEntityNotFoundError(String operation, String requestId, Throwable error) {
        log.warn("User not found in {}-{}: {}", operation, requestId, error.getMessage());
        return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(
                ApiResponse.error(error.getMessage()));
    }
    
    private Mono<ServerResponse> handleValidationError(String operation, String requestId, Throwable error) {
        log.error("Validation error in {}-{}: {}", operation, requestId, error.getMessage());
        return ServerResponse.badRequest().bodyValue(
                ApiResponse.error(error.getMessage()));
    }
    
    private Mono<ServerResponse> handleConflictError(String operation, String requestId, Throwable error) {
        log.warn("Conflict in {}-{}: {}", operation, requestId, error.getMessage());
        return ServerResponse.status(HttpStatus.CONFLICT).bodyValue(
                ApiResponse.conflict(error.getMessage()));
    }
    
    private Mono<ServerResponse> handleUnexpectedError(String operation, String requestId, Throwable error) {
        log.error("Unexpected error in {}-{}: {}", operation, requestId, error.getMessage(), error);
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue(ApiResponse.error(HandlerMessages.INTERNAL_SERVER_ERROR));
    }
}
