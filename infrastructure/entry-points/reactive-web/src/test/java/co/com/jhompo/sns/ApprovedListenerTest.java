package co.com.jhompo.sns;

import co.com.jhompo.model.approved.Approved;
import co.com.jhompo.sqs.ApprovedListener;
import co.com.jhompo.usecase.approved.ApprovedUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovedListenerTest {

    @Mock
    private ApprovedUseCase approvedUseCase;

    private ObjectMapper objectMapper;

    @InjectMocks
    private ApprovedListener approvedListener;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        approvedListener = new ApprovedListener(approvedUseCase, objectMapper);
    }

    @Test
    @DisplayName("Should process valid approved message with positive amount")
    void shouldProcessValidApprovedMessage() throws Exception {
        Map<String, Object> message = new HashMap<>();
        message.put("status", "APROBADO");
        message.put("amount", "100.50");
        message.put("loanId", "loan-123");

        String json = objectMapper.writeValueAsString(message);

        when(approvedUseCase.update(any(BigDecimal.class)))
                .thenReturn(Mono.just(Approved.builder().count(1L).build()));

        approvedListener.processMessage(json);

        verify(approvedUseCase, times(1)).update(new BigDecimal("100.50"));
    }

    @Test
    @DisplayName("Should warn when amount is zero")
    void shouldWarnWhenAmountIsZero() throws Exception {
        Map<String, Object> message = new HashMap<>();
        message.put("status", "APROBADO");
        message.put("amount", "0");
        message.put("loanId", "loan-123");

        String json = objectMapper.writeValueAsString(message);

        approvedListener.processMessage(json);

        verify(approvedUseCase, never()).update(any());
    }

    @Test
    @DisplayName("Should warn when amount is null")
    void shouldWarnWhenAmountIsNull() throws Exception {
        Map<String, Object> message = new HashMap<>();
        message.put("status", "APROBADO");
        message.put("loanId", "loan-123");

        String json = objectMapper.writeValueAsString(message);

        approvedListener.processMessage(json);

        verify(approvedUseCase, never()).update(any());
    }

    @Test
    @DisplayName("Should not process message when status is not APROBADO")
    void shouldNotProcessWhenStatusIsNotApproved() throws Exception {
        Map<String, Object> message = new HashMap<>();
        message.put("status", "RECHAZADO");
        message.put("amount", "100.50");

        String json = objectMapper.writeValueAsString(message);

        approvedListener.processMessage(json);

        verify(approvedUseCase, never()).update(any());
    }

    @Test
    @DisplayName("Should log error on invalid JSON")
    void shouldLogErrorOnInvalidJson() {
        String invalidJson = "{invalid-json}";

        approvedListener.processMessage(invalidJson);

        verify(approvedUseCase, never()).update(any());
    }

    @Test
    @DisplayName("Should catch unexpected exceptions")
    void shouldCatchUnexpectedExceptions() throws Exception {
        Map<String, Object> message = new HashMap<>();
        message.put("status", "APROBADO");
        message.put("amount", "200");

        String json = objectMapper.writeValueAsString(message);

        when(approvedUseCase.update(any())).thenThrow(new RuntimeException("Unexpected error"));

        approvedListener.processMessage(json);

        verify(approvedUseCase, times(1)).update(new BigDecimal("200"));
    }
}

