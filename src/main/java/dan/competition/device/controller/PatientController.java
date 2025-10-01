package dan.competition.device.controller;

import dan.competition.device.model.PatientData;
import dan.competition.device.service.MessageHandlerService;
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

    private final MessageHandlerService messageHandlerService;

    @GetMapping("/patient")
    public PatientData getCurrentPatient() {
        return messageHandlerService.getPatientData();
    }

}
