package dan.competition.device.config;


import dan.competition.device.model.PatientData;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Свойства приложения.
 */
@Configuration
@ConfigurationProperties(prefix = "medical.device")
@Data
public class AppProperties {

    private final PatientData patientData = PatientData.builder()
            .id(33L)
            .name("12-regular")
            .diagnoses(List.of("I своевременные роды", "Анемия"))
            .age(22)
            .be(1234.2f)
            .ph(1234.2f)
            .co2(1234.2f)
            .lac(1234.2f)
            .glu(1234.2f)
            .status(false)
            .build();

    private String sourceUrl = "ws://localhost:8097/ws";
}
