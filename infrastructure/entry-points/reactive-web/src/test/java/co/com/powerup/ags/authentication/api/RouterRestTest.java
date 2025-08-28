package co.com.powerup.ags.authentication.api;

import co.com.powerup.ags.authentication.api.constants.HandlerMessages;
import co.com.powerup.ags.authentication.api.dto.CreateUserRequest;
import co.com.powerup.ags.authentication.api.dto.UpdateUserRequest;
import co.com.powerup.ags.authentication.api.helper.GlobalErrorAttributes;
import co.com.powerup.ags.authentication.model.common.exception.DataAlreadyExistsException;
import co.com.powerup.ags.authentication.model.common.exception.UserNotFoundException;
import co.com.powerup.ags.authentication.usecase.user.UserUseCase;
import co.com.powerup.ags.authentication.usecase.user.dto.CreateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UpdateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;

@Configuration
class TestConfig {
    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }
}

@ContextConfiguration(classes = {RouterRest.class, HandlerV1.class, TestConfig.class,
        GlobalExceptionHandler.class, GlobalErrorAttributes.class})
@WebFluxTest
class RouterRestTest {
    
    public static final String USER_ID_1 = "123e4567-e89b-12d3-a456-426614174001";
    public static final String USER_NAME_1 = "Raminder";
    public static final String USER_LAST_NAME_1 = "Kalher";
    public static final String USER_ADDRESS_1 = "Carrera 30 # 53-14";
    public static final String USER_PHONE_NUMBER_1 = "1234567890";
    public static final String USER_EMAIL_1 = "raminder.kalher@example.com";
    public static final String USER_ID_2 = "123e4567-e89b-12d3-a456-426614174002";
    public static final String USER_NAME_2 = "Monica";
    public static final String USER_LAST_NAME_2 = "Gil";
    public static final String USER_ADDRESS_2 = "Calle 60 # 71-12";
    public static final String USER_PHONE_NUMBER_2 = "0987654321";
    public static final String USER_EMAIL_2 = "monica.gil@gmail.com";
    public static final String USERS_PATH = "/api/v1/users";
    public static final String USER_BASE_SALARY_1 = "50000.00";

    @Autowired
    private WebTestClient webTestClient;
    
    @MockitoBean
    private UserUseCase userUseCase;
    
    UserResponse mockUser1;
    UserResponse mockUser2;
    
    @BeforeEach
    void setUp() {
         mockUser1 = new UserResponse(
                USER_ID_1,
                USER_NAME_1,
                USER_LAST_NAME_1,
                USER_ADDRESS_1,
                USER_PHONE_NUMBER_1,
                LocalDate.of(1990, 1, 15),
                USER_EMAIL_1,
                new BigDecimal(USER_BASE_SALARY_1)
        );
        
        mockUser2 = new UserResponse(
                USER_ID_2,
                USER_NAME_2,
                USER_LAST_NAME_2,
                USER_ADDRESS_2,
                USER_PHONE_NUMBER_2,
                LocalDate.of(1985, 5, 20),
                USER_EMAIL_2,
                new BigDecimal("60000.00")
        );
    }

    @Test
    void testGETAllUsers() {
        when(userUseCase.getAllUsers()).thenReturn(Flux.just(mockUser1, mockUser2));

        webTestClient.get()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo(HandlerMessages.USERS_RETRIEVED_SUCCESS)
                .jsonPath("$.path").isEqualTo(USERS_PATH)
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.data").isArray()
                .jsonPath("$.data.length()").isEqualTo(2)
                .jsonPath("$.data[0].id").isEqualTo(USER_ID_1)
                .jsonPath("$.data[0].name").isEqualTo(USER_NAME_1)
                .jsonPath("$.data[0].lastName").isEqualTo(USER_LAST_NAME_1)
                .jsonPath("$.data[0].email").isEqualTo(USER_EMAIL_1)
                .jsonPath("$.data[1].id").isEqualTo(USER_ID_2)
                .jsonPath("$.data[1].name").isEqualTo(USER_NAME_2)
                .jsonPath("$.data[1].lastName").isEqualTo(USER_LAST_NAME_2)
                .jsonPath("$.data[1].email").isEqualTo(USER_EMAIL_2);
    }

    @Test
    void testGETAllUsersWithDatabaseConnectionError() {
        when(userUseCase.getAllUsers())
                .thenReturn(Flux.error(new DataAccessResourceFailureException("Unable to connect to database")));

        webTestClient.get()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo(USERS_PATH)
                .jsonPath("$.error").exists();
    }

