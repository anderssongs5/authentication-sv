package co.com.powerup.ags.authentication.model.user;

import co.com.powerup.autenticacion.model.user.valueobjects.Email;
import co.com.powerup.autenticacion.model.user.valueobjects.PhoneNumber;

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
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "El número de teléfono no puede ser nulo");
        this.birthDate = validateBirthDate(birthDate);
        this.email = Objects.requireNonNull(email, "El correo electrónico no puede ser nulo");
        this.baseSalary = validateBaseSalary(baseSalary);
    }
    
    /*public static User create(String name, String lastName, String address,
                             PhoneNumber phoneNumber, LocalDate birthDate, Email email, BigDecimal baseSalary) {
        return new User(null, name, lastName, address, phoneNumber, birthDate, email, baseSalary);
    }*/
    
    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }
        return name;
    }
    
    private String validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede ser nulo o vacío");
        }
        return lastName;
    }
    
    private LocalDate validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser nula");
        }
        if (birthDate.isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("El usuario debe tener al menos 18 años");
        }
        return birthDate;
    }
    
    private BigDecimal validateBaseSalary(BigDecimal baseSalary) {
        if (baseSalary == null) {
            throw new IllegalArgumentException("El salario base no puede ser nulo");
        }
        if (baseSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El salario base no puede ser negativo");
        }
        if (baseSalary.compareTo(new BigDecimal("15000000")) > 0) {
            throw new IllegalArgumentException("El salario base no puede ser mayor a 15,000,000");
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
