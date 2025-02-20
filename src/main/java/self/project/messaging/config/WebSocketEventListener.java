package self.project.messaging.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import self.project.messaging.dto.MessageDto;
import self.project.messaging.dto.MessageType;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            log.info("User {} disconnected!", username);
            var chatMessage = MessageDto.builder()
                    .type(MessageType.LEAVE)
                    .sender(new MessageDto.Sender(null, username))
                    .build();
            messageTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
