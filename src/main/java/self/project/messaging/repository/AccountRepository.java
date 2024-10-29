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

    public List<AccountDto> findByUsernameStartingWith(String username) {
        return dsl.selectFrom(Accounts.ACCOUNTS)
                .where(Accounts.ACCOUNTS.USERNAME.like("%" + username + "%")) // returns any account which username contains specified argument
                .limit(20)
                .fetchInto(AccountDto.class);
    }

    public List<AccountDto> findByChatId(Long chatId) {
        var A = Accounts.ACCOUNTS;
        var AC = AccountsChats.ACCOUNTS_CHATS;
        return dsl.select(A.ID, A.PHONE_NUMBER, A.USERNAME)
                .from(A.join(AC)
                        .on(A.ID.eq(AC.ACCOUNT_ID)))
                .where(AC.CHAT_ID.eq(chatId))
                .fetchInto(AccountDto.class);
    }

    public void addUserToChat(Long chatId, Long userId) {
        dsl.insertInto(AccountsChats.ACCOUNTS_CHATS)
                .values(chatId, userId)
                .execute();
    }
}
