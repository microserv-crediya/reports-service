package co.com.jhompo.model.approvedcount.gateways;

import co.com.jhompo.model.approvedcount.ApprovedCount;
import reactor.core.publisher.Mono;

public interface ApprovedCountRepository {
    Mono<ApprovedCount> getCount();
    Mono<ApprovedCount> incrementCount();
}
