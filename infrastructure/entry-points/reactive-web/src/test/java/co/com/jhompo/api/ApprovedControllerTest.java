package co.com.jhompo.api;

import co.com.jhompo.api.dtos.ApprovedDTO;
import co.com.jhompo.model.approved.Approved;
import co.com.jhompo.usecase.approved.ApprovedUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias del ApprovedController")
class ApprovedControllerTest {

    @InjectMocks
    private ApprovedController approvedController;

    @Mock
    private ApprovedUseCase approvedUseCase;

    @Mock
    private ModelMapper modelMapper;

    private Approved approved;
    private ApprovedDTO approvedDTO;

    @BeforeEach
    void setUp() {
        approved = Approved.builder()
                .id("reports-counter")
                .count(150L)
                .totalAmount(BigDecimal.valueOf(2500.75))
                .build();

        approvedDTO = ApprovedDTO.builder()
                .id("reports-counter")
                .count(150L)
                .totalAmount(BigDecimal.valueOf(2500.75))
                .build();

        lenient().when(modelMapper.map(any(Approved.class), eq(ApprovedDTO.class)))
                .thenAnswer(invocation -> {
                    Approved src = invocation.getArgument(0);
                    return ApprovedDTO.builder()
                            .id(src.getId())
                            .count(src.getCount())
                            .totalAmount(src.getTotalAmount())
                            .build();
                });
    }

    @Test
    @DisplayName("Debe devolver ApprovedDTO correctamente cuando existe información")
    void shouldReturnApprovedSuccessfully() {
        when(approvedUseCase.get()).thenReturn(Mono.just(approved));

        StepVerifier.create(approvedController.getApproved())
                .expectNextMatches(dto ->
                        dto.getId().equals("reports-counter")
                                && dto.getCount().equals(150L)
                                && dto.getTotalAmount().equals(BigDecimal.valueOf(2500.75))
                )
                .verifyComplete();

        verify(approvedUseCase, times(1)).get();
    }

    @Test
    @DisplayName("Debe devolver vacío cuando no existe información")
    void shouldReturnEmptyWhenNoData() {
        when(approvedUseCase.get()).thenReturn(Mono.empty());

        StepVerifier.create(approvedController.getApproved())
                .verifyComplete();

        verify(approvedUseCase, times(1)).get();
    }

    @Test
    @DisplayName("Debe manejar valores nulos en Approved sin lanzar excepción")
    void shouldHandleNullValuesGracefully() {
        Approved problematicApproved = Approved.builder()
                .id(null)
                .count(null)
                .totalAmount(null)
                .build();

        when(approvedUseCase.get()).thenReturn(Mono.just(problematicApproved));

        StepVerifier.create(approvedController.getApproved())
                .expectNextMatches(dto ->
                        dto.getId() == null &&
                                dto.getCount() == null &&
                                dto.getTotalAmount() == null
                )
                .verifyComplete();

        verify(approvedUseCase, times(1)).get();
    }

    @Test
    @DisplayName("Debe propagar errores del caso de uso")
    void shouldPropagateErrorFromUseCase() {
        when(approvedUseCase.get()).thenReturn(Mono.error(new RuntimeException("Error en DB")));

        StepVerifier.create(approvedController.getApproved())
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Error en DB")
                )
                .verify();

        verify(approvedUseCase, times(1)).get();
    }

    @Test
    @DisplayName("Debe mapear correctamente Approved a ApprovedDTO usando ModelMapper")
    void shouldMapApprovedToApprovedDTO() {
        Approved customApproved = Approved.builder()
                .id("custom-id")
                .count(999L)
                .totalAmount(BigDecimal.TEN)
                .build();

        ApprovedDTO expectedDTO = ApprovedDTO.builder()
                .id("custom-id")
                .count(999L)
                .totalAmount(BigDecimal.TEN)
                .build();

        when(approvedUseCase.get()).thenReturn(Mono.just(customApproved));
        when(modelMapper.map(customApproved, ApprovedDTO.class)).thenReturn(expectedDTO);

        StepVerifier.create(approvedController.getApproved())
                .expectNextMatches(dto ->
                        dto.getId().equals("custom-id") &&
                                dto.getCount().equals(999L) &&
                                dto.getTotalAmount().equals(BigDecimal.TEN)
                )
                .verifyComplete();

        verify(approvedUseCase, times(1)).get();
        verify(modelMapper, times(1)).map(customApproved, ApprovedDTO.class);
    }
}
