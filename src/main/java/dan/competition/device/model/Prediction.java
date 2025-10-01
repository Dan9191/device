package dan.competition.device.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * Предсказание.
 */
@Data
public class Prediction {

    @JsonProperty("message")
    private String message;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("severity")
    private String severity;

    @JsonProperty("riskComplication")
    private double riskComplication;
}
