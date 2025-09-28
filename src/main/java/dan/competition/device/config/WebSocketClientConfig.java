package dan.competition.device.config;

import dan.competition.device.service.MessageHandlerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;


@Configuration
public class WebSocketClientConfig {

    @Bean
    public WebSocketStompClient webSocketStompClient() {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        return stompClient;
    }

    @Bean
    public CustomStompSessionHandler stompSessionHandler(MessageHandlerService messageHandlerService) {
        return new CustomStompSessionHandler(messageHandlerService);
    }
}