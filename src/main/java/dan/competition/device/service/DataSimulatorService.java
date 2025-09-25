package dan.competition.device.service;

import dan.competition.device.config.AppConfig;
import dan.competition.device.model.MedicalData;
import dan.competition.device.model.PatientData;
import dan.competition.device.model.Prediction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSimulatorService {

    private final SimpMessagingTemplate messagingTemplate;

    private final AppConfig appConfig;

    private final List<MedicalData> dataset = new ArrayList<>();

    @PostConstruct
    public void init() {
        // simple dataset generation
        for (int i = 0; i < 500; i++) {
            dataset.add(new MedicalData(58.3 + i*0.1, 2 + (20 - 2) * Math.random(), 100 + (200 - 100) * Math.random(), (int) (20 + (100 - 20) * Math.random())));
        }
    }

    @Scheduled(fixedRate = 100000)
    public void simulate() {
        log.info("simulate start");
        appConfig.setInStream(true);
        for (int i=0; i<dataset.size(); i++) {
            messagingTemplate.convertAndSend("/topic/data", dataset.get(i));
            try { Thread.sleep(500); } catch (Exception ignored) {}

            if (i== 20) {
                log.info("send predicate1");
                sendPrediction(new Prediction());
            }

            if (i== 40) {
                log.info("send predicate2");
                sendPrediction2(new Prediction());
            }

            if (i== 60) {
                log.info("send predicate3");
                sendPrediction3(new Prediction());
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        appConfig.setPatientData(PatientData.builder()
                .id(36L)
                .name("16-regular")
                .diagnoses(List.of("Iiu своевременные роды", "Анемия"))
                        .age(66)
                .build());

        appConfig.setInStream(false);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendPrediction(Prediction prediction) {
        prediction.setMessage("Риск осложнений в ближайший час");
        prediction.setSeverity("negative");
        prediction.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/predictions", prediction);
    }

    public void sendPrediction2(Prediction prediction) {
        prediction.setMessage("Проверьте самочувствие плода");
        prediction.setSeverity("normal");
        prediction.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/predictions", prediction);
    }

    public void sendPrediction3(Prediction prediction) {
        prediction.setMessage("Положительный прогноз");
        prediction.setSeverity("positive");
        prediction.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/predictions", prediction);
    }
}
