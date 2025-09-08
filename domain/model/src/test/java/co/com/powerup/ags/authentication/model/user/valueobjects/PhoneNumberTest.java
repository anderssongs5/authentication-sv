package co.com.powerup.ags.authentication.model.user.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class PhoneNumberTest {
    
    @ParameterizedTest
    @ValueSource(strings = {
            "1234567890",
            "123456789012345",
            "1",
            "555123456789",
            "9876543210"
    })
    void shouldCreatePhoneNumberWithValidNumbers(String validPhoneNumber) {
        PhoneNumber phoneNumber = new PhoneNumber(validPhoneNumber);
        
        assertThat(phoneNumber.value()).isEqualTo(validPhoneNumber);
    }
    
    @Test
    void shouldTrimWhitespaceFromPhoneNumber() {
        String phoneNumberWithSpaces = "  1234567890  ";
        
        PhoneNumber phoneNumber = new PhoneNumber(phoneNumberWithSpaces);
        
        assertThat(phoneNumber.value()).isEqualTo("1234567890");
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\n"})
    void shouldThrowExceptionForNullOrEmptyPhoneNumbers(String invalidPhoneNumber) {
        assertThatThrownBy(() -> new PhoneNumber(invalidPhoneNumber))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Phone number must have a value");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
            "123-456-7890",
            "+1234567890",
            "(123)456-7890",
            "123 456 7890",
            "123.456.7890",
            "123a456b789",
            "123#456*789",
            "abc1234567",
            "1234567890a"
    })
    void shouldThrowExceptionForNonNumericPhoneNumbers(String invalidPhoneNumber) {
        assertThatThrownBy(() -> new PhoneNumber(invalidPhoneNumber))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Phone number must contain only numbers: " + invalidPhoneNumber);
    }
    
    @Test
    void shouldBeEqualWhenPhoneNumberValuesAreTheSame() {
        PhoneNumber phoneNumber1 = new PhoneNumber("1234567890");
        PhoneNumber phoneNumber2 = new PhoneNumber("1234567890");
        
        assertThat(phoneNumber1).isEqualTo(phoneNumber2);
        assertThat(phoneNumber1.hashCode()).isEqualTo(phoneNumber2.hashCode());
    }
    
    @Test
    void shouldBeEqualWhenTrimmedValuesAreTheSame() {
        PhoneNumber phoneNumber1 = new PhoneNumber("1234567890");
        PhoneNumber phoneNumber2 = new PhoneNumber("  1234567890  ");
        
        assertThat(phoneNumber1).isEqualTo(phoneNumber2);
        assertThat(phoneNumber1.hashCode()).isEqualTo(phoneNumber2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenPhoneNumberValuesAreDifferent() {
        PhoneNumber phoneNumber1 = new PhoneNumber("1234567890");
        PhoneNumber phoneNumber2 = new PhoneNumber("9876543210");
        
        assertThat(phoneNumber1).isNotEqualTo(phoneNumber2);
    }
    
    @Test
    void shouldReturnPhoneNumberValueAsString() {
        String phoneNumberValue = "1234567890";
        PhoneNumber phoneNumber = new PhoneNumber(phoneNumberValue);
        
        assertThat(phoneNumber.toString()).isEqualTo(phoneNumberValue);
    }
    
    @Test
    void shouldHandleEqualsWithNullAndDifferentTypes() {
        PhoneNumber phoneNumber = new PhoneNumber("1234567890");
        
        assertThat(phoneNumber).isNotEqualTo(null);
        assertThat(phoneNumber).isNotEqualTo("1234567890");
        assertThat(phoneNumber).isEqualTo(phoneNumber);
    }
    
    @Test
    void shouldHandleSingleDigitPhoneNumber() {
        String singleDigit = "5";
        
        PhoneNumber phoneNumber = new PhoneNumber(singleDigit);
        
        assertThat(phoneNumber.value()).isEqualTo("5");
    }
    
    @Test
    void shouldHandleMaximumLengthPhoneNumber() {
        String maxLengthNumber = "123456789012345";
        
        PhoneNumber phoneNumber = new PhoneNumber(maxLengthNumber);
        
        assertThat(phoneNumber.value()).isEqualTo(maxLengthNumber);
    }
    
    @Test
    void shouldHandlePhoneNumberStartingWithZero() {
        String phoneStartingWithZero = "0123456789";
        
        PhoneNumber phoneNumber = new PhoneNumber(phoneStartingWithZero);
        
        assertThat(phoneNumber.value()).isEqualTo("0123456789");
    }
}