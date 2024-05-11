package self.project.websocket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import self.project.websocket.dto.MessageDto;
import self.project.websocket.model.Account;
import self.project.websocket.model.Chat;
import self.project.websocket.model.Message;
import self.project.websocket.repository.MessageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository repository;

    public void save(MessageDto messageDto, Account account, Chat chat) {
        repository.save(new Message(null, messageDto.getContent(), messageDto.getSent(), account, chat));
    }

    public List<Message> findByChat(Long chatId) {
        return repository.findByChatId(chatId);
    }
}
