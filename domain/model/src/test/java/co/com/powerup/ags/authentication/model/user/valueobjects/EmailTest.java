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
        Email email = new Email(validEmail);

        assertThat(email.value()).isEqualTo(validEmail.toLowerCase());
    }

    @Test
    void shouldTrimAndConvertToLowercase() {
        String emailWithSpacesAndUpperCase = "  TEST@EXAMPLE.COM  ";

        Email email = new Email(emailWithSpacesAndUpperCase);

        assertThat(email.value()).isEqualTo("test@example.com");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldThrowExceptionForNullOrEmptyEmails(String invalidEmail) {
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
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email does not have a valid format: " + invalidEmail);
    }

    @Test
    void shouldExtractDomainCorrectly() {
        Email email = new Email("test@example.com");

        String domain = email.getDomain();

        assertThat(domain).isEqualTo("example.com");
    }

    @Test
    void shouldExtractLocalPartCorrectly() {
        Email email = new Email("test.user@example.com");

        String localPart = email.getLocalPart();

        assertThat(localPart).isEqualTo("test.user");
    }

    @Test
    void shouldBeEqualWhenEmailValuesAreTheSame() {
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("TEST@EXAMPLE.COM");

        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenEmailValuesAreDifferent() {
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("different@example.com");

        assertThat(email1).isNotEqualTo(email2);
    }

    @Test
    void shouldReturnEmailValueAsString() {
        String emailValue = "test@example.com";
        Email email = new Email(emailValue);

        assertThat(email.toString()).isEqualTo(emailValue);
    }

    @Test
    void shouldHandleEqualsWithNullAndDifferentTypes() {
        Email email = new Email("test@example.com");

        assertThat(email).isNotEqualTo(null);
        assertThat(email).isNotEqualTo("test@example.com");
        assertThat(email).isEqualTo(email);
    }
}