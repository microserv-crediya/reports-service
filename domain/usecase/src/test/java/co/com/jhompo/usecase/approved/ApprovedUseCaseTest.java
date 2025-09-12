package co.com.jhompo.usecase.approved;

import co.com.jhompo.model.approved.Approved;
import co.com.jhompo.model.approved.gateways.ApprovedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApprovedUseCase Tests (WebFlux)")
class ApprovedUseCaseTest {

    @Mock
    private ApprovedRepository approvedRepository;

    @InjectMocks
    private ApprovedUseCase approvedUseCase;

    private Approved approved;

    @BeforeEach
    void setUp() {
        approved = Approved.builder()
                .id("reports-counter")
                .count(100L)
                .totalAmount(BigDecimal.valueOf(1500.75))
                .build();
    }

    @Test
    @DisplayName("Should get approved successfully")
    void shouldGetApprovedSuccessfully() {
        when(approvedRepository.getCount()).thenReturn(Mono.just(approved));

        StepVerifier.create(approvedUseCase.get())
                .assertNext(result -> {
                    assertThat(result.getId()).isEqualTo("reports-counter");
                    assertThat(result.getCount()).isEqualTo(100L);
                    assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.valueOf(1500.75));
                })
                .verifyComplete();

        verify(approvedRepository).getCount();
    }

    @Test
    @DisplayName("Should handle empty result when getting approved")
    void shouldHandleEmptyResultWhenGettingApproved() {
        when(approvedRepository.getCount()).thenReturn(Mono.empty());

        StepVerifier.create(approvedUseCase.get())
                .verifyComplete();

        verify(approvedRepository).getCount();
    }

    @Test
    @DisplayName("Should handle error when getting approved")
    void shouldHandleErrorWhenGettingApproved() {
        when(approvedRepository.getCount()).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(approvedUseCase.get())
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Database error"))
                .verify();

        verify(approvedRepository).getCount();
    }

    @Test
    @DisplayName("Should update approved successfully")
    void shouldUpdateApprovedSuccessfully() {
        BigDecimal amount = BigDecimal.valueOf(250.50);
        Approved updatedApproved = Approved.builder()
                .id("reports-counter")
                .count(101L)
                .totalAmount(amount)
                .build();

        when(approvedRepository.updateReport(amount)).thenReturn(Mono.just(updatedApproved));

        StepVerifier.create(approvedUseCase.update(amount))
                .expectNext(updatedApproved)
                .verifyComplete();

        verify(approvedRepository).updateReport(amount);
    }

    @Test
    @DisplayName("Should handle empty result when updating")
    void shouldHandleEmptyResultWhenUpdating() {
        BigDecimal amount = BigDecimal.valueOf(100);
        when(approvedRepository.updateReport(amount)).thenReturn(Mono.empty());

        StepVerifier.create(approvedUseCase.update(amount))
                .verifyComplete();

        verify(approvedRepository).updateReport(amount);
    }

    @Test
    @DisplayName("Should handle error when updating")
    void shouldHandleErrorWhenUpdating() {
        BigDecimal amount = BigDecimal.valueOf(100);
        when(approvedRepository.updateReport(amount))
                .thenReturn(Mono.error(new RuntimeException("Update failed")));

        StepVerifier.create(approvedUseCase.update(amount))
                .expectErrorMessage("Update failed")
                .verify();

        verify(approvedRepository).updateReport(amount);
    }
}
