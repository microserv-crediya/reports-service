package co.com.jhompo.model.approved;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Approved {
    private String id; // Usaremos un ID fijo, como "reports-counter"
    private Long count;
    private BigDecimal totalAmount;
}
