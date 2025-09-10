package co.com.powerup.ags.authentication.model.auth;

public record TokenDTO(
        String token,
        String tokenType,
        long expiresIn
) {}