package self.project.messaging.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import self.project.messaging.dto.ChatShortDto;
import self.project.messaging.repository.ChatRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository repository;

//    public Long save(Chat chat) {
//        return repository.save(chat);
//    }

    public ChatShortDto findByIdShort(Long id) {
        return repository.findByIdShort(id).orElseThrow(() ->
                new NoSuchElementException("Chat with id " + id + " not found"));
    }

    public List<ChatShortDto> findForUser(Long userId) {
        return repository.findByUserId(userId);
    }

//    public Chat update(Chat chat) {
//        return repository.update(chat)
//                .orElseThrow(() -> new IllegalStateException("Error updating chat: " + chat.getId()));
//    }
}
