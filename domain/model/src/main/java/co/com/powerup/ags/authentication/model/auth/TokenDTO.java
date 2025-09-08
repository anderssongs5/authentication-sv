package co.com.powerup.ags.authentication.model.auth;

public record TokenDTO(
        String token,
        String tokenType,
        long expiresIn
) {
    public TokenDTO {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        if (tokenType == null || tokenType.trim().isEmpty()) {
            throw new IllegalArgumentException("Token type cannot be null or empty");
        }
        if (expiresIn <= 0) {
            throw new IllegalArgumentException("Expires in must be positive");
        }
    }
}