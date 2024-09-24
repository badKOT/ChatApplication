package self.project.messaging.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final AccountService accountService;

    @GetMapping()
    public String chatList(Principal principal, Model model) {
        Account account = accountService.findByUsername(principal.getName());
        model.addAttribute("userId", account.getId());
        model.addAttribute("username", account.getUsername());
        return "index";
    }

    @GetMapping("/{id}")
    public String getChatById(@PathVariable Long id, Model model, Principal principal) throws JsonProcessingException {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build(); // TODO() check: should work the same way as commented lines above do
        Account account = accountService.findByUsername(principal.getName());
        ChatFullDto chatInfo = delegatingService.loadChatById(id);

        model.addAttribute("userId", account.getId());
        model.addAttribute("username", principal.getName());
        model.addAttribute("chatInfo", mapper.writeValueAsString(chatInfo));
        return "index";
    }
}
