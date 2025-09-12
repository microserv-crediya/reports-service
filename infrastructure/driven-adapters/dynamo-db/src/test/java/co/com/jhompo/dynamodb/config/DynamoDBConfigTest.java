package co.com.jhompo.dynamodb.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DynamoDBConfigTest {

    @Mock
    private MetricPublisher publisher;

    @Mock
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    private final DynamoDBConfig dynamoDBConfig = new DynamoDBConfig();

    @Test
    void testAmazonDynamoDB() {
        DynamoDbAsyncClient result = dynamoDBConfig.amazonDynamoDB(
                "http://aws.dynamo.test",
                "us-east-1",
                publisher);

        assertNotNull(result);
        assertEquals("us-east-1", result.serviceClientConfiguration().region().id());
        assertEquals("http://aws.dynamo.test", result.serviceClientConfiguration().endpointOverride().get().toString());
    }

    @Test
    void testAmazonDynamoDBAsync() {
        DynamoDbAsyncClient result = dynamoDBConfig.amazonDynamoDBAsync(
                publisher,
                "us-west-2");

        assertNotNull(result);
        assertEquals("us-west-2", result.serviceClientConfiguration().region().id());
    }

    @Test
    void testGetDynamoDbEnhancedAsyncClient() {
        DynamoDbEnhancedAsyncClient result = dynamoDBConfig.getDynamoDbEnhancedAsyncClient(dynamoDbAsyncClient);

        assertNotNull(result);
    }


    /*
    @Test
    void testAmazonDynamoDBWithNullPublisher() {
        DynamoDbAsyncClient result = dynamoDBConfig.amazonDynamoDB(
                "http://aws.dynamo.local",
                "eu-central-1",
                null);

        assertNotNull(result);
        assertEquals("eu-central-1", result.serviceClientConfiguration().region().id());
    }

    @Test
    void testAmazonDynamoDBAsyncWithNullPublisher() {
        DynamoDbAsyncClient result = dynamoDBConfig.amazonDynamoDBAsync(
                null,
                "ap-south-1");

        assertNotNull(result);
        assertEquals("ap-south-1", result.serviceClientConfiguration().region().id());
    }*/
}
