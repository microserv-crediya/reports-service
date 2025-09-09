package co.com.jhompo.sqs;

import co.com.jhompo.usecase.approvedcount.ApprovedCountUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.awspring.cloud.sqs.annotation.SqsListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovedLoanEventConsumer {

    private final ApprovedCountUseCase approvedCountUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Listener que escucha automáticamente la cola "queue-reports"
    @SqsListener("queue-reports")
    public void processMessage(String messageBody) {
        log.info("Procesando mensaje de SQS: {}", messageBody);

        try {
            Map<String, Object> parsedMessage = objectMapper.readValue(messageBody, Map.class);
            Double amount = (Double) parsedMessage.get("amount");
            String loanId = (String) parsedMessage.get("loanId");

            if (amount != null) {
                log.info("Monto aprobado detectado: {} para préstamo ID: {}", amount, loanId);

                // Ejecuta el caso de uso y suscribe al Mono para que se ejecute
                approvedCountUseCase.increment()
                        .doOnSuccess(count -> log.info("Contador incrementado a: {}", count.getCount()))
                        .doOnError(error -> log.error("Error al incrementar el contador: {}", error.getMessage()))
                        .subscribe(); // Importante para ejecutar el Mono
            } else {
                log.warn("Mensaje SQS no contiene un 'amount' válido. Contenido: {}", parsedMessage);
            }
        } catch (JsonProcessingException e) {
            log.error("Error al parsear el mensaje JSON de SQS: {}", e.getMessage());
            log.error("Contenido del mensaje que falló: {}", messageBody);
        } catch (Exception e) {
            log.error("Error inesperado procesando mensaje SQS: {}", e.getMessage(), e);
        }
    }
}