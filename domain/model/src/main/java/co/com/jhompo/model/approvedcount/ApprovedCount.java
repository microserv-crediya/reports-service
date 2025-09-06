package co.com.jhompo.model.approvedcount;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApprovedCount {
    private String id; // Usaremos un ID fijo, como "reports-counter"
    private Long count;
}
