package self.project.messaging.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import self.project.messaging.model.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
