package co.com.jhompo.dynamodb;

import co.com.jhompo.model.approved.Approved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApprovedAdapter Tests")
class ApprovedAdapterTest {

    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbClient;

    @Mock
    private ObjectMapper objectMapper;

    private ApprovedAdapter approvedAdapter;
    private Approved existingApproved;
    private Approved initialApproved;

    @BeforeEach
    void setUp() {
        approvedAdapter = spy(new ApprovedAdapter(dynamoDbClient, objectMapper));

        existingApproved = Approved.builder()
                .id("reports-counter")
                .count(5L)
                .totalAmount(BigDecimal.valueOf(150.50))
                .build();

        initialApproved = Approved.builder()
                .id("reports-counter")
                .count(0L)
                .totalAmount(BigDecimal.ZERO)
                .build();
    }

    @Test
    @DisplayName("Should get existing count successfully")
    void shouldGetExistingCountSuccessfully() {
        // Arrange
        doReturn(Mono.just(existingApproved))
                .when(approvedAdapter).getById("reports-counter");

        // Act & Assert
        StepVerifier.create(approvedAdapter.getCount())
                .assertNext(result -> {
                    assertThat(result.getId()).isEqualTo("reports-counter");
                    assertThat(result.getCount()).isEqualTo(5L);
                    assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.valueOf(150.50));
                })
                .verifyComplete();

        verify(approvedAdapter).getById("reports-counter");
    }

    @Test
    @DisplayName("Should initialize counter when not exists")
    void shouldInitializeCounterWhenNotExists() {
        // Arrange
        doReturn(Mono.empty())
                .when(approvedAdapter).getById("reports-counter");
        doReturn(Mono.just(initialApproved))
                .when(approvedAdapter).save(any(Approved.class));

        // Act & Assert
        StepVerifier.create(approvedAdapter.getCount())
                .assertNext(result -> {
                    assertThat(result.getId()).isEqualTo("reports-counter");
                    assertThat(result.getCount()).isEqualTo(0L);
                    assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.ZERO);
                })
                .verifyComplete();

        verify(approvedAdapter).getById("reports-counter");
        verify(approvedAdapter).save(argThat(approved ->
                approved.getId().equals("reports-counter") &&
                        approved.getCount().equals(0L) &&
                        approved.getTotalAmount().equals(BigDecimal.ZERO)));
    }

    @Test
    @DisplayName("Should handle error during initialization and fetch existing value")
    void shouldHandleErrorDuringInitializationAndFetchExisting() {
        // Arrange
        doReturn(Mono.empty())
                .doReturn(Mono.just(existingApproved)) // La segunda llamada a getById retornará el objeto
                .when(approvedAdapter).getById("reports-counter");

        // Simula el fallo de la operación de guardado
        doReturn(Mono.error(new RuntimeException("Save failed")))
                .when(approvedAdapter).save(any(Approved.class));

        // Act & Assert
        StepVerifier.create(approvedAdapter.getCount())
                .assertNext(result -> {
                    assertThat(result.getId()).isEqualTo("reports-counter");
                    assertThat(result.getCount()).isEqualTo(5L);
                    assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.valueOf(150.50));
                })
                .verifyComplete();

        // El test verifica que el metodo `getById` se llamó 2 veces y `save` 1 vez.
        verify(approvedAdapter, times(2)).getById("reports-counter");
        verify(approvedAdapter).save(any(Approved.class));
    }

    @Test
    @DisplayName("Should update report successfully")
    void shouldUpdateReportSuccessfully() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(100.25);
        Approved expectedUpdated = Approved.builder()
                .id("reports-counter")
                .count(6L)
                .totalAmount(BigDecimal.valueOf(250.75))
                .build();

        doReturn(Mono.just(existingApproved))
                .when(approvedAdapter).getCount();
        doReturn(Mono.just(expectedUpdated))
                .when(approvedAdapter).save(any(Approved.class));

        // Act & Assert
        StepVerifier.create(approvedAdapter.updateReport(amount))
                .assertNext(result -> {
                    assertThat(result.getCount()).isEqualTo(6L);
                    assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.valueOf(250.75));
                })
                .verifyComplete();

        verify(approvedAdapter).getCount();
        verify(approvedAdapter).save(argThat(approved ->
                approved.getCount().equals(6L) &&
                        approved.getTotalAmount().equals(BigDecimal.valueOf(250.75))));
    }

    @Test
    @DisplayName("Should update report with null amount")
    void shouldUpdateReportWithNullAmount() {
        // Arrange
        Approved expectedUpdated = Approved.builder()
                .id("reports-counter")
                .count(6L)
                .totalAmount(BigDecimal.valueOf(150.50)) // Same as existing since amount is null
                .build();

        doReturn(Mono.just(existingApproved))
                .when(approvedAdapter).getCount();
        doReturn(Mono.just(expectedUpdated))
                .when(approvedAdapter).save(any(Approved.class));

        // Act & Assert
        StepVerifier.create(approvedAdapter.updateReport(null))
                .assertNext(result -> {
                    assertThat(result.getCount()).isEqualTo(6L);
                    assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.valueOf(150.50));
                })
                .verifyComplete();

        verify(approvedAdapter).save(argThat(approved ->
                approved.getTotalAmount().equals(BigDecimal.valueOf(150.50))));
    }

    @Test
    @DisplayName("Should handle null totalAmount in existing record")
    void shouldHandleNullTotalAmountInExistingRecord() {
        // Arrange
        Approved approvedWithNullAmount = Approved.builder()
                .id("reports-counter")
                .count(5L)
                .totalAmount(null)
                .build();

        BigDecimal amount = BigDecimal.valueOf(100.25);
        Approved expectedUpdated = Approved.builder()
                .id("reports-counter")
                .count(6L)
                .totalAmount(BigDecimal.valueOf(100.25)) // Only the new amount
                .build();

        doReturn(Mono.just(approvedWithNullAmount))
                .when(approvedAdapter).getCount();
        doReturn(Mono.just(expectedUpdated))
                .when(approvedAdapter).save(any(Approved.class));

        // Act & Assert
        StepVerifier.create(approvedAdapter.updateReport(amount))
                .assertNext(result -> {
                    assertThat(result.getCount()).isEqualTo(6L);
                    assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.valueOf(100.25));
                })
                .verifyComplete();
    }



    @Test
    @DisplayName("Debería manejar actualización con amount cero")
    void shouldHandleZeroAmountUpdate() {
        // Arrange
        BigDecimal amount = BigDecimal.ZERO;
        Approved expectedUpdated = Approved.builder()
                .id("reports-counter")
                .count(6L)
                .totalAmount(BigDecimal.valueOf(150.50)) // Same as existing since amount is zero
                .build();

        doReturn(Mono.just(existingApproved))
                .when(approvedAdapter).getCount();
        doReturn(Mono.just(expectedUpdated))
                .when(approvedAdapter).save(any(Approved.class));

        // Act & Assert
        StepVerifier.create(approvedAdapter.updateReport(amount))
                .assertNext(result -> {
                    assertThat(result.getCount()).isEqualTo(6L);
                    assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.valueOf(150.50));
                })
                .verifyComplete();
    }
}