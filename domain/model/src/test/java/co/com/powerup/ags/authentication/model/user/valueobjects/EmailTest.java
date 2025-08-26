package co.com.powerup.ags.authentication.model.user.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "test@example.com",
            "user.name@domain.com",
            "test123@test-domain.org",
            "valid_email@example.co.uk",
            "a@b.co",
            "test+tag@example.com"
    })
    void shouldCreateEmailWithValidFormats(String validEmail) {
        // When
        Email email = new Email(validEmail);

        // Then
        assertThat(email.value()).isEqualTo(validEmail.toLowerCase());
    }

    @Test
    void shouldTrimAndConvertToLowercase() {
        // Given
        String emailWithSpacesAndUpperCase = "  TEST@EXAMPLE.COM  ";

        // When
        Email email = new Email(emailWithSpacesAndUpperCase);

        // Then
        assertThat(email.value()).isEqualTo("test@example.com");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldThrowExceptionForNullOrEmptyEmails(String invalidEmail) {
        // When & Then
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email must have a value");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "test@",
            "@example.com",
            "test@@example.com",
            "test.example.com",
            "test@example",
            "test@.com",
            "test@example.",
            "test @example.com",
            "test@exam ple.com"
    })
    void shouldThrowExceptionForInvalidEmailFormats(String invalidEmail) {
        // When & Then
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email does not have a valid format: " + invalidEmail);
    }

    @Test
    void shouldExtractDomainCorrectly() {
        // Given
        Email email = new Email("test@example.com");

        // When
        String domain = email.getDomain();

        // Then
        assertThat(domain).isEqualTo("example.com");
    }

    @Test
    void shouldExtractLocalPartCorrectly() {
        // Given
        Email email = new Email("test.user@example.com");

        // When
        String localPart = email.getLocalPart();

        // Then
        assertThat(localPart).isEqualTo("test.user");
    }

    @Test
    void shouldBeEqualWhenEmailValuesAreTheSame() {
        // Given
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("TEST@EXAMPLE.COM");

        // When & Then
        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenEmailValuesAreDifferent() {
        // Given
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("different@example.com");

        // When & Then
        assertThat(email1).isNotEqualTo(email2);
    }

    @Test
    void shouldReturnEmailValueAsString() {
        // Given
        String emailValue = "test@example.com";
        Email email = new Email(emailValue);

        // When & Then
        assertThat(email.toString()).isEqualTo(emailValue);
    }

    @Test
    void shouldHandleEqualsWithNullAndDifferentTypes() {
        // Given
        Email email = new Email("test@example.com");

        // When & Then
        assertThat(email).isNotEqualTo(null);
        assertThat(email).isNotEqualTo("test@example.com");
        assertThat(email).isEqualTo(email);
    }
}