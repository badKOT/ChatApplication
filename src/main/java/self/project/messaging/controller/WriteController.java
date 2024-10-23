package self.project.messaging.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import self.project.messaging.dto.NewChatRqDto;
import self.project.messaging.service.DelegatingService;

import java.security.Principal;

@RestController
@RequestMapping("/write")
@RequiredArgsConstructor
public class WriteController {
    private final DelegatingService delegatingService;
    private final static String OK = "OK";

    @PostMapping("/new-chat")
    public String createChat(@RequestBody NewChatRqDto dto, Principal principal) throws JsonProcessingException {
        delegatingService.createChat(dto, principal.getName());
        return new ObjectMapper().writeValueAsString(OK);
    }
}
