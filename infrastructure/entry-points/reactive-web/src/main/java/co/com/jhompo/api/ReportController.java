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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ApprovedCountUseCase approvedCountUseCase;
    private final ModelMapper mapper;

    @Operation(
            summary = "Obtener cantidad y monto total de solicitudes aprobadas",
            description = "Este endpoint retorna la cantidad de solicitudes de crédito aprobadas y el monto total aprobado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Información de solicitudes aprobadas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApprovedCountDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @GetMapping
    public Mono<ApprovedCountDTO> getApprovedCount() {
        log.info(SYSTEM.REPORT_PROCESS);

        return approvedCountUseCase.get()
                .map(model-> mapper.map(model,ApprovedCountDTO.class ));
    }
}