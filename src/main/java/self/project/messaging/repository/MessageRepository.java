package self.project.messaging.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import self.project.messaging.dto.MessageDto;
import self.project.messaging.model.tables.Accounts;
import self.project.messaging.model.tables.Messages;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepository {
    private final DSLContext dsl;

    public List<MessageDto> findByChatId(Long chatId) {
        var M = Messages.MESSAGES;
        var A = Accounts.ACCOUNTS;
        return dsl.selectFrom(M.join(A)
                        .on(A.ID.eq(M.ACCOUNT_ID)))
                .where(M.CHAT_ID.eq(chatId))
                .fetch()
                .map(r -> {
                    var message = r.into(MessageDto.class);
                    message.setSender(new MessageDto.Sender(
                            r.get(M.ACCOUNT_ID),
                            r.get(A.USERNAME)));
                    return message;
                });
    }

    public void save(MessageDto messageDto) {
        var messageRecord = dsl.newRecord(Messages.MESSAGES, messageDto);
        messageRecord.set(Messages.MESSAGES.ACCOUNT_ID, messageDto.getSender().id());
        messageRecord.insert();
    }
}
