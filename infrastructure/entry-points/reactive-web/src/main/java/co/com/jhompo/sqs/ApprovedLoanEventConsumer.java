package co.com.jhompo.sqs;

import co.com.jhompo.usecase.approvedcount.ApprovedCountUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovedLoanEventConsumer {

    private final ApprovedCountUseCase approvedCountUseCase;

    // Este metodo será activado automáticamente por el cliente de SQS al recibir un mensaje
    public Mono<Void> processMessage(Message message) {
        log.info("Procesando mensaje de SQS: {}", message.body());

        // Aquí es donde se llama al metodo de incremento.
        return approvedCountUseCase.increment()
                .doOnSuccess(count -> log.info("Contador incrementado a: {}", count.getCount()))
                .doOnError(error -> log.error("Error al incrementar el contador: {}", error.getMessage()))
                .then(); // Indica que el proceso ha finalizado
    }
}
