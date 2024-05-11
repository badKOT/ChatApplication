package self.project.messaging.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import self.project.messaging.dto.MessageDto;
import self.project.messaging.model.Account;
import self.project.messaging.model.Chat;
import self.project.messaging.model.Message;
import self.project.messaging.repository.MessageRepository;

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
