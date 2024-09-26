package self.project.messaging.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import self.project.messaging.dto.MessageDto;
import self.project.messaging.service.DelegatingService;
import self.project.messaging.service.MessageService;

@Controller
@RequiredArgsConstructor
public class WebSocketMessageController {

    private final DelegatingService delegatingService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat.sendMessage")
    public MessageDto sendMessage(@Payload MessageDto messageDto) {
        System.out.println("Got the request to send the message: " + messageDto);
        messageService.save(messageDto);

        messagingTemplate.convertAndSend("/topic/chat/" + messageDto.getChatId(), messageDto);
        return messageDto;
    }

    @MessageMapping("/chat.addUser")
    public MessageDto addUser(@Payload MessageDto messageDto, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("Got the request to add user: " + messageDto);
        // When user is added, content field contains id of the user being added. Need to change to username of this user
        // TODO() rethink this shit

        delegatingService.addUserToChat(messageDto);

        messagingTemplate.convertAndSend("/topic/chat/" + messageDto.getChatId(), messageDto);

        // add username to web socket session
        headerAccessor.getSessionAttributes().put("username", messageDto.getContent());
        return messageDto;
    }
}
