package co.com.jhompo.model.approved.gateways;

import co.com.jhompo.model.approved.Approved;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ApprovedRepository {
    Mono<Approved> getCount();
    Mono<Approved> updateReport(BigDecimal amount);
}
