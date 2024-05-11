package self.project.messaging.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import self.project.messaging.model.Account;
import self.project.messaging.service.AccountService;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account;
        try {
            account = accountService.findByUsername(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getMessage());
        }

        return new UserDetailsImpl(account);
    }
}
