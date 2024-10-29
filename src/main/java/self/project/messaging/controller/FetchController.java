package self.project.messaging.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import self.project.messaging.dto.ChatFullDto;
import self.project.messaging.dto.ChatShortDto;
import self.project.messaging.service.AccountService;
import self.project.messaging.service.ChatService;
import self.project.messaging.service.DelegatingService;

import java.util.List;

@RestController
@RequestMapping("/fetch")
@RequiredArgsConstructor
public class FetchController {

    private final DelegatingService delegatingService;
    private final ChatService chatService;
    private final AccountService accountService;
    ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @GetMapping("/chatList/{userId}")
    public String chatListForUser(@PathVariable Long userId) throws JsonProcessingException {
        List<ChatShortDto> chatList = chatService.findForUser(userId);
        return mapper.writeValueAsString(chatList);
    }

    @GetMapping("/chatInfo/{id}")
    public String chatInfo(@PathVariable Long id) throws JsonProcessingException {
        ChatFullDto chatInfo = delegatingService.loadChatById(id);
        return mapper.writeValueAsString(chatInfo);
    }

    @GetMapping("/users/search")
    public String searchUsers(@RequestParam String username) throws JsonProcessingException {
        var accounts = accountService.findByUsernameStartingWith(username);
        return mapper.writeValueAsString(accounts);
    }

    @GetMapping("/chatList/sorted/{userId}")
    public String sortedChatListForUser(@PathVariable Long userId) throws JsonProcessingException {
        var chatList = chatService.findForUserSorted(userId);
        return mapper.writeValueAsString(chatList);
    }
}
