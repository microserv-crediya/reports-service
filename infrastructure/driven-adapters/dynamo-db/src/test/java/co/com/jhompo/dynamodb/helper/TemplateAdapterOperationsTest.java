package co.com.jhompo.dynamodb.helper;

import co.com.jhompo.dynamodb.ApprovedCountAdapter;
import co.com.jhompo.dynamodb.ApprovedCountEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class TemplateAdapterOperationsTest {

    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private DynamoDbAsyncTable<ApprovedCountEntity> customerTable;

    private ApprovedCountEntity approvedCountEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(dynamoDbEnhancedAsyncClient.table("table_name", TableSchema.fromBean(ApprovedCountEntity.class)))
                .thenReturn(customerTable);

        approvedCountEntity = new ApprovedCountEntity();
        approvedCountEntity.setId("id");
        approvedCountEntity.setCount("count");
    }

    @Test
    void modelEntityPropertiesMustNotBeNull() {
        ApprovedCountEntity approvedCountEntityUnderTest = new ApprovedCountEntity("id", "count");

        assertNotNull(approvedCountEntityUnderTest.getId());
        assertNotNull(approvedCountEntityUnderTest.getCount());
    }

 /*   @Test
    void testSave() {
        when(customerTable.putItem(approvedCountEntity)).thenReturn(CompletableFuture.runAsync(()->{}));
        when(mapper.map(approvedCountEntity, ApprovedCountEntity.class)).thenReturn(approvedCountEntity);

        ApprovedCountAdapter approvedCountAdapter =
                new ApprovedCountAdapter(dynamoDbEnhancedAsyncClient, mapper);

        StepVerifier.create(approvedCountAdapter.save(approvedCountEntity))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testGetById() {
        String id = "id";

        when(customerTable.getItem(
                Key.builder().partitionValue(AttributeValue.builder().s(id).build()).build()))
                .thenReturn(CompletableFuture.completedFuture(approvedCountEntity));
        when(mapper.map(approvedCountEntity, Object.class)).thenReturn("value");

        ApprovedCountAdapter approvedCountAdapter =
                new ApprovedCountAdapter(dynamoDbEnhancedAsyncClient, mapper);

        StepVerifier.create(approvedCountAdapter.getById("id"))
                .expectNext("value")
                .verifyComplete();
    }*/

   /*  @Test
   void testDelete() {
        when(mapper.map(approvedCountEntity, ApprovedCountEntity.class)).thenReturn(approvedCountEntity);
        when(mapper.map(approvedCountEntity, Object.class)).thenReturn("value");

        when(customerTable.deleteItem(approvedCountEntity))
                .thenReturn(CompletableFuture.completedFuture(approvedCountEntity));

        ApprovedCountAdapter approvedCountAdapter =
                new ApprovedCountAdapter(dynamoDbEnhancedAsyncClient, mapper);

        StepVerifier.create(approvedCountAdapter.delete(approvedCountEntity))
                .expectNext("value")
                .verifyComplete();
    }*/
}