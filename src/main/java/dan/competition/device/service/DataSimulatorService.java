package dan.competition.device.service;

import dan.competition.device.config.AppProperties;
import dan.competition.device.config.CustomStompSessionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import jakarta.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSimulatorService {

    private final WebSocketStompClient stompClient;
    private final CustomStompSessionHandler stompSessionHandler;
    private StompSession stompSession;
    private volatile boolean isConnecting = false;
    private final AppProperties appConfig;

    @PostConstruct
    public void init() {
        connectToSource();
    }

    public void setStompSession(StompSession session) {
        this.stompSession = session;
        this.isConnecting = false;
    }

    public void handleDisconnect() {
        this.stompSession = null;
        connectToSource();
    }

    private void connectToSource() {
        if (isConnecting || stompSession != null) {
            return;
        }
        isConnecting = true;
        log.info("Attempting to connect to source at {}", appConfig.getSourceUrl());

        stompClient.connectAsync(appConfig.getSourceUrl(), stompSessionHandler)
                .whenComplete((session, throwable) -> {
                    if (throwable != null) {
                        log.error("Failed to connect to source: {}", throwable.getMessage());
                        scheduleReconnect();
                    } else {
                        log.info("Successfully connected to source");
                        setStompSession(session);
                    }
                });
    }

    @Scheduled(fixedDelay = 5000)
    private void scheduleReconnect() {
        if (stompSession == null && !isConnecting) {
            connectToSource();
        }
    }
}