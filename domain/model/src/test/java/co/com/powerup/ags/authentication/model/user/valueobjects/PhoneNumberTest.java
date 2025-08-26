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
        // When
        PhoneNumber phoneNumber = new PhoneNumber(validPhoneNumber);
        
        // Then
        assertThat(phoneNumber.value()).isEqualTo(validPhoneNumber);
    }
    
    @Test
    void shouldTrimWhitespaceFromPhoneNumber() {
        // Given
        String phoneNumberWithSpaces = "  1234567890  ";
        
        // When
        PhoneNumber phoneNumber = new PhoneNumber(phoneNumberWithSpaces);
        
        // Then
        assertThat(phoneNumber.value()).isEqualTo("1234567890");
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\n"})
    void shouldThrowExceptionForNullOrEmptyPhoneNumbers(String invalidPhoneNumber) {
        // When & Then
        System.out.println("invalidPhoneNumber: " + invalidPhoneNumber);
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
        // When & Then
        assertThatThrownBy(() -> new PhoneNumber(invalidPhoneNumber))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Phone number must contain only numbers: " + invalidPhoneNumber);
    }
    
    @Test
    void shouldBeEqualWhenPhoneNumberValuesAreTheSame() {
        // Given
        PhoneNumber phoneNumber1 = new PhoneNumber("1234567890");
        PhoneNumber phoneNumber2 = new PhoneNumber("1234567890");
        
        // When & Then
        assertThat(phoneNumber1).isEqualTo(phoneNumber2);
        assertThat(phoneNumber1.hashCode()).isEqualTo(phoneNumber2.hashCode());
    }
    
    @Test
    void shouldBeEqualWhenTrimmedValuesAreTheSame() {
        // Given
        PhoneNumber phoneNumber1 = new PhoneNumber("1234567890");
        PhoneNumber phoneNumber2 = new PhoneNumber("  1234567890  ");
        
        // When & Then
        assertThat(phoneNumber1).isEqualTo(phoneNumber2);
        assertThat(phoneNumber1.hashCode()).isEqualTo(phoneNumber2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenPhoneNumberValuesAreDifferent() {
        // Given
        PhoneNumber phoneNumber1 = new PhoneNumber("1234567890");
        PhoneNumber phoneNumber2 = new PhoneNumber("9876543210");
        
        // When & Then
        assertThat(phoneNumber1).isNotEqualTo(phoneNumber2);
    }
    
    @Test
    void shouldReturnPhoneNumberValueAsString() {
        // Given
        String phoneNumberValue = "1234567890";
        PhoneNumber phoneNumber = new PhoneNumber(phoneNumberValue);
        
        // When & Then
        assertThat(phoneNumber.toString()).isEqualTo(phoneNumberValue);
    }
    
    @Test
    void shouldHandleEqualsWithNullAndDifferentTypes() {
        // Given
        PhoneNumber phoneNumber = new PhoneNumber("1234567890");
        
        // When & Then
        assertThat(phoneNumber).isNotEqualTo(null);
        assertThat(phoneNumber).isNotEqualTo("1234567890");
        assertThat(phoneNumber).isEqualTo(phoneNumber);
    }
    
    @Test
    void shouldHandleSingleDigitPhoneNumber() {
        // Given
        String singleDigit = "5";
        
        // When
        PhoneNumber phoneNumber = new PhoneNumber(singleDigit);
        
        // Then
        assertThat(phoneNumber.value()).isEqualTo("5");
    }
    
    @Test
    void shouldHandleMaximumLengthPhoneNumber() {
        // Given
        String maxLengthNumber = "123456789012345"; // 15 digits
        
        // When
        PhoneNumber phoneNumber = new PhoneNumber(maxLengthNumber);
        
        // Then
        assertThat(phoneNumber.value()).isEqualTo(maxLengthNumber);
    }
    
    @Test
    void shouldHandlePhoneNumberStartingWithZero() {
        // Given
        String phoneStartingWithZero = "0123456789";
        
        // When
        PhoneNumber phoneNumber = new PhoneNumber(phoneStartingWithZero);
        
        // Then
        assertThat(phoneNumber.value()).isEqualTo("0123456789");
    }
}