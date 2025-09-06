package co.com.jhompo.usecase.approvedcount;

import co.com.jhompo.model.approvedcount.ApprovedCount;
import co.com.jhompo.model.approvedcount.gateways.ApprovedCountRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ApprovedCountUseCase {
    private final ApprovedCountRepository approvedCountRepository;

    public Mono<ApprovedCount> execute() {
        return approvedCountRepository.incrementCount();
    }
}
