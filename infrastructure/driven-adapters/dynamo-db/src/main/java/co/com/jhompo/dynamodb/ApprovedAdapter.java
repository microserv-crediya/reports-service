package co.com.jhompo.dynamodb;

import co.com.jhompo.dynamodb.helper.TemplateAdapterOperations;
import co.com.jhompo.model.approved.Approved;
import co.com.jhompo.model.approved.gateways.ApprovedRepository;
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

import java.math.BigDecimal;
import java.time.Duration;

@Slf4j
@Repository
public class ApprovedAdapter
        extends TemplateAdapterOperations<Approved, String, ApprovedEntity>
        implements ApprovedRepository {

    private static final String TABLE_NAME = "approved_counts";
    private static final String COUNTER_ID = "reports-counter";

    public ApprovedAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {

        super(connectionFactory, mapper, d ->  mapper.map(d, Approved.class), TABLE_NAME);
    }


    @Override
    public Mono<Approved> getCount() {
        return this.getById(COUNTER_ID)
                .switchIfEmpty(Mono.defer(this::initializeCounter)) // solo crea 1 vez
                .doOnNext(count -> log.debug("Current count: {}", count.getCount()));
    }

    private Mono<Approved> initializeCounter() {
        Approved initialCount = Approved.builder()
                .id(COUNTER_ID)
                .count(0L)
                .totalAmount(BigDecimal.ZERO)
                .build();

        log.info("Initializing counter for the first time: {}", initialCount);
        return this.save(initialCount)
                .doOnSuccess(saved -> log.info("Counter initialized with value: 0"))
                .onErrorResume(error -> {
                    log.warn("Counter already exists, fetching current value");
                    return this.getById(COUNTER_ID);
                });
    }

    @Override
    public Mono<Approved> updateReport(BigDecimal amount) {
        return this.getCount()
                .flatMap(currentCount -> {
                    BigDecimal currentTotal = currentCount.getTotalAmount() != null
                            ? currentCount.getTotalAmount()
                            : BigDecimal.ZERO;

                    BigDecimal newAmount = amount != null ? amount : BigDecimal.ZERO;

                    Approved newCount = Approved.builder()
                            .id(COUNTER_ID)
                            .count(currentCount.getCount() + 1)
                            .totalAmount(currentTotal.add(newAmount))
                            .build();

                    return this.save(newCount);
                })
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
                        .filter(throwable -> throwable instanceof ConditionalCheckFailedException))
                .doOnSuccess(updated ->
                        log.info("Counter incremented to: {}, totalAmount: {}",  updated.getCount(), updated.getTotalAmount()))
                .doOnError(error -> log.error("Error incrementing counter", error));
    }




}
