package self.project.messaging.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import self.project.messaging.model.Chat;
import self.project.messaging.repository.ChatRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public List<Chat> findAll() {
        return chatRepository.findAll();
    }

    public Chat findById(Long id) {
        return chatRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Chat with id " + id + " not found"));
    }

    public List<Chat> findForUser(Long userId) {
        return chatRepository.findByUserId(userId);
    }
}
