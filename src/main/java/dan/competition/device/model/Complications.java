package dan.competition.device.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Осложнения.
 */
@Data
@AllArgsConstructor
public class Complications {
    private Long id;

    /**
     * Название.
     */
    private String name;

    /**
     * Описание.
     */
    private String description;

    /**
     * Груви скрипт, определяющий наличие осложнения.
     */
    private String condition;
}