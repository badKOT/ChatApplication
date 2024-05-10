package self.project.websocket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import self.project.websocket.dto.MessageDto;
import self.project.websocket.mapper.DtoMessageMapper;
import self.project.websocket.model.Chat;
import self.project.websocket.service.ChatService;
import self.project.websocket.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/fetch")
@RequiredArgsConstructor
public class FetchController {

    private final ChatService chatService;

    @GetMapping("/chatList")
    public String chatListForUser() throws JsonProcessingException {
//        List<Chat> chatList = chatService.findForUser(username);
        List<Chat> chatList = chatService.findAll();
        // TODO() exclude participants
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(chatList);
    }
}
