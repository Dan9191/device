package dan.competition.device.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDataWebSocket {

    private Long id;

    private String name;

    private Integer age;

    /**
     * Кислотно-щелочной баланс.
     */
    private Float ph;

    /**
     * Давление углекислого газа, растворенного в артериальной крови.
     */
    private Float co2;

    /**
     * Уровень сахара в крови.
     */
    private Float glu;

    /**
     * Продукт анаэробного (бескислородного) метаболизма глюкозы.
     */
    private Float lac;

    /**
     * Показатель метаболического компонента регуляции pH.
     */
    private Float be;

    private List<String> diagnoses;

    private Boolean status;
}
