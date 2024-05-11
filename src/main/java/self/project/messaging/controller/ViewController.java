package self.project.messaging.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import self.project.messaging.dto.ChatInfoDto;
import self.project.messaging.service.DelegatingService;

import java.security.Principal;

@Controller
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ViewController {

    private final DelegatingService delegatingService;
    private final ObjectMapper om;

    @GetMapping()
    public String chatList() {
        return "index";
    }

    @GetMapping("/{id}")
    public String getChatById(@PathVariable Long id, Model model, Principal principal) throws JsonProcessingException {
        om.registerModule(new JavaTimeModule());
        ChatInfoDto chatInfo = delegatingService.loadChatById(id);

        model.addAttribute("username", principal.getName());
        model.addAttribute("chatInfo", om.writeValueAsString(chatInfo));
        return "chatView";
    }
}
