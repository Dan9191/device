package dan.competition.device.config;


import dan.competition.device.model.PatientData;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Свойства приложения.
 */
@Component
@ConfigurationProperties(prefix = "medical.device")
@Data
public class AppConfig {

    private PatientData patientData = PatientData.builder()
            .id(33L)
            .name("12-regular")
            .diagnoses(List.of("I своевременные роды", "Анемия"))
            .age(22)
            .be(1234.2f)
            .ph(1234.2f)
            .co2(1234.2f)
            .lac(1234.2f)
            .glu(1234.2f)
            .build();

    private boolean inStream = true;
}
