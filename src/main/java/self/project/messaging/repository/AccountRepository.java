package self.project.messaging.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import self.project.messaging.dto.AccountDto;
import self.project.messaging.model.tables.Accounts;
import self.project.messaging.model.tables.AccountsChats;
import self.project.messaging.security.AccountFullDto;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountRepository {

    private final DSLContext dsl;

    public Optional<AccountDto> findById(Long id) {
        return dsl.selectFrom(Accounts.ACCOUNTS)
                .where(Accounts.ACCOUNTS.ID.eq(id))
                .fetchOptionalInto(AccountDto.class);
    }

    public Optional<AccountDto> findByUsername(String username) {
        return dsl.selectFrom(Accounts.ACCOUNTS)
                .where(Accounts.ACCOUNTS.USERNAME.eq(username))
                .fetchOptionalInto(AccountDto.class);
    }

    public Optional<AccountFullDto> findFullByUsername(String username) {
        return dsl.selectFrom(Accounts.ACCOUNTS)
                .where(Accounts.ACCOUNTS.USERNAME.eq(username))
                .fetchOptionalInto(AccountFullDto.class);
    }

    public List<AccountDto> findByChatId(Long chatId) {
        return dsl.select(Accounts.ACCOUNTS.ID, Accounts.ACCOUNTS.PHONE_NUMBER, Accounts.ACCOUNTS.USERNAME)
                .from(Accounts.ACCOUNTS
                        .join(AccountsChats.ACCOUNTS_CHATS)
                        .on(Accounts.ACCOUNTS.ID.eq(AccountsChats.ACCOUNTS_CHATS.ACCOUNT_ID)))
                .where(AccountsChats.ACCOUNTS_CHATS.CHAT_ID.eq(chatId))
                .fetchInto(AccountDto.class);
    }

    public void addUserToChat(Long chatId, Long userId) {
        dsl.insertInto(AccountsChats.ACCOUNTS_CHATS)
                .values(chatId, userId)
                .execute();
    }
}
