package co.com.powerup.ags.authentication.api;

import co.com.powerup.ags.authentication.api.constants.HandlerMessages;
import co.com.powerup.ags.authentication.api.dto.CreateUserRequest;
import co.com.powerup.ags.authentication.api.dto.SuccessResponse;
import co.com.powerup.ags.authentication.api.dto.UpdateUserRequest;
import co.com.powerup.ags.authentication.api.dto.UserResponse;
import co.com.powerup.ags.authentication.api.mapper.UserRequestMapper;
import co.com.powerup.ags.authentication.usecase.user.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class HandlerV1 {
    
    private static final Logger log = LoggerFactory.getLogger(HandlerV1.class);
    
    private final UserRequestMapper requestMapper;
    private final IUserService userService;
    private final Validator validator;
    
    public HandlerV1(IUserService userService, Validator validator) {
        this.requestMapper = UserRequestMapper.INSTANCE;
        this.validator = validator;
        this.userService = userService;
    }
    
    private <T> Mono<T> validateRequest(T request) {
        Errors errors = new BeanPropertyBindingResult(request, request.getClass().getSimpleName());
        validator.validate(request, errors);
        
        if (errors.hasErrors()) {
            String errorMessage = errors.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                    .orElse("Validation failed");
            return Mono.error(new IllegalArgumentException(errorMessage));
        }
        
        return Mono.just(request);
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        log.info("Retrieving all users.");
        return userService.getAllUsers()
                .collectList()
                .doOnNext(users -> log.info("Retrieved {} users successfully", users.size()))
                .map(requestMapper::toListResponse)
                .flatMap(users -> {
                    SuccessResponse<List<UserResponse>> successResponse = SuccessResponse.<List<UserResponse>>builder()
                            .timestamp(LocalDateTime.now())
                            .path(serverRequest.path())
                            .data(users)
                            .message(HandlerMessages.USERS_RETRIEVED_SUCCESS)
                            .build();
                    
                    return ServerResponse.ok().bodyValue(successResponse);
                })
                .doOnError(throwable -> log.error("Error getting all users: {}", throwable.getMessage()));
    }

    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        log.info("Retrieving user with ID: {} ", id);
        return userService.getUserById(id)
                .map(requestMapper::toResponse)
                .doOnNext(user -> log.info("User retrieved successfully: {}", user.id()))
                .flatMap(userResponse -> {
                    SuccessResponse<UserResponse> successResponse = SuccessResponse.<UserResponse>builder()
                            .timestamp(LocalDateTime.now())
                            .path(serverRequest.path())
                            .data(userResponse)
                            .message(HandlerMessages.USER_RETRIEVED_SUCCESS)
                            .build();
                    
                    return ServerResponse.ok().bodyValue(successResponse);
                })
                .doOnError(throwable -> log.error("Error getting user with ID {}: {}", id, throwable.getMessage()));
    }

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateUserRequest.class)
                .flatMap(this::validateRequest)
                .map(requestMapper::toCommand)
                .doOnNext(command -> log.info("Creating new user with email: {}", command.email()))
                .flatMap(userService::createUser)
                .map(requestMapper::toResponse)
                .doOnNext(user -> log.info("User created successfully with ID: {}", user.id()))
                .flatMap(user -> {
                    SuccessResponse<UserResponse> successResponse = SuccessResponse.<UserResponse>builder()
                            .timestamp(LocalDateTime.now())
                            .path(serverRequest.path())
                            .data(user)
                            .message(HandlerMessages.USER_CREATED_SUCCESS)
                            .build();
                    
                    return ServerResponse.created(URI.create("/api/v1/users/" + user.id()))
                            .bodyValue(successResponse);
                })
                .doOnError(throwable -> log.error("Error creating user: {}", throwable.getMessage()));
    }
    
    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        log.info("Updating user with ID: {} ", id);
        return serverRequest.bodyToMono(UpdateUserRequest.class)
                .flatMap(this::validateRequest)
                .map(request -> requestMapper.toCommand(request, id))
                .flatMap(userService::updateUser)
                .map(requestMapper::toResponse)
                .doOnNext(user -> log.info("User updated successfully with ID: {}", user.id()))
                .flatMap(userResponse -> {
                    SuccessResponse<UserResponse> successResponse = SuccessResponse.<UserResponse>builder()
                            .timestamp(LocalDateTime.now())
                            .path(serverRequest.path())
                            .data(userResponse)
                            .message(HandlerMessages.USER_UPDATED_SUCCESS)
                            .build();
                    
                    return ServerResponse.ok().bodyValue(successResponse);
                })
                .doOnError(throwable -> log.error("Error updating user with ID {}: {}", id, throwable.getMessage()));
    }
    
    public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        log.info("Deleting user with ID: {}", id);
        return userService.deleteUser(id)
                .then(Mono.just("deleted"))
                .doOnNext(result -> log.info("User deleted successfully"))
                .flatMap(result -> {
                    SuccessResponse<String> successResponse = SuccessResponse.<String>builder()
                            .timestamp(LocalDateTime.now())
                            .path(serverRequest.path())
                            .data(String.format("""
                                    "deletedId": "%s"
                                    """, id))
                            .message(HandlerMessages.USER_DELETED_SUCCESS)
                            .build();
                    
                    return ServerResponse.ok().bodyValue(successResponse);
                });
    }
    
    public Mono<ServerResponse> getUserByEmail(ServerRequest serverRequest) {
        String email = serverRequest.queryParam("email").orElse("");
        
        log.info("Searching user by email: {}", email);
        return userService.getUserByEmail(email)
                .map(requestMapper::toResponse)
                .doOnNext(user -> log.info("User retrieved successfully by email: {}", user.email()))
                .flatMap(userResponse -> {
                    SuccessResponse<UserResponse> successResponse = SuccessResponse.<UserResponse>builder()
                            .timestamp(LocalDateTime.now())
                            .path(serverRequest.path() + (serverRequest.uri().getQuery() != null ? "?" + serverRequest.uri().getQuery() : ""))
                            .data(userResponse)
                            .message(HandlerMessages.USER_RETRIEVED_SUCCESS)
                            .build();
                    
                    return ServerResponse.ok().bodyValue(successResponse);
                })
                .doOnError(throwable -> log.error("Error getting user with email {}: {}", email, throwable.getMessage()));
    }
}
