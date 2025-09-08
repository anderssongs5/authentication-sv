package co.com.powerup.ags.authentication.securitycrypto.token;

import co.com.powerup.ags.authentication.model.user.EnrichedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.Logger;

public class JwtProvider {
    
    private static final Logger LOGGER =  Logger.getLogger(JwtProvider.class.getName());
    
    private JwtProvider() {
        super();
    }
    
    public static String generateToken(EnrichedUser user, Long expiration, String secret) {
        Instant currentInstant = Instant.now();
        var expirationInstant = currentInstant.plus(expiration, ChronoUnit.SECONDS);

        return Jwts.builder()
                .subject(user.email().value())
                .claim("role", user.role().getName())
                .issuedAt(Date.from(currentInstant))
                .expiration(Date.from(expirationInstant))
                .signWith(getKey(secret))
                .compact();
    }

    public static Claims getClaims(String token, String secret) {
        return Jwts.parser()
                .verifyWith(getKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    public static String getSubject(String token, String secret) {
        return Jwts.parser()
                .verifyWith(getKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    
    public static boolean validate(String token, String secret){
        try {
            Jwts.parser()
                    .verifyWith(getKey(secret))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.severe("token expired");
        } catch (UnsupportedJwtException e) {
            LOGGER.severe("token unsupported");
        } catch (MalformedJwtException e) {
            LOGGER.severe("token malformed");
        } catch (IllegalArgumentException e) {
            LOGGER.severe("illegal args");
        }
        return false;
    }
    
    private static SecretKey getKey(String secret) {
        byte[] secretBytes = Decoders.BASE64URL.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }
}
