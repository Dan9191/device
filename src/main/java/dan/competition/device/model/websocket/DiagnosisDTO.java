package dan.competition.device.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiagnosisDTO {
    private Long id;
    private String name;
    private String description;
    private String impact;
}