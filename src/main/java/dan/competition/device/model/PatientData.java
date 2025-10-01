package dan.competition.device.model;

import dan.competition.device.model.websocket.DiagnosisDTO;
import dan.competition.device.model.websocket.PatientDataWebSocket;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PatientData {

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

    public static PatientData fromWebSocket(PatientDataWebSocket patientDataWebSocket) {
        List<String> diagnoses = patientDataWebSocket.getDiagnoses().stream().map(DiagnosisDTO::getName).toList();
        return PatientData.builder()
                .id(patientDataWebSocket.getId())
                .name(patientDataWebSocket.getName())
                .age(patientDataWebSocket.getAge())
                .ph(patientDataWebSocket.getPh())
                .co2(patientDataWebSocket.getCo2())
                .glu(patientDataWebSocket.getGlu())
                .lac(patientDataWebSocket.getLac())
                .be(patientDataWebSocket.getBe())
                .diagnoses(diagnoses)
                .status(patientDataWebSocket.getStatus())
                .build();
    }
}
