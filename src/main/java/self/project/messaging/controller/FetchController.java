package self.project.messaging.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import self.project.messaging.dto.ChatIdTitleDto;
import self.project.messaging.mapper.ChatMapper;
import self.project.messaging.service.ChatService;

import java.util.List;

@RestController
@RequestMapping("/fetch")
@RequiredArgsConstructor
public class FetchController {

    private final ChatService chatService;

    /**
     * Not used. Replaced with method below to return personalized list
     */
    @GetMapping("/chatList")
    public String chatListForUser() throws JsonProcessingException {
        List<ChatIdTitleDto> chatList = chatService.findAll()
                .stream()
                .map(ChatMapper.INSTANCE::toDto)
                .toList();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(chatList);
    }

    @GetMapping("/chatList/{userId}")
    public String chatListForUser(@PathVariable Long userId) throws JsonProcessingException {
        List<ChatIdTitleDto> chatList = chatService.findForUser(userId)
                .stream()
                .map(ChatMapper.INSTANCE::toDto)
                .toList();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(chatList);
    }
}
