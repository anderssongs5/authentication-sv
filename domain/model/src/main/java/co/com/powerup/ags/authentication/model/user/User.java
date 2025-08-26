package co.com.powerup.ags.authentication.model.user;

import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public record User(String id, String name, String lastName, String address, PhoneNumber phoneNumber,
                   LocalDate birthDate, Email email, BigDecimal baseSalary) {
    
    public User(String id, String name, String lastName, String address,
                PhoneNumber phoneNumber, LocalDate birthDate, Email email, BigDecimal baseSalary) {
        this.id = id;
        this.name = validateName(name);
        this.lastName = validateLastName(lastName);
        this.address = address;
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
        this.birthDate = validateBirthDate(birthDate);
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.baseSalary = validateBaseSalary(baseSalary);
    }
    
    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return name;
    }
    
    private String validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        return lastName;
    }
    
    private LocalDate validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }
        if (birthDate.isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("User must be at least 18 years old");
        }
        return birthDate;
    }
    
    private BigDecimal validateBaseSalary(BigDecimal baseSalary) {
        if (baseSalary == null) {
            throw new IllegalArgumentException("Base salary cannot be null");
        }
        if (baseSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Base salary cannot be negative");
        }
        if (baseSalary.compareTo(new BigDecimal("15000000")) > 0) {
            throw new IllegalArgumentException("Base salary cannot be greater than 15,000,000");
        }
        return baseSalary;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    @SuppressWarnings("NullableProblems")
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email=" + email +
                '}';
    }
}
