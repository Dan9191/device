package dan.competition.device.service;

import dan.competition.device.model.BatchMessage;
import dan.competition.device.model.MedicalDataDTO;
import dan.competition.device.model.StatusMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSimulatorService {

    private final SimpMessagingTemplate messagingTemplate;

    private List<MedicalDataDTO> dataset = new ArrayList<>();
    private boolean started = false;

    @PostConstruct
    public void init() {
        // simple dataset generation
        for (int i = 0; i < 500; i++) {
            dataset.add(new MedicalDataDTO(58.3 + i*0.1, i%20==0 ? 12.5 : null, 120 + (i%20)));
        }
    }

    @Scheduled(fixedRate = 50000)
    public void simulate() {
        // if (started) return;
        started = true;
        log.info("simulate start");
        // send START
        messagingTemplate.convertAndSend("/topic/status",
                new StatusMessage ("START","123","Иван","Иванов", "Начало отправки данных"));

        int batchSize = 10;
        int totalBatches = (int)Math.ceil(dataset.size()/(double)batchSize);
        for (int i=0; i<totalBatches; i++) {
            int from = i*batchSize;
            int to = Math.min(dataset.size(), from+batchSize);
            List<MedicalDataDTO> part = dataset.subList(from, to);
            messagingTemplate.convertAndSend("/topic/data", new BatchMessage(i, totalBatches, part));
            try { Thread.sleep(500); } catch (Exception ignored) {}
        }

        // send FINISH
        messagingTemplate.convertAndSend("/topic/status",
                new StatusMessage("FINISH","123","Иван","Иванов","Конец передачи данных"));
    }
}
