package self.project.messaging.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import self.project.messaging.dto.AddUserToChatDto;
import self.project.messaging.dto.NewChatRqDto;
import self.project.messaging.service.DelegatingService;

import java.security.Principal;

@RestController
@RequestMapping("/write")
@RequiredArgsConstructor
public class WriteController {
    private final static String OK = "OK";

    private final DelegatingService delegatingService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/new-chat")
    public String createChat(@RequestBody NewChatRqDto dto, Principal principal) {
        var newChatId = delegatingService.createChat(dto, principal.getName());
        return String.valueOf(newChatId);
    }

    @PostMapping("/user/to/chat")
    public String addUserToChat(@RequestBody AddUserToChatDto dto) {
        var messageDto = delegatingService.addUserToChat(dto);
        messagingTemplate.convertAndSend("/topic/chat/" + dto.chatId(), messageDto);
        return OK;
    }
}
