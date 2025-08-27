package co.com.powerup.ags.authentication.r2dbc.mapper;

import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;
import co.com.powerup.ags.authentication.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityMapperTest {

    private static final String USER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String USER_NAME = "Steven";
    private static final String USER_LAST_NAME = "Garcia";
    private static final String USER_ADDRESS = "Carrera 60 # 53-14";
    private static final String USER_PHONE_NUMBER = "1234567890";
    private static final LocalDate USER_BIRTH_DATE = LocalDate.of(1990, 10, 1);
    private static final String USER_EMAIL = "steven.garcia@test.com";
    private static final BigDecimal USER_BASE_SALARY = new BigDecimal("50000.00");

    private final UserEntityMapper mapper = UserEntityMapper.INSTANCE;
    
    private User validUser;
    private UserEntity validUserEntity;

    @BeforeEach
    void setUp() {
        validUser = new User(
                USER_ID,
                USER_NAME,
                USER_LAST_NAME,
                USER_ADDRESS,
                new PhoneNumber(USER_PHONE_NUMBER),
                USER_BIRTH_DATE,
                new Email(USER_EMAIL),
                USER_BASE_SALARY
        );

        validUserEntity = UserEntity.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .lastName(USER_LAST_NAME)
                .address(USER_ADDRESS)
                .phoneNumber(USER_PHONE_NUMBER)
                .birthDate(USER_BIRTH_DATE)
                .email(USER_EMAIL)
                .baseSalary(USER_BASE_SALARY)
                .isNew(true)
                .build();
    }

    @Test
    void shouldMapUserToEntity() {
        UserEntity result = mapper.toEntity(validUser);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(validUser.id());
        assertThat(result.getName()).isEqualTo(validUser.name());
        assertThat(result.getLastName()).isEqualTo(validUser.lastName());
        assertThat(result.getAddress()).isEqualTo(validUser.address());
        assertThat(result.getPhoneNumber()).isEqualTo(validUser.phoneNumber().value());
        assertThat(result.getBirthDate()).isEqualTo(validUser.birthDate());
        assertThat(result.getEmail()).isEqualTo(validUser.email().value());
        assertThat(result.getBaseSalary()).isEqualTo(validUser.baseSalary());
        assertThat(result.isNew()).isTrue();
    }

    @Test
    void shouldMapUserToExistingEntity() {
        UserEntity result = mapper.toExistingEntity(validUser);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(validUser.id());
        assertThat(result.getName()).isEqualTo(validUser.name());
        assertThat(result.getLastName()).isEqualTo(validUser.lastName());
        assertThat(result.getAddress()).isEqualTo(validUser.address());
        assertThat(result.getPhoneNumber()).isEqualTo(validUser.phoneNumber().value());
        assertThat(result.getBirthDate()).isEqualTo(validUser.birthDate());
        assertThat(result.getEmail()).isEqualTo(validUser.email().value());
        assertThat(result.getBaseSalary()).isEqualTo(validUser.baseSalary());
        assertThat(result.isNew()).isFalse(); // Key difference: existing entity
    }

    @Test
    void shouldMapEntityToDomain() {
        User result = mapper.toDomain(validUserEntity);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(validUserEntity.getId());
        assertThat(result.name()).isEqualTo(validUserEntity.getName());
        assertThat(result.lastName()).isEqualTo(validUserEntity.getLastName());
        assertThat(result.address()).isEqualTo(validUserEntity.getAddress());
        assertThat(result.phoneNumber()).isNotNull();
        assertThat(result.phoneNumber().value()).isEqualTo(validUserEntity.getPhoneNumber());
        assertThat(result.birthDate()).isEqualTo(validUserEntity.getBirthDate());
        assertThat(result.email()).isNotNull();
        assertThat(result.email().value()).isEqualTo(validUserEntity.getEmail());
        assertThat(result.baseSalary()).isEqualTo(validUserEntity.getBaseSalary());
    }

    @Test
    void shouldHandlePhoneNumberValueObjectConversion() {
        UserEntity entity = mapper.toEntity(validUser);
        User domainFromEntity = mapper.toDomain(entity);

        assertThat(domainFromEntity.phoneNumber().value()).isEqualTo(validUser.phoneNumber().value());
        assertThat(domainFromEntity.phoneNumber().value()).isEqualTo(USER_PHONE_NUMBER);
    }

    @Test
    void shouldHandleEmailValueObjectConversion() {
        UserEntity entity = mapper.toEntity(validUser);
        User domainFromEntity = mapper.toDomain(entity);

        assertThat(domainFromEntity.email().value()).isEqualTo(validUser.email().value());
        assertThat(domainFromEntity.email().value()).isEqualTo(USER_EMAIL);
    }

    @Test
    void shouldPreserveAllFieldsInRoundTripConversion() {
        UserEntity entity = mapper.toEntity(validUser);
        User result = mapper.toDomain(entity);

        assertThat(result.id()).isEqualTo(validUser.id());
        assertThat(result.name()).isEqualTo(validUser.name());
        assertThat(result.lastName()).isEqualTo(validUser.lastName());
        assertThat(result.address()).isEqualTo(validUser.address());
        assertThat(result.phoneNumber().value()).isEqualTo(validUser.phoneNumber().value());
        assertThat(result.birthDate()).isEqualTo(validUser.birthDate());
        assertThat(result.email().value()).isEqualTo(validUser.email().value());
        assertThat(result.baseSalary()).isEqualTo(validUser.baseSalary());
    }

    @Test
    void shouldHandleNullValues() {
        UserEntity entityWithNulls = UserEntity.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .lastName(USER_LAST_NAME)
                .address(null)
                .phoneNumber(USER_PHONE_NUMBER)
                .birthDate(USER_BIRTH_DATE)
                .email(USER_EMAIL)
                .baseSalary(USER_BASE_SALARY)
                .build();

        User result = mapper.toDomain(entityWithNulls);

        assertThat(result).isNotNull();
        assertThat(result.address()).isNull();
        assertThat(result.name()).isEqualTo(USER_NAME);
        assertThat(result.phoneNumber().value()).isEqualTo(USER_PHONE_NUMBER);
        assertThat(result.email().value()).isEqualTo(USER_EMAIL);
    }
}