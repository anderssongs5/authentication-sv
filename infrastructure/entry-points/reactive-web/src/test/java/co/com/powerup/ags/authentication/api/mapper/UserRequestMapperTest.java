package co.com.powerup.ags.authentication.api.mapper;

import co.com.powerup.ags.authentication.api.dto.CreateUserRequest;
import co.com.powerup.ags.authentication.api.dto.UpdateUserRequest;
import co.com.powerup.ags.authentication.api.dto.UserResponse;
import co.com.powerup.ags.authentication.usecase.user.dto.CreateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UpdateUserCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserRequestMapperTest {

    private static final String USER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String USER_NAME = "Steven";
    private static final String USER_LAST_NAME = "Garcia";
    private static final String USER_ADDRESS = "Carrera 60 # 53-14";
    private static final String USER_PHONE_NUMBER = "1234567890";
    private static final LocalDate USER_BIRTH_DATE = LocalDate.of(1990, 10, 1);
    private static final String USER_EMAIL = "steven.garcia@test.com";
    private static final BigDecimal USER_BASE_SALARY = new BigDecimal("50000.00");
    private static final String USER_ID_NUMBER = "561615616";

    private final UserRequestMapper mapper = UserRequestMapper.INSTANCE;
    
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private co.com.powerup.ags.authentication.usecase.user.dto.UserResponse domainUserResponse;

    @BeforeEach
    void setUp() {
        createUserRequest = new CreateUserRequest(
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                USER_ID_NUMBER
        );

        updateUserRequest = new UpdateUserRequest(
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                USER_ID_NUMBER
        );

        domainUserResponse = new co.com.powerup.ags.authentication.usecase.user.dto.UserResponse(
                USER_ID,
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                USER_PHONE_NUMBER,
                USER_BIRTH_DATE,
                USER_EMAIL,
                USER_BASE_SALARY,
                USER_ID_NUMBER
        );
    }

    @Test
    void shouldMapCreateUserRequestToCommand() {
        CreateUserCommand result = mapper.toCommand(createUserRequest);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(createUserRequest.getName());
        assertThat(result.lastName()).isEqualTo(createUserRequest.getLastName());
        assertThat(result.address()).isEqualTo(createUserRequest.getAddress());
        assertThat(result.phoneNumber()).isEqualTo(createUserRequest.getPhoneNumber());
        assertThat(result.birthDate()).isEqualTo(createUserRequest.getBirthDate());
        assertThat(result.email()).isEqualTo(createUserRequest.getEmail());
        assertThat(result.baseSalary()).isEqualTo(createUserRequest.getBaseSalary());
    }

    @Test
    void shouldMapUpdateUserRequestToCommandWithId() {
        UpdateUserCommand result = mapper.toCommand(updateUserRequest, USER_ID);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(USER_ID);
        assertThat(result.name()).isEqualTo(updateUserRequest.getName());
        assertThat(result.lastName()).isEqualTo(updateUserRequest.getLastName());
        assertThat(result.address()).isEqualTo(updateUserRequest.getAddress());
        assertThat(result.phoneNumber()).isEqualTo(updateUserRequest.getPhoneNumber());
        assertThat(result.birthDate()).isEqualTo(updateUserRequest.getBirthDate());
        assertThat(result.email()).isEqualTo(updateUserRequest.getEmail());
        assertThat(result.baseSalary()).isEqualTo(updateUserRequest.getBaseSalary());
    }

    @Test
    void shouldMapDomainUserResponseToApiResponse() {
        UserResponse result = mapper.toResponse(domainUserResponse);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(domainUserResponse.id());
        assertThat(result.name()).isEqualTo(domainUserResponse.name());
        assertThat(result.lastName()).isEqualTo(domainUserResponse.lastName());
        assertThat(result.address()).isEqualTo(domainUserResponse.address());
        assertThat(result.phoneNumber()).isEqualTo(domainUserResponse.phoneNumber());
        assertThat(result.birthDate()).isEqualTo(domainUserResponse.birthDate());
        assertThat(result.email()).isEqualTo(domainUserResponse.email());
        assertThat(result.baseSalary()).isEqualTo(domainUserResponse.baseSalary());
    }

    @Test
    void shouldMapDomainUserResponseListToApiResponseList() {
        co.com.powerup.ags.authentication.usecase.user.dto.UserResponse domainResponse2 = 
                new co.com.powerup.ags.authentication.usecase.user.dto.UserResponse(
                        "123e4567-e89b-12d3-a456-426614174001",
                        "Andersson",
                        "Garcia",
                        "Av 30 # 53-14",
                        "0987654321",
                        LocalDate.of(1985, 5, 20),
                        "andersson.garcia@example.com",
                        new BigDecimal("60000.00"),
                        "1245679"
                );

        List<co.com.powerup.ags.authentication.usecase.user.dto.UserResponse> domainList = 
                Arrays.asList(domainUserResponse, domainResponse2);

        List<UserResponse> result = mapper.toListResponse(domainList);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        UserResponse firstUser = result.get(0);
        assertThat(firstUser.id()).isEqualTo(domainUserResponse.id());
        assertThat(firstUser.name()).isEqualTo(domainUserResponse.name());
        assertThat(firstUser.email()).isEqualTo(domainUserResponse.email());

        UserResponse secondUser = result.get(1);
        assertThat(secondUser.id()).isEqualTo(domainResponse2.id());
        assertThat(secondUser.name()).isEqualTo(domainResponse2.name());
        assertThat(secondUser.email()).isEqualTo(domainResponse2.email());
    }

    @Test
    void shouldHandleNullValuesInCreateUserRequest() {
        CreateUserRequest requestWithNulls = new CreateUserRequest();
        requestWithNulls.setName(USER_NAME);
        requestWithNulls.setLastName(USER_LAST_NAME);

        CreateUserCommand result = mapper.toCommand(requestWithNulls);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(USER_NAME);
        assertThat(result.lastName()).isEqualTo(USER_LAST_NAME);
        assertThat(result.address()).isNull();
        assertThat(result.phoneNumber()).isNull();
        assertThat(result.birthDate()).isNull();
        assertThat(result.email()).isNull();
        assertThat(result.baseSalary()).isNull();
    }

    @Test
    void shouldHandleNullValuesInUpdateUserRequest() {
        UpdateUserRequest requestWithNulls = new UpdateUserRequest();
        requestWithNulls.setName(USER_NAME);
        requestWithNulls.setEmail(USER_EMAIL);

        UpdateUserCommand result = mapper.toCommand(requestWithNulls, USER_ID);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(USER_ID);
        assertThat(result.name()).isEqualTo(USER_NAME);
        assertThat(result.email()).isEqualTo(USER_EMAIL);
        assertThat(result.lastName()).isNull();
        assertThat(result.address()).isNull();
        assertThat(result.phoneNumber()).isNull();
        assertThat(result.birthDate()).isNull();
        assertThat(result.baseSalary()).isNull();
    }

    @Test
    void shouldHandleEmptyListMapping() {
        List<co.com.powerup.ags.authentication.usecase.user.dto.UserResponse> emptyList = 
                Arrays.asList();

        List<UserResponse> result = mapper.toListResponse(emptyList);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void shouldPreserveDataTypesInMapping() {
        CreateUserCommand command = mapper.toCommand(createUserRequest);
        UserResponse response = mapper.toResponse(domainUserResponse);

        assertThat(command.birthDate()).isInstanceOf(LocalDate.class);
        assertThat(command.baseSalary()).isInstanceOf(BigDecimal.class);
        assertThat(response.birthDate()).isInstanceOf(LocalDate.class);
        assertThat(response.baseSalary()).isInstanceOf(BigDecimal.class);

        assertThat(command.baseSalary()).isEqualTo(USER_BASE_SALARY);
        assertThat(command.birthDate()).isEqualTo(USER_BIRTH_DATE);
        assertThat(response.baseSalary()).isEqualTo(USER_BASE_SALARY);
        assertThat(response.birthDate()).isEqualTo(USER_BIRTH_DATE);
    }

    @Test
    void shouldMaintainFieldIntegrityAcrossAllMappings() {
        CreateUserCommand command = mapper.toCommand(createUserRequest);
        
        co.com.powerup.ags.authentication.usecase.user.dto.UserResponse simulatedDomainResponse = 
                new co.com.powerup.ags.authentication.usecase.user.dto.UserResponse(
                        "generated-id",
                        command.name(),
                        command.lastName(),
                        command.address(),
                        command.phoneNumber(),
                        command.birthDate(),
                        command.email(),
                        command.baseSalary(),
                        command.idNumber()
                );
        
        UserResponse finalResponse = mapper.toResponse(simulatedDomainResponse);

        assertThat(finalResponse.name()).isEqualTo(createUserRequest.getName());
        assertThat(finalResponse.lastName()).isEqualTo(createUserRequest.getLastName());
        assertThat(finalResponse.address()).isEqualTo(createUserRequest.getAddress());
        assertThat(finalResponse.phoneNumber()).isEqualTo(createUserRequest.getPhoneNumber());
        assertThat(finalResponse.birthDate()).isEqualTo(createUserRequest.getBirthDate());
        assertThat(finalResponse.email()).isEqualTo(createUserRequest.getEmail());
        assertThat(finalResponse.baseSalary()).isEqualTo(createUserRequest.getBaseSalary());
    }
}