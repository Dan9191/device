package dan.competition.device.config;

import dan.competition.device.model.websocket.MedicalDataWebSocket;
import dan.competition.device.model.websocket.PatientDataWebSocket;
import dan.competition.device.service.MessageHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomStompSessionHandler extends StompSessionHandlerAdapter {

    private final MessageHandlerService messageHandlerService;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("Connected to source WebSocket");
        session.subscribe("/topic/patient", this);
        session.subscribe("/topic/data", this);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                                byte[] payload, Throwable exception) {
        log.error("STOMP error: {}", exception.getMessage(), exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        log.error("Transport error: {}", exception.getMessage(), exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        String destination = headers.getDestination();
        if ("/topic/patient".equals(destination)) {
            return PatientDataWebSocket.class;
        } else if ("/topic/data".equals(destination)) {
            return MedicalDataWebSocket.class;
        }
        return Object.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        String destination = headers.getDestination();
        if (destination == null) {
            log.warn("Received frame without destination: {}", payload);
            return;
        }
        log.debug("Received message on {}", destination);
        switch (destination) {
            case "/topic/patient" -> messageHandlerService.handlePatientData((PatientDataWebSocket) payload);
            case "/topic/data"    -> messageHandlerService.handleMedicalData((MedicalDataWebSocket) payload);
            default               -> log.warn("Unhandled destination: {}", destination);
        }
    }
}