package co.com.powerup.ags.authentication.usecase.user.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateUserCommand(String id, String name, String lastName, String address, String phoneNumber,
                                LocalDate birthDate, String email, BigDecimal baseSalary, String idNumber) {
    
}