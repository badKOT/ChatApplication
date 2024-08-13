package self.project.messaging.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import self.project.messaging.dto.AccountDto;
import self.project.messaging.dto.ChatFullDto;
import self.project.messaging.dto.MessageDto;
import self.project.messaging.mapper.AccountMapper;
import self.project.messaging.mapper.ChatMapper;
import self.project.messaging.mapper.MessageMapper;
import self.project.messaging.model.Account;
import self.project.messaging.model.Chat;
import self.project.messaging.model.Message.MessageType;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DelegatingService {

    private final ChatService chatService;
    private final MessageService messageService;
    private final AccountService accountService;

    public ChatFullDto loadChatById(Long id) {
        Chat chat = chatService.findById(id);

        List<MessageDto> messageList = messageService
                .findByChat(id)
                .stream()
                .map(MessageMapper.INSTANCE::toDto)
                .sorted(Comparator.comparing(MessageDto::getSent))
                .toList();

        List<AccountDto> participants = chat
                .getParticipants()
                .stream()
                .map(AccountMapper.INSTANCE::toDto)
                .toList();

        return new ChatFullDto(ChatMapper.INSTANCE.toDto(chat), participants, messageList);
    }

    public MessageDto saveMessage(MessageDto messageDto) {
        Account account;

        if (messageDto.getType() == MessageType.JOIN) {
            account = accountService.findById(Long.parseLong(messageDto.getSender()));
            messageDto.setSender(account.getUsername());
        } else {
            account = accountService.findByUsername(messageDto.getSender());
        }
        Chat chat = chatService.findById(messageDto.getChatId());

        messageService.save(messageDto, account, chat);
        return messageDto;
    }

    @Transactional
    public void addUserToChat(Long chatId, Long userId) {
        Chat chat = chatService.findById(chatId);
        Account account = accountService.findById(userId);

        List<Account> participants = chat.getParticipants();
        if (!participants.stream()
                .map(Account::getId)
                .toList()
                .contains(userId)) {
            participants.add(account);
        }
    }
}
