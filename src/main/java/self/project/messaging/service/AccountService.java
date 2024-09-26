package self.project.messaging.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import self.project.messaging.dto.AccountDto;
import self.project.messaging.repository.AccountRepository;
import self.project.messaging.security.AccountFullDto;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;

    public AccountDto findById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Account with id " + id + " not found"));
    }

    public AccountDto findByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() ->
                new NoSuchElementException("Account with username " + username + " not found"));
    }

    public AccountFullDto findFullByUsername(String username) {
        return repository.findFullByUsername(username).orElseThrow(() ->
                new NoSuchElementException("Account with username " + username + " not found"));
    }

    public List<AccountDto> findByChatId(Long chatId) {
        return repository.findByChatId(chatId);
    }

    public void addUserToChat(Long chatId, Long userId) {
        List<AccountDto> accounts = findByChatId(chatId);

        if (!accounts.stream().map(AccountDto::getId).toList().contains(userId)) {
            repository.addUserToChat(chatId, userId);
        }
    }
}
