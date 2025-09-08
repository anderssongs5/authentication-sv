package co.com.powerup.ags.authentication.model.user;

import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class UserTest {
    
    private static final String VALID_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String VALID_NAME = "Steven";
    private static final String VALID_LAST_NAME = "Garcia";
    private static final String VALID_ADDRESS = "Cra 60 # 53-14";
    private static final PhoneNumber VALID_PHONE_NUMBER = new PhoneNumber("1234567890");
    private static final LocalDate VALID_BIRTH_DATE = LocalDate.of(1990, 10, 1);
    private static final Email VALID_EMAIL = new Email("steven.garcia@test.com");
    private static final BigDecimal VALID_BASE_SALARY = new BigDecimal("50000.00");
    private static final String VALID_ID_NUMBER = "123456";
    
    @Test
    void shouldCreateUserWithAllValidParameters() {
        User user = new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                VALID_BIRTH_DATE,
                VALID_EMAIL,
                VALID_BASE_SALARY,
                VALID_ID_NUMBER
        );
        
        assertThat(user).isNotNull();
        assertThat(user.id()).isEqualTo(VALID_ID);
        assertThat(user.name()).isEqualTo(VALID_NAME);
        assertThat(user.lastName()).isEqualTo(VALID_LAST_NAME);
        assertThat(user.address()).isEqualTo(VALID_ADDRESS);
        assertThat(user.phoneNumber()).isEqualTo(VALID_PHONE_NUMBER);
        assertThat(user.birthDate()).isEqualTo(VALID_BIRTH_DATE);
        assertThat(user.email()).isEqualTo(VALID_EMAIL);
        assertThat(user.baseSalary()).isEqualTo(VALID_BASE_SALARY);
    }
    
    @Test
    void shouldCreateUserWithMinimumAge() {
        LocalDate eighteenYearsAgo = LocalDate.now().minusYears(18);
        User user = new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                eighteenYearsAgo,
                VALID_EMAIL,
                VALID_BASE_SALARY,
                VALID_ID_NUMBER
        );
        
        assertThat(user.birthDate()).isEqualTo(eighteenYearsAgo);
    }
    
    @Test
    void shouldCreateUserWithMaximumSalary() {
        BigDecimal maxSalary = new BigDecimal("15000000");
        
        User user = new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                VALID_BIRTH_DATE,
                VALID_EMAIL,
                maxSalary,
                VALID_ID_NUMBER
        );
        
        assertThat(user.baseSalary()).isEqualByComparingTo(maxSalary);
    }
    
    @Test
    void shouldCreateUserWithMinimumSalary() {
        BigDecimal minSalary = new BigDecimal("0.01");
        
        User user = new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                VALID_BIRTH_DATE,
                VALID_EMAIL,
                minSalary,
                VALID_ID_NUMBER
        );
        
        assertThat(user.baseSalary()).isEqualByComparingTo(minSalary);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", ""})
    void shouldThrowExceptionForInvalidNames(String invalidName) {
        assertThatThrownBy(() -> new User(
                VALID_ID,
                invalidName,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                VALID_BIRTH_DATE,
                VALID_EMAIL,
                VALID_BASE_SALARY,
                VALID_ID_NUMBER
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name cannot be null or empty");
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldThrowExceptionForInvalidLastNames(String invalidLastName) {
        assertThatThrownBy(() -> new User(
                VALID_ID,
                VALID_NAME,
                invalidLastName,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                VALID_BIRTH_DATE,
                VALID_EMAIL,
                VALID_BASE_SALARY,
                VALID_ID_NUMBER
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Last name cannot be null or empty");
    }
    
    @Test
    void shouldThrowExceptionForNullPhoneNumber() {
        assertThatThrownBy(() -> new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                null,
                VALID_BIRTH_DATE,
                VALID_EMAIL,
                VALID_BASE_SALARY,
                VALID_ID_NUMBER
        ))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Phone number cannot be null");
    }
    
    @Test
    void shouldThrowExceptionForNullEmail() {
        assertThatThrownBy(() -> new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                VALID_BIRTH_DATE,
                null,
                VALID_BASE_SALARY,
                VALID_ID_NUMBER
        ))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Email cannot be null");
    }
    
    @Test
    void shouldThrowExceptionForNullBirthDate() {
        assertThatThrownBy(() -> new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                null,
                VALID_EMAIL,
                VALID_BASE_SALARY,
                VALID_ID_NUMBER
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Birth date cannot be null");
    }
    
    @Test
    void shouldThrowExceptionForFutureBirthDate() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        
        assertThatThrownBy(() -> new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                futureDate,
                VALID_EMAIL,
                VALID_BASE_SALARY,
                VALID_ID_NUMBER
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User must be at least 18 years old");
    }
    
    @Test
    void shouldThrowExceptionForUserUnder18YearsOld() {
        LocalDate seventeenYearsAgo = LocalDate.now().minusYears(17);
        
        assertThatThrownBy(() -> new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                seventeenYearsAgo,
                VALID_EMAIL,
                VALID_BASE_SALARY,
                VALID_ID_NUMBER
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User must be at least 18 years old");
    }
    
    @Test
    void shouldThrowExceptionForNullBaseSalary() {
        assertThatThrownBy(() -> new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                VALID_BIRTH_DATE,
                VALID_EMAIL,
                null,
                VALID_ID_NUMBER
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Base salary cannot be null");
    }
    
    @Test
    void shouldThrowExceptionForNegativeBaseSalary() {
        BigDecimal negativeSalary = new BigDecimal("-1000");
        
        assertThatThrownBy(() -> new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                VALID_BIRTH_DATE,
                VALID_EMAIL,
                negativeSalary,
                VALID_ID_NUMBER
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Base salary cannot be negative");
    }
    
    @Test
    void shouldThrowExceptionForZeroBaseSalary() {
        BigDecimal negativeSalary = BigDecimal.ONE.negate();
        
        assertThatThrownBy(() -> new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                VALID_BIRTH_DATE,
                VALID_EMAIL,
                negativeSalary,
                VALID_ID_NUMBER
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Base salary cannot be negative");
    }
    
    @Test
    void shouldThrowExceptionForSalaryExceedingMaximum() {
        BigDecimal excessiveSalary = new BigDecimal("15000001");
        
        assertThatThrownBy(() -> new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                VALID_BIRTH_DATE,
                VALID_EMAIL,
                excessiveSalary,
                VALID_ID_NUMBER
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Base salary cannot be greater than 15,000,000");
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldThrowExceptionForInvalidIdNumber(String invalidIdNumber) {
        assertThatThrownBy(() -> new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                VALID_BIRTH_DATE,
                VALID_EMAIL,
                VALID_BASE_SALARY,
                invalidIdNumber
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID number cannot be null or empty");
    }
    
    @Test
    void shouldBeEqualWhenIdsAreTheSame() {
        User user1 = new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                VALID_BIRTH_DATE,
                VALID_EMAIL,
                VALID_BASE_SALARY,
                VALID_ID_NUMBER
        );
        User user2 = new User(
                VALID_ID,
                "Different Name",
                "Different Last Name",
                "Different Address",
                new PhoneNumber("9876543210"),
                LocalDate.of(1985, 12, 25),
                new Email("different@example.com"),
                new BigDecimal("100000"),
                "654616"
        );
        
        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        User user1 = new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                VALID_BIRTH_DATE,
                VALID_EMAIL,
                VALID_BASE_SALARY,
                VALID_ID_NUMBER
        );
        User user2 = new User(
                "different-id",
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                VALID_BIRTH_DATE,
                VALID_EMAIL,
                VALID_BASE_SALARY,
                VALID_ID_NUMBER
        );
        
        assertThat(user1).isNotEqualTo(user2);
    }
    
    @Test
    void shouldHandleEqualsWithNullAndDifferentTypes() {
        User user = new User(
                VALID_ID,
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_PHONE_NUMBER,
                VALID_BIRTH_DATE,
                VALID_EMAIL,
                VALID_BASE_SALARY,
                VALID_ID_NUMBER
        );
        
        assertThat(user).isNotEqualTo(null);
        assertThat(user).isNotEqualTo("not a user");
        assertThat(user).isEqualTo(user);
    }
}