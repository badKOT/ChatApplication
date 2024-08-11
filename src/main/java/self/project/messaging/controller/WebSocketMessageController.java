package self.project.messaging.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import self.project.messaging.dto.MessageDto;
import self.project.messaging.mapper.AccountMapper;
import self.project.messaging.model.Account;
import self.project.messaging.service.AccountService;
import self.project.messaging.service.DelegatingService;

@Controller
@RequiredArgsConstructor
public class WebSocketMessageController {

    private final AccountService accountService;
    private final DelegatingService delegatingService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public MessageDto sendMessage(@Payload MessageDto messageDto) {
        System.out.println("Got the request to send the message: " + messageDto);
        delegatingService.saveMessage(messageDto);

        Long chatId = messageDto.getChatId();
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, messageDto);
        return messageDto;
    }

    /**
     * Temporarily disabled. Need to fix it.
     */
//    @MessageMapping("/chat.addUser")
//    @SendTo("/topic/public")
    public MessageDto addUser(@Payload MessageDto messageDto, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("Got the request to add user: " + messageDto);

        Account account = AccountMapper.INSTANCE.toEntity(messageDto);
        if (accountService.findByUsername(messageDto.getSender()) == null) {
            accountService.save(account);
        }

        // add username to web socket session
        headerAccessor.getSessionAttributes().put("username", messageDto.getSender());
        return messageDto;
    }
}
