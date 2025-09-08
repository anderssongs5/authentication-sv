package co.com.powerup.ags.authentication.model.user;

import co.com.powerup.ags.authentication.model.role.Role;
import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.Password;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EnrichedUser(String id, String name, String lastName, String address, PhoneNumber phoneNumber,
                           LocalDate birthDate, Email email, BigDecimal baseSalary, String idNumber, Password password,
                           Role role) {
}
