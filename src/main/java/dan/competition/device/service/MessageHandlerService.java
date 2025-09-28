package dan.competition.device.service;

import dan.competition.device.config.AppConfig;
import dan.competition.device.model.MedicalData;
import dan.competition.device.model.PatientData;
import dan.competition.device.model.Prediction;
import dan.competition.device.model.websocket.MedicalDataWebSocket;
import dan.competition.device.model.websocket.PatientDataWebSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageHandlerService {

    private final SimpMessagingTemplate messagingTemplate;
    private final AppConfig appConfig;

    public void handlePatientData(PatientDataWebSocket patientDataWebSocket) {
        log.info("Received PatientData: {}", patientDataWebSocket);
        PatientData patientData = PatientData.fromWebSocket(patientDataWebSocket);
        appConfig.setPatientData(patientData);
        appConfig.setInStream(patientData.getStatus() != null && patientData.getStatus());
    }

    public void handleMedicalData(MedicalDataWebSocket medicalData) {
        log.info("Received MedicalData: {}", medicalData);
        MedicalData processedData = doSomething(medicalData);
        messagingTemplate.convertAndSend("/topic/data", processedData);
    }

    private MedicalData doSomething(MedicalDataWebSocket medicalData) {
        int riskComplications = calculateRiskComplications(medicalData);
        MedicalData updatedData = new MedicalData(
                medicalData.getTimeSec(),
                medicalData.getUterus(),
                medicalData.getBpm(),
                riskComplications
        );

        if (Math.random() < 0.1) {
            Prediction prediction = new Prediction();
            prediction.setTimestamp(LocalDateTime.now());
            if (riskComplications > 80) {
                prediction.setMessage("Высокий риск осложнений");
                prediction.setSeverity("negative");
            } else if (riskComplications > 50) {
                prediction.setMessage("Проверьте состояние");
                prediction.setSeverity("normal");
            } else {
                prediction.setMessage("Положительный прогноз");
                prediction.setSeverity("positive");
            }
            messagingTemplate.convertAndSend("/topic/predictions", prediction);
        }

        return updatedData;
    }

    private int calculateRiskComplications(MedicalDataWebSocket medicalData) {
        double bpm = medicalData.getBpm();
        double uterus = medicalData.getUterus();
        int risk = (int) (bpm * 0.4 + uterus * 0.6);
        return Math.min(100, Math.max(0, risk));
    }
}
