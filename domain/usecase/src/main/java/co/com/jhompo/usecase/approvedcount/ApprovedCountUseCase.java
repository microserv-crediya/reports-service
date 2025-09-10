package co.com.jhompo.usecase.approvedcount;

import co.com.jhompo.model.approvedcount.ApprovedCount;
import co.com.jhompo.model.approvedcount.gateways.ApprovedCountRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class ApprovedCountUseCase {
    private final ApprovedCountRepository approvedCountRepository;

    public Mono<ApprovedCount> get() {
        return approvedCountRepository.getCount();
    }

    public Mono<ApprovedCount> update(BigDecimal amount) {
        return approvedCountRepository.updateReport(amount);
    }
}
