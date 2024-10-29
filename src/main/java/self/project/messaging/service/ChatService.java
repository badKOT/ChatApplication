package self.project.messaging.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import self.project.messaging.dto.ChatShortDto;
import self.project.messaging.dto.NewChatRqDto;
import self.project.messaging.repository.ChatRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository repository;

    public Long save(NewChatRqDto dto) {
        return repository.save(dto).orElseThrow(() ->
                new IllegalArgumentException("Error saving chat with name: " + dto.title()));
    }

    public ChatShortDto findByIdShort(Long id) {
        return repository.findByIdShort(id).orElseThrow(() ->
                new NoSuchElementException("Chat with id " + id + " not found"));
    }

    public List<ChatShortDto> findForUser(Long userId) {
        return repository.findByUserId(userId);
    }

    public List<ChatShortDto> findForUserSorted(Long userId) {
        return repository.findByUserIdSorted(userId);
    }
}
