package co.com.jhompo.model.approvedcount.gateways;

import co.com.jhompo.model.approvedcount.ApprovedCount;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ApprovedCountRepository {
    Mono<ApprovedCount> getCount();
    Mono<ApprovedCount> updateReport(BigDecimal amount);
}
