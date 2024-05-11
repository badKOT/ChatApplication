package self.project.messaging.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import self.project.messaging.dto.ChatIdTitleDto;
import self.project.messaging.mapper.DtoChatMapper;
import self.project.messaging.service.ChatService;

import java.util.List;

@RestController
@RequestMapping("/fetch")
@RequiredArgsConstructor
public class FetchController {

    private final ChatService chatService;

    @GetMapping("/chatList")
    public String chatListForUser() throws JsonProcessingException {
//        List<Chat> chatList = chatService.findForUser(username); TODO() chat-list for user
        List<ChatIdTitleDto> chatList = chatService.findAll()
                .stream()
                .map(DtoChatMapper.INSTANCE::toDto)
                .toList();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(chatList);
    }
}
