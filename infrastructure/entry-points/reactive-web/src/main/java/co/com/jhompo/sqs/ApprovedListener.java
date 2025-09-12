package co.com.jhompo.sqs;

import co.com.jhompo.usecase.approved.ApprovedUseCase;
import co.com.jhompo.util.Messages.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.awspring.cloud.sqs.annotation.SqsListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
public class ApprovedListener {

    private final ApprovedUseCase approvedUseCase;
    private final ObjectMapper objectMapper;

    public ApprovedListener(ApprovedUseCase approvedUseCase, ObjectMapper objectMapper) {
        this.approvedUseCase = approvedUseCase;
        this.objectMapper = objectMapper;
    }


    @SqsListener("queue-reports")
    public void processMessage(String messageBody) {
        log.info(SYSTEM.SQS_PROCESS, messageBody);

        try {
            Map<String, Object> parsedMessage = objectMapper.readValue(messageBody, Map.class);
            String status = (String) parsedMessage.get("status");
            log.info(SYSTEM.STATUS_LOG, status);
            if (status != null && status.equals(STATUS.APROBADO)) {
                // Obtiene el valor del monto como un String para evitar problemas con números grandes
                String amountStr = (String) parsedMessage.get("amount");

                if (amountStr != null) {
                    BigDecimal amount = new BigDecimal(amountStr);
                    String loanId = (String) parsedMessage.get("loanId");

                    if (amount.compareTo(BigDecimal.ZERO) > 0) {
                        log.info(SYSTEM.APPROVED_AMOUNT_DETECTED, amount, loanId);

                        approvedUseCase.update(amount)
                                .doOnSuccess(count -> log.info(SYSTEM.COUNTER_INCREMENTED, count.getCount()))
                                .doOnError(error -> log.error(SYSTEM.COUNTER_ERROR, error.getMessage()))
                                .subscribe();
                    } else {
                        log.warn(SYSTEM.INVALID_AMOUNT_WARNING, parsedMessage);
                    }
                } else {
                    log.warn(SYSTEM.NULL_AMOUNT_WARNING, parsedMessage);
                }
            }

        } catch (JsonProcessingException e) {
            log.error(SYSTEM.JSON_PARSE_ERROR, e.getMessage());
            log.error(SYSTEM.JSON_CONTENT_ERROR, messageBody);
        } catch (Exception e) {
            log.error(SYSTEM.UNEXPECTED_PROCESSING_ERROR, e.getMessage(), e);
        }
    }
}