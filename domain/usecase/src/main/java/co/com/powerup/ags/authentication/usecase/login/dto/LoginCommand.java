package co.com.powerup.ags.authentication.usecase.login.dto;

import co.com.powerup.ags.authentication.model.user.valueobjects.Email;

public record LoginCommand(
        Email email,
        String password
) {
    public LoginCommand {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }
}