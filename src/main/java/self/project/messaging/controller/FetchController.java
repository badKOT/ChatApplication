package self.project.messaging.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import self.project.messaging.dto.ChatFullDto;
import self.project.messaging.dto.ChatIdTitleDto;
import self.project.messaging.mapper.ChatMapper;
import self.project.messaging.service.ChatService;
import self.project.messaging.service.DelegatingService;

import java.util.List;

@RestController
@RequestMapping("/fetch")
@RequiredArgsConstructor
public class FetchController {

    private final DelegatingService delegatingService;
    private final ChatService chatService;

    @GetMapping("/chatList/{userId}")
    public String chatListForUser(@PathVariable Long userId) throws JsonProcessingException {
        List<ChatIdTitleDto> chatList = chatService.findForUser(userId)
                .stream()
                .map(ChatMapper.INSTANCE::toDto)
                .toList();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(chatList);
    }

    @GetMapping("/chatInfo/{id}")
    public String chatInfo(@PathVariable Long id) throws JsonProcessingException {
        ChatFullDto chatInfo = delegatingService.loadChatById(id);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsString(chatInfo);
    }
}
