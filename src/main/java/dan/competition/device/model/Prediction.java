package dan.competition.device.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * Предсказание.
 */
@Data
public class Prediction {

    /**
     * Сообщение предсказания.
     */
    @JsonProperty("message")
    private String message;

    /**
     * Временная метка.
     */
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    /**
     * Маркер опасности.
     */
    @JsonProperty("severity")
    private String severity;

    /**
     * Шанс рождения больного ребенка.
     */
    @JsonProperty("riskComplication")
    private double riskComplication;
}
