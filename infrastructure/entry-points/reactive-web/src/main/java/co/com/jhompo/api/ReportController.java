package co.com.jhompo.api;
import co.com.jhompo.api.dtos.ApprovedCountDTO;
import co.com.jhompo.model.approvedcount.ApprovedCount;
import co.com.jhompo.usecase.approvedcount.ApprovedCountUseCase;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ApprovedCountUseCase approvedCountUseCase;
    private final ModelMapper mapper;

    @GetMapping
    public Mono<ApprovedCountDTO> getApprovedCount() {
        return approvedCountUseCase.get()
                .map(model-> mapper.map(model,ApprovedCountDTO.class ));
    }
}