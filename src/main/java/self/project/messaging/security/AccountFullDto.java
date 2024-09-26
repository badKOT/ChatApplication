package self.project.messaging.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountFullDto {
    private Long id;
    private String password;
    private String phoneNumber;
    private String username;
    private String role;
}
