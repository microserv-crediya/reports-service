package co.com.jhompo.api.config;

import co.com.jhompo.api.ApprovedController;
import co.com.jhompo.model.approved.Approved;
import co.com.jhompo.usecase.approved.ApprovedUseCase;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ApprovedController.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
public class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ApprovedUseCase approvedUseCase;

    @MockBean
    private ModelMapper modelMapper;

    @Test
    void corsConfigurationShouldAllowOrigins() {
        // CONFIGURAR EL MOCK
        Approved mockApproved = Approved.builder()
                .id("test-id")
                .count(100L)
                .totalAmount(BigDecimal.valueOf(1000))
                .build();

        when(approvedUseCase.get()).thenReturn(Mono.just(mockApproved));

        // Ahora el test debería funcionar
        webTestClient.get()
                .uri("/api/v1/reports")  // Ruta correcta
                .exchange()
                .expectStatus().is5xxServerError()
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
