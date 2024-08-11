package self.project.messaging.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import self.project.messaging.dto.ChatFullDto;
import self.project.messaging.model.Account;
import self.project.messaging.service.AccountService;
import self.project.messaging.service.DelegatingService;

import java.security.Principal;

@Controller
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ViewController {

    private final DelegatingService delegatingService;
    private final ObjectMapper om;
    private final AccountService accountService;

    @GetMapping()
    public String chatList(Principal principal, Model model) {
        Account account = accountService.findByUsername(principal.getName());
        model.addAttribute("userId", account.getId());
        return "index";
    }

    @GetMapping("/{id}")
    public String getChatById(@PathVariable Long id, Model model, Principal principal) throws JsonProcessingException {
        om.registerModule(new JavaTimeModule());
        ChatFullDto chatInfo = delegatingService.loadChatById(id);

        model.addAttribute("username", principal.getName());
        model.addAttribute("chatInfo", om.writeValueAsString(chatInfo));
        return "chatView";
    }

    @PostMapping("/{id}/add/{userId}")
    public String addUserToChat(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        System.out.printf("Adding user %d to chat %d\n", userId, id);
        delegatingService.addUserToChat(id, userId);
        // TODO() message that a user was added
        return "redirect:/chats/" + id;
    }
}
