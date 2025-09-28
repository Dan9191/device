package dan.competition.device.controller;

import dan.competition.device.config.AppConfig;
import dan.competition.device.model.PatientData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/device")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000"})
public class PatientController {

    private final AppConfig appConfig;

    @GetMapping("/patient")
    public PatientData getCurrentPatient() {
        return appConfig.getPatientData();
    }

    @GetMapping("/status")
    public boolean getStatus() {
        return appConfig.getPatientData().getStatus();
    }
}
