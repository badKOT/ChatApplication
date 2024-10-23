package self.project.messaging.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import self.project.messaging.dto.ChatShortDto;
import self.project.messaging.dto.NewChatRqDto;
import self.project.messaging.model.tables.AccountsChats;
import self.project.messaging.model.tables.Chats;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRepository {
    private final DSLContext dsl;

    public Optional<Long> save(NewChatRqDto chat) {
        var C = Chats.CHATS;
        return dsl.insertInto(C)
                .set(dsl.newRecord(C, chat))
                .returning(C.ID)
                .fetchOptionalInto(ChatShortDto.class)
                .map(ChatShortDto::getId);
    }

    public List<ChatShortDto> findByUserId(Long userId) {
        var C = Chats.CHATS;
        var AC = AccountsChats.ACCOUNTS_CHATS;
        return dsl.select(C.ID, C.TITLE)
                .from(C.join(AC)
                        .on(C.ID.eq(AC.CHAT_ID)))
                .where(AC.ACCOUNT_ID.eq(userId))
                .fetchInto(ChatShortDto.class);
    }

    public Optional<ChatShortDto> findByIdShort(Long id) {
        var C = Chats.CHATS;
        return dsl.select(C.ID, C.TITLE)
                .from(C)
                .where(C.ID.eq(id))
                .fetchOptionalInto(ChatShortDto.class);
    }
}
