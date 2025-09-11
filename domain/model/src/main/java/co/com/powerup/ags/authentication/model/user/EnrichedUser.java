package co.com.powerup.ags.authentication.model.user;

import co.com.powerup.ags.authentication.model.role.Role;
import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.Password;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EnrichedUser {
    
    private final String id;
    private final String name;
    private final String lastName;
    private final String address;
    private final PhoneNumber phoneNumber;
    private final LocalDate birthDate;
    private final Email email;
    private final BigDecimal baseSalary;
    private final String idNumber;
    private final Password password;
    private final Role role;
    
    public EnrichedUser(User user, Role role) {
        this.id = user.id();
        this.name = user.name();
        this.lastName = user.lastName();
        this.address = user.address();
        this.phoneNumber = user.phoneNumber();
        this.birthDate = user.birthDate();
        this.email = user.email();
        this.baseSalary = user.baseSalary();
        this.idNumber = user.idNumber();
        this.password = user.password();
        this.role = role;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public Email getEmail() {
        return email;
    }
    
    public BigDecimal getBaseSalary() {
        return baseSalary;
    }
    
    public String getIdNumber() {
        return idNumber;
    }
    
    public Password getPassword() {
        return password;
    }
    
    public Role getRole() {
        return role;
    }
}
