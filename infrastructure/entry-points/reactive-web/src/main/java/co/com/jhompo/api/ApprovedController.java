package co.com.jhompo.api;
import co.com.jhompo.api.dtos.ApprovedDTO;
import co.com.jhompo.usecase.approved.ApprovedUseCase;
import co.com.jhompo.util.Messages.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ApprovedController {

    private final ApprovedUseCase approvedUseCase;
    private final ModelMapper mapper;

    @Operation(summary = HTTP.APPROVED_SUMMARY,description = HTTP.APPROVED_DESCRIPTION,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = HTTP.RESPONSE_OK_DESCRIPTION,
                content = @Content(
                        mediaType = HTTP.MEDIA_TYPE_JSON,
                        schema = @Schema(implementation = ApprovedDTO.class)
                )
            ),
            @ApiResponse(responseCode = "500", description = HTTP.RESPONSE_ERROR_DESCRIPTION)
        }
    )
    @GetMapping
    public Mono<ApprovedDTO> getApproved() {
        log.info(SYSTEM.REPORT_PROCESS);

        return approvedUseCase.get()
                .map(model-> mapper.map(model, ApprovedDTO.class ));
    }
}