package co.com.jhompo.api.dtos;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApprovedCountDTO {
    private String id; // Usaremos un ID fijo, como "reports-counter"
    private Long count;
    private BigDecimal totalAmount;
}
