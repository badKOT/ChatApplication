package self.project.messaging.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import self.project.messaging.dto.MessageDto;
import self.project.messaging.service.MessageService;

@Controller
@RequiredArgsConstructor
public class WebSocketMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat.sendMessage")
    public MessageDto sendMessage(@Payload MessageDto messageDto) {
        System.out.println("Got the request to send the message: " + messageDto);
        messageService.save(messageDto);

        messagingTemplate.convertAndSend("/topic/chat/" + messageDto.getChatId(), messageDto);
        return messageDto;
    }
}