    @Test
    void testPOSTCreateUser() {
        CreateUserCommand createUserRequest = new CreateUserCommand(USER_NAME_1, USER_LAST_NAME_1, USER_ADDRESS_1,
                USER_PHONE_NUMBER_1, LocalDate.of(1985, 5, 20), USER_EMAIL_1, new BigDecimal(USER_BASE_SALARY_1));
        
        when(userUseCase.createUser(createUserRequest)).thenReturn(Mono.just(mockUser1));
        
        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(createUserRequest), CreateUserRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.message").isEqualTo(HandlerMessages.USER_CREATED_SUCCESS)
                .jsonPath("$.path").isEqualTo(USERS_PATH)
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.data.id").isEqualTo(USER_ID_1)
                .jsonPath("$.data.name").isEqualTo(USER_NAME_1)
                .jsonPath("$.data.lastName").isEqualTo(USER_LAST_NAME_1)
                .jsonPath("$.data.email").isEqualTo(USER_EMAIL_1);
    }

    @Test
    void testPOSTCreateUserWithDatabaseConnectionError() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName(USER_NAME_1);
        createUserRequest.setLastName(USER_LAST_NAME_1);
        createUserRequest.setAddress(USER_ADDRESS_1);
        createUserRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        createUserRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        createUserRequest.setEmail(USER_EMAIL_1);
        createUserRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        when(userUseCase.createUser(org.mockito.ArgumentMatchers.any(CreateUserCommand.class)))
                .thenReturn(Mono.error(new DataAccessResourceFailureException("Unable to connect to database")));

        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(createUserRequest)
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo(USERS_PATH)
                .jsonPath("$.error").exists();
    }

    @Test
    void testPOSTCreateUserWithExistingEmail() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName(USER_NAME_1);
        createUserRequest.setLastName(USER_LAST_NAME_1);
        createUserRequest.setAddress(USER_ADDRESS_1);
        createUserRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        createUserRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        createUserRequest.setEmail(USER_EMAIL_1);
        createUserRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        when(userUseCase.createUser(org.mockito.ArgumentMatchers.any(CreateUserCommand.class)))
                .thenReturn(Mono.error(new DataAlreadyExistsException("User with email " + USER_EMAIL_1 + " already exists")));

        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(createUserRequest)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo(USERS_PATH)
                .jsonPath("$.error").exists();
    }

    @Test
    void testGETUserByIdWithDatabaseConnectionError() {
        when(userUseCase.getUserById(USER_ID_1))
                .thenReturn(Mono.error(new DataAccessResourceFailureException("Unable to connect to database")));

        String uri = String.format("%s/%s", USERS_PATH, USER_ID_1);
        webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo(uri)
                .jsonPath("$.error").exists();
    }
    
    @Test
    void testGETUserById() {
        when(userUseCase.getUserById(USER_ID_1)).thenReturn(Mono.just(mockUser1));
        
        String uri = String.format("%s/%s", USERS_PATH, USER_ID_1);
        webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo(HandlerMessages.USER_RETRIEVED_SUCCESS)
                .jsonPath("$.path").isEqualTo(uri)
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.data.id").isEqualTo(USER_ID_1)
                .jsonPath("$.data.name").isEqualTo(USER_NAME_1)
                .jsonPath("$.data.lastName").isEqualTo(USER_LAST_NAME_1)
                .jsonPath("$.data.email").isEqualTo(USER_EMAIL_1);
    }

    @Test
    void testGETUserByIdNotFound() {
        String nonExistentUserId = "non-existent-id";
        
        when(userUseCase.getUserById(nonExistentUserId))
                .thenReturn(Mono.error(new UserNotFoundException("User with id " + nonExistentUserId + " not found")));

        String uri = String.format("%s/%s", USERS_PATH, nonExistentUserId);
        webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo(uri)
                .jsonPath("$.error").exists();
    }

    @Test
    void testPUTUpdateUserWithDatabaseConnectionError() {
        UpdateUserRequest updateRequest = new UpdateUserRequest(
                USER_NAME_2, USER_LAST_NAME_2, USER_ADDRESS_2, 
                USER_PHONE_NUMBER_2, LocalDate.of(1985, 5, 20), 
                USER_EMAIL_2, new BigDecimal("60000.00")
        );

        when(userUseCase.updateUser(org.mockito.ArgumentMatchers.any(UpdateUserCommand.class)))
                .thenReturn(Mono.error(new DataAccessResourceFailureException("Unable to connect to database")));

        String uri = String.format("%s/%s", USERS_PATH, USER_ID_1);
        webTestClient.put()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo(uri)
                .jsonPath("$.error").exists();
    }
    
    @Test
    void testDELETEUserById() {
        when(userUseCase.deleteUser(USER_ID_1)).thenReturn(Mono.empty());
        
        String uri = String.format("%s/%s", USERS_PATH, USER_ID_1);
        webTestClient.delete()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo(HandlerMessages.USER_DELETED_SUCCESS)
                .jsonPath("$.path").isEqualTo(uri)
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void testDELETEUserByIdNotFound() {
        String nonExistentUserId = "non-existent-id";
        
        when(userUseCase.deleteUser(nonExistentUserId))
                .thenReturn(Mono.error(new UserNotFoundException("User with id " + nonExistentUserId + " not found")));

        String uri = String.format("%s/%s", USERS_PATH, nonExistentUserId);
        webTestClient.delete()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo(uri)
                .jsonPath("$.error").exists();
    }

    @Test
    void testDELETEUserByIdWithDatabaseConnectionError() {
        when(userUseCase.deleteUser(USER_ID_1))
                .thenReturn(Mono.error(new DataAccessResourceFailureException("Unable to connect to database")));

        String uri = String.format("%s/%s", USERS_PATH, USER_ID_1);
        webTestClient.delete()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo(uri)
                .jsonPath("$.error").exists();
    }
    
    @Test
    void testGETUserByEmail() {
        when(userUseCase.getUserByEmail(USER_EMAIL_1)).thenReturn(Mono.just(mockUser1));
        
        String uri = String.format("%s/search?email=%s", USERS_PATH, USER_EMAIL_1);
        webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo(HandlerMessages.USER_RETRIEVED_SUCCESS)
                .jsonPath("$.path").isEqualTo(uri)
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.data.id").isEqualTo(USER_ID_1)
                .jsonPath("$.data.name").isEqualTo(USER_NAME_1)
                .jsonPath("$.data.lastName").isEqualTo(USER_LAST_NAME_1)
                .jsonPath("$.data.email").isEqualTo(USER_EMAIL_1);
    }

    @Test
    void testGETUserByEmailNotFound() {
        String nonExistentEmail = "nonexistent@example.com";
        
        when(userUseCase.getUserByEmail(nonExistentEmail))
                .thenReturn(Mono.error(new UserNotFoundException("User with email " + nonExistentEmail + " not found")));

        String uri = String.format("%s/search?email=%s", USERS_PATH, nonExistentEmail);
        webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo(uri)
                .jsonPath("$.error").exists();
    }

    @Test
    void testGETUserByEmailWithDatabaseConnectionError() {
        when(userUseCase.getUserByEmail(USER_EMAIL_1))
                .thenReturn(Mono.error(new DataAccessResourceFailureException("Unable to connect to database")));

        String uri = String.format("%s/search?email=%s", USERS_PATH, USER_EMAIL_1);
        webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo(uri)
                .jsonPath("$.error").exists();
    }
    
    @Test
    void testPOSTCreateUserWithBlankName() {
        CreateUserRequest invalidRequest = new CreateUserRequest();
        invalidRequest.setName("");
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }
    
    @Test
    void testPOSTCreateUserWithNullName() {
        CreateUserRequest invalidRequest = new CreateUserRequest();
        invalidRequest.setName(null);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPOSTCreateUserWithBlankLastName() {
        CreateUserRequest invalidRequest = new CreateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName("");
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPOSTCreateUserWithInvalidEmail() {
        CreateUserRequest invalidRequest = new CreateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPOSTCreateUserWithNullEmail() {
        CreateUserRequest invalidRequest = new CreateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(null);
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPOSTCreateUserWithInvalidPhoneNumber() {
        CreateUserRequest invalidRequest = new CreateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber("invalid-phone");
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPOSTCreateUserWithFutureBirthDate() {
        CreateUserRequest invalidRequest = new CreateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.now().plusDays(1));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPOSTCreateUserWithNullBirthDate() {
        CreateUserRequest invalidRequest = new CreateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(null);
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPOSTCreateUserWithNegativeBaseSalary() {
        CreateUserRequest invalidRequest = new CreateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal("-1000"));

        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPOSTCreateUserWithExcessiveBaseSalary() {
        CreateUserRequest invalidRequest = new CreateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal("20000000"));

        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPOSTCreateUserWithNullBaseSalary() {
        CreateUserRequest invalidRequest = new CreateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(null);

        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPOSTCreateUserWithBlankAddress() {
        CreateUserRequest invalidRequest = new CreateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress("");
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPOSTCreateUserWithBlankPhoneNumber() {
        CreateUserRequest invalidRequest = new CreateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber("");
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        webTestClient.post()
                .uri(USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
        
        .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPUTUpdateUser() {
        UpdateUserRequest updateRequest = new UpdateUserRequest(
                USER_NAME_2, USER_LAST_NAME_2, USER_ADDRESS_2, 
                USER_PHONE_NUMBER_2, LocalDate.of(1985, 5, 20), 
                USER_EMAIL_2, new BigDecimal("60000.00")
        );

        UserResponse updatedUser = new UserResponse(
                USER_ID_1, USER_NAME_2, USER_LAST_NAME_2, USER_ADDRESS_2,
                USER_PHONE_NUMBER_2, LocalDate.of(1985, 5, 20), 
                USER_EMAIL_2, new BigDecimal("60000.00")
        );

        when(userUseCase.updateUser(org.mockito.ArgumentMatchers.any(UpdateUserCommand.class)))
                .thenReturn(Mono.just(updatedUser));

        String uri = String.format("%s/%s", USERS_PATH, USER_ID_1);
        webTestClient.put()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo(HandlerMessages.USER_UPDATED_SUCCESS)
                .jsonPath("$.path").isEqualTo(uri)
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.data.id").isEqualTo(USER_ID_1)
                .jsonPath("$.data.name").isEqualTo(USER_NAME_2)
                .jsonPath("$.data.lastName").isEqualTo(USER_LAST_NAME_2)
                .jsonPath("$.data.email").isEqualTo(USER_EMAIL_2)
                .jsonPath("$.data.baseSalary").isEqualTo(60000.00);
    }

    @Test
    void testPUTUpdateUserNotFound() {
        String nonExistentUserId = "non-existent-id";
        UpdateUserRequest updateRequest = new UpdateUserRequest(
                USER_NAME_2, USER_LAST_NAME_2, USER_ADDRESS_2, 
                USER_PHONE_NUMBER_2, LocalDate.of(1985, 5, 20), 
                USER_EMAIL_2, new BigDecimal("60000.00")
        );

        when(userUseCase.updateUser(org.mockito.ArgumentMatchers.any(UpdateUserCommand.class)))
                .thenReturn(Mono.error(new UserNotFoundException("User with id " + nonExistentUserId + " not found")));

        String uri = String.format("%s/%s", USERS_PATH, nonExistentUserId);
        webTestClient.put()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo(uri)
                .jsonPath("$.error").exists();
    }

    @Test
    void testPUTUpdateUserWithBlankName() {
        UpdateUserRequest invalidRequest = new UpdateUserRequest();
        invalidRequest.setName("");
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        String uri = String.format("%s/%s", USERS_PATH, USER_ID_1);
        webTestClient.put()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPUTUpdateUserWithBlankLastName() {
        UpdateUserRequest invalidRequest = new UpdateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName("");
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        String uri = String.format("%s/%s", USERS_PATH, USER_ID_1);
        webTestClient.put()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPUTUpdateUserWithInvalidEmail() {
        UpdateUserRequest invalidRequest = new UpdateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        String uri = String.format("%s/%s", USERS_PATH, USER_ID_1);
        webTestClient.put()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPUTUpdateUserWithInvalidPhoneNumber() {
        UpdateUserRequest invalidRequest = new UpdateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber("invalid-phone");
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        String uri = String.format("%s/%s", USERS_PATH, USER_ID_1);
        webTestClient.put()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPUTUpdateUserWithFutureBirthDate() {
        UpdateUserRequest invalidRequest = new UpdateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.now().plusDays(1));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        String uri = String.format("%s/%s", USERS_PATH, USER_ID_1);
        webTestClient.put()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPUTUpdateUserWithNegativeBaseSalary() {
        UpdateUserRequest invalidRequest = new UpdateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal("-1000"));

        String uri = String.format("%s/%s", USERS_PATH, USER_ID_1);
        webTestClient.put()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPUTUpdateUserWithExcessiveBaseSalary() {
        UpdateUserRequest invalidRequest = new UpdateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal("20000000"));

        String uri = String.format("%s/%s", USERS_PATH, USER_ID_1);
        webTestClient.put()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPUTUpdateUserWithBlankAddress() {
        UpdateUserRequest invalidRequest = new UpdateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress("");
        invalidRequest.setPhoneNumber(USER_PHONE_NUMBER_1);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        String uri = String.format("%s/%s", USERS_PATH, USER_ID_1);
        webTestClient.put()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPUTUpdateUserWithBlankPhoneNumber() {
        UpdateUserRequest invalidRequest = new UpdateUserRequest();
        invalidRequest.setName(USER_NAME_1);
        invalidRequest.setLastName(USER_LAST_NAME_1);
        invalidRequest.setAddress(USER_ADDRESS_1);
        invalidRequest.setPhoneNumber("");
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        invalidRequest.setEmail(USER_EMAIL_1);
        invalidRequest.setBaseSalary(new BigDecimal(USER_BASE_SALARY_1));

        String uri = String.format("%s/%s", USERS_PATH, USER_ID_1);
        webTestClient.put()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
