package dan.competition.device.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StatusMessage {
    private final String type; // START or FINISH
    private final String patientId;
    private final String firstName;
    private final String lastName;
    private final String info;


}
