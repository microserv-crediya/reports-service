package co.com.jhompo.api.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApprovedCountDTO {
    private String id; // Usaremos un ID fijo, como "reports-counter"
    private Long count;

}
