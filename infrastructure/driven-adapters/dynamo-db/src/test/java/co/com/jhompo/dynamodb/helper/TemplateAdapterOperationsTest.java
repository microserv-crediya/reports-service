package co.com.jhompo.dynamodb.helper;

import co.com.jhompo.dynamodb.ApprovedAdapter;
import co.com.jhompo.dynamodb.ApprovedEntity;
import co.com.jhompo.model.approved.Approved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.*;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemplateAdapterOperationsTest {

    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @Mock
    private DynamoDbAsyncTable<ApprovedEntity> customerTable;

    @Mock
    private ObjectMapper mapper;

    private ApprovedAdapter approvedAdapter;
    private ApprovedEntity approvedEntity;

    @BeforeEach
    void setUp() {
        approvedEntity = new ApprovedEntity();
        approvedEntity.setId("id");

        when(dynamoDbEnhancedAsyncClient.table(anyString(), any(TableSchema.class)))
                .thenReturn(customerTable);

        approvedAdapter = new ApprovedAdapter(dynamoDbEnhancedAsyncClient, mapper);
    }

    @Test
    void testGetById() {
        Approved approved = new Approved();
        approved.setId("id");

        when(customerTable.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(approvedEntity));

        // Usar any() en lugar de eq() para que el mock funcione
        when(mapper.map(any(ApprovedEntity.class), any(Class.class))).thenReturn(approved);

        StepVerifier.create(approvedAdapter.getById("id"))
                .expectNextMatches(entity -> "id".equals(entity.getId()))
                .verifyComplete();
    }

    @Test
    void testSave() {
        Approved approved = new Approved();
        approved.setId("id");

        when(customerTable.putItem(any(ApprovedEntity.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // El que se usa realmente
        when(mapper.map(any(Approved.class), any(Class.class))).thenReturn(approvedEntity);
        // El que no se usa - marcarlo como lenient
        lenient().when(mapper.map(any(ApprovedEntity.class), any(Class.class))).thenReturn(approved);

        StepVerifier.create(approvedAdapter.save(approved))
                .expectNextMatches(result -> "id".equals(result.getId()))
                .verifyComplete();
    }

    @Test
    void testGetByIdWithAllFields() {
        // Crear ApprovedEntity con todos los campos
        ApprovedEntity entityFromDb = ApprovedEntity.builder()
                .id("test-id")
                .count(5L)
                .totalAmount(BigDecimal.valueOf(100.50))
                .build();

        // Crear Approved esperado con los mismos datos
        Approved expectedApproved = new Approved();
        expectedApproved.setId("test-id");
        expectedApproved.setCount(5L);
        expectedApproved.setTotalAmount(BigDecimal.valueOf(100.50));

        when(customerTable.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(entityFromDb));

        when(mapper.map(any(ApprovedEntity.class), any(Class.class))).thenReturn(expectedApproved);

        StepVerifier.create(approvedAdapter.getById("test-id"))
                .expectNextMatches(result ->
                        "test-id".equals(result.getId()) &&
                                result.getCount().equals(5L) &&
                                result.getTotalAmount().compareTo(BigDecimal.valueOf(100.50)) == 0
                )
                .verifyComplete();
    }

    @Test
    void testSaveWithAllFields() {
        // Crear objeto Approved de entrada con todos los campos
        Approved inputApproved = new Approved();
        inputApproved.setId("test-id");
        inputApproved.setCount(5L);
        inputApproved.setTotalAmount(BigDecimal.valueOf(100.50));

        // Crear ApprovedEntity que devuelve el mapper
        ApprovedEntity mappedEntity = ApprovedEntity.builder()
                .id("test-id")
                .count(5L)
                .totalAmount(BigDecimal.valueOf(100.50))
                .build();

        when(customerTable.putItem(any(ApprovedEntity.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        when(mapper.map(any(Approved.class), any(Class.class))).thenReturn(mappedEntity);
        lenient().when(mapper.map(any(ApprovedEntity.class), any(Class.class))).thenReturn(inputApproved);

        StepVerifier.create(approvedAdapter.save(inputApproved))
                .expectNextMatches(result ->
                        "test-id".equals(result.getId()) &&
                                result.getCount().equals(5L) &&
                                result.getTotalAmount().compareTo(BigDecimal.valueOf(100.50)) == 0
                )
                .verifyComplete();
    }

    @Test
    void testDelete() {
        // Crear objetos de prueba
        Approved approved = new Approved();
        approved.setId("test-id");
        approved.setCount(5L);
        approved.setTotalAmount(BigDecimal.valueOf(100.50));

        ApprovedEntity entityToDelete = ApprovedEntity.builder()
                .id("test-id")
                .count(5L)
                .totalAmount(BigDecimal.valueOf(100.50))
                .build();

        ApprovedEntity deletedEntity = ApprovedEntity.builder()
                .id("test-id")
                .count(5L)
                .totalAmount(BigDecimal.valueOf(100.50))
                .build();

        // Configurar mocks
        when(customerTable.deleteItem(any(ApprovedEntity.class)))
                .thenReturn(CompletableFuture.completedFuture(deletedEntity));

        when(mapper.map(any(Approved.class), any(Class.class))).thenReturn(entityToDelete);
        when(mapper.map(any(ApprovedEntity.class), any(Class.class))).thenReturn(approved);

        // Ejecutar y verificar
        StepVerifier.create(approvedAdapter.delete(approved))
                .expectNextMatches(result ->
                        "test-id".equals(result.getId()) &&
                                result.getCount().equals(5L) &&
                                result.getTotalAmount().compareTo(BigDecimal.valueOf(100.50)) == 0
                )
                .verifyComplete();
    }



}