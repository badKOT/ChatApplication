package self.project.messaging.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import self.project.messaging.dto.MessageDto;
import self.project.messaging.dto.MessageDto.Sender;
import self.project.messaging.dto.MessageType;
import self.project.messaging.dto.NewChatRqDto;
import self.project.messaging.service.AccountService;
import self.project.messaging.service.ChatService;
import self.project.messaging.service.DelegatingService;

import java.security.Principal;
import java.time.Instant;

@RestController
@RequestMapping("/write")
@RequiredArgsConstructor
public class WriteController {
    private final ChatService chatService;
    private final AccountService accountService;
    private final DelegatingService delegatingService;
    private final static String OK = "OK";

    @PostMapping("/new-chat")
    public String createChat(@RequestBody NewChatRqDto dto, Principal principal) throws JsonProcessingException {
        System.out.println("Got the request to create chat");
        // save chat
        var chatId = chatService.save(dto);
        // add chat to the user
        var account = accountService.findByUsername(principal.getName());
        var messageDto = new MessageDto(
                            account.getId().toString(),
                            new Sender(account.getId(), account.getUsername()),
                            MessageType.JOIN,
                            Instant.now(),
                            chatId);
        delegatingService.addUserToChat(messageDto);

        System.out.println("New chat request completed successfully!");
        return new ObjectMapper().writeValueAsString(OK);
    }
}
