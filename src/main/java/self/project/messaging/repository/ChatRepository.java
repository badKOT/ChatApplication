package self.project.messaging.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import self.project.messaging.dto.ChatShortDto;
import self.project.messaging.model.tables.AccountsChats;
import self.project.messaging.model.tables.Chats;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRepository {
    private final DSLContext dsl;

    public List<ChatShortDto> findByUserId(Long userId) {
        return dsl.select(Chats.CHATS.ID, Chats.CHATS.TITLE)
                .from(Chats.CHATS
                        .join(AccountsChats.ACCOUNTS_CHATS)
                        .on(Chats.CHATS.ID.eq(AccountsChats.ACCOUNTS_CHATS.CHAT_ID)))
                .where(AccountsChats.ACCOUNTS_CHATS.ACCOUNT_ID.eq(userId))
                .fetchInto(ChatShortDto.class);
    }

    public Optional<ChatShortDto> findByIdShort(Long id) {
        return dsl.select(Chats.CHATS.ID, Chats.CHATS.TITLE)
                .from(Chats.CHATS)
                .where(Chats.CHATS.ID.eq(id))
                .fetchOptionalInto(ChatShortDto.class);
    }
}
