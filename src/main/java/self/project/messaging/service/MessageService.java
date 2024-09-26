package self.project.messaging.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import self.project.messaging.dto.MessageDto;
import self.project.messaging.repository.MessageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository repository;

    public void save(MessageDto messageDto) {
        repository.save(messageDto);
    }

    public List<MessageDto> findByChatId(Long chatId) {
        return repository.findByChatId(chatId);
    }
}
