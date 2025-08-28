package co.com.powerup.ags.authentication.api.config;

import co.com.powerup.ags.authentication.api.HandlerV1;
import co.com.powerup.ags.authentication.api.RouterRest;
import co.com.powerup.ags.authentication.usecase.user.UserUseCase;
import co.com.powerup.ags.authentication.usecase.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDate;

@ContextConfiguration(classes = {RouterRest.class, HandlerV1.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;
    
    @MockitoBean
    private UserUseCase userUseCase;

    @Test
    void corsConfigurationShouldAllowOrigins() {
        Mockito.when(userUseCase.getAllUsers()).thenReturn(Flux.just(new UserResponse("", "",
                "", "", "", LocalDate.now(), "", BigDecimal.ONE)));
        
        webTestClient.get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}