package co.com.jhompo.dynamodb;

import co.com.jhompo.dynamodb.helper.TemplateAdapterOperations;
import co.com.jhompo.model.approvedcount.ApprovedCount;
import co.com.jhompo.model.approvedcount.gateways.ApprovedCountRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.Duration;
import java.util.List;

@Slf4j
@Repository
public class ApprovedCountAdapter
        extends TemplateAdapterOperations<ApprovedCount, String, ApprovedCountEntity>
        implements ApprovedCountRepository {

    private static final String TABLE_NAME = "approved_counts";
    private static final String COUNTER_ID = "reports-counter";

    public ApprovedCountAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {

        super(connectionFactory, mapper, d ->  mapper.map(d, ApprovedCount.class), TABLE_NAME);
    }


    private QueryEnhancedRequest generateQueryExpression(String partitionKey, String sortKey) {
        return QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(partitionKey).build()))
                .queryConditional(QueryConditional.sortGreaterThanOrEqualTo(Key.builder().sortValue(sortKey).build()))
                .build();
    }

    @Override
    public Mono<ApprovedCount> getCount() {
        return this.getById(COUNTER_ID)
                .switchIfEmpty(initializeCounter())
                .doOnNext(count -> log.debug("Current count: {}", count.getCount()));
    }

    @Override
    public Mono<ApprovedCount> incrementCount() {
        return this.getCount()
                .flatMap(currentCount -> {
                    ApprovedCount newCount = ApprovedCount.builder()
                            .id(COUNTER_ID)
                            .count(currentCount.getCount() + 1)
                            .build();

                    return this.save(newCount);
                })
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
                        .filter(throwable -> throwable instanceof ConditionalCheckFailedException))
                .doOnSuccess(updated -> log.info("Counter incremented to: {}", updated.getCount()))
                .doOnError(error -> log.error("Error incrementing counter", error));
    }



    public Mono<ApprovedCount> incrementCount2() {
        return this.getCount()
                .flatMap(currentCount -> {
                    ApprovedCount newCount = new ApprovedCount(COUNTER_ID, currentCount.getCount() + 1);
                    return this.save(newCount);
                });
    }

    private Mono<ApprovedCount> initializeCounter() {
        ApprovedCount initialCount = ApprovedCount.builder()
                .id(COUNTER_ID)
                .count(0L)
                .build();

        return this.save(initialCount)
                .doOnSuccess(saved -> log.info("Counter initialized with value: 0"))
                .onErrorResume(error -> {
                    log.warn("Counter already exists, fetching current value");
                    return this.getById(COUNTER_ID);
                });
    }
}
