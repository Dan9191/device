package dan.competition.device.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Complications {
    private Long id;
    private String name;
    private String description;
    private String condition;
}