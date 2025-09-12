package co.com.jhompo.usecase.approved;

import co.com.jhompo.model.approved.Approved;
import co.com.jhompo.model.approved.gateways.ApprovedRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class ApprovedUseCase {
    private final ApprovedRepository approvedRepository;

    public Mono<Approved> get() {
        return approvedRepository.getCount();
    }

    public Mono<Approved> update(BigDecimal amount) {
        return approvedRepository.updateReport(amount);
    }
}
