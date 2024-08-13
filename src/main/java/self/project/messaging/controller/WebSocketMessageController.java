package self.project.messaging.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import self.project.messaging.dto.MessageDto;
import self.project.messaging.service.DelegatingService;

@Controller
@RequiredArgsConstructor
public class WebSocketMessageController {

    private final DelegatingService delegatingService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public MessageDto sendMessage(@Payload MessageDto messageDto) {
        System.out.println("Got the request to send the message: " + messageDto);
        delegatingService.saveMessage(messageDto);

        messagingTemplate.convertAndSend("/topic/chat/" + messageDto.getChatId(), messageDto);
        return messageDto;
    }

    @MessageMapping("/chat.addUser")
    public MessageDto addUser(@Payload MessageDto messageDto, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("Got the request to add user: " + messageDto);
        // When user is added, sender field contains his id. Need to replace it with username.

        // TODO() check if user exists

        delegatingService.addUserToChat(messageDto.getChatId(), Long.parseLong(messageDto.getSender()));
        messageDto = delegatingService.saveMessage(messageDto);

        messagingTemplate.convertAndSend("/topic/chat/" + messageDto.getChatId(), messageDto);

        // add username to web socket session
        headerAccessor.getSessionAttributes().put("username", messageDto.getSender());
        return messageDto;
    }
}
