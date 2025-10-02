package dan.competition.device.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Диагноз.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiagnosisDTO {

    private Long id;

    /**
     * Название
     */
    private String name;

    /**
     * Описание.
     */
    private String description;

    /**
     * Груви скрипт, вносящий изменения в стартовые нормативы пациента.
     */
    private String impact;
}