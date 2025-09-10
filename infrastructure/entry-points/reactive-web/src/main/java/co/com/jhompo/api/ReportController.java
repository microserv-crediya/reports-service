package co.com.jhompo.api;
import co.com.jhompo.api.dtos.ApprovedCountDTO;
import co.com.jhompo.usecase.approvedcount.ApprovedCountUseCase;
import co.com.jhompo.util.Messages.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ApprovedCountUseCase approvedCountUseCase;
    private final ModelMapper mapper;

    @GetMapping
    public Mono<ApprovedCountDTO> getApprovedCount() {
        log.info(SYSTEM.REPORT_PROCESS);

        return approvedCountUseCase.get()
                .map(model-> mapper.map(model,ApprovedCountDTO.class ));
    }
}