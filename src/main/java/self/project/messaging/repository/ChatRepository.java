package self.project.messaging.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import self.project.messaging.model.Chat;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("select c from Chat c join c.participants a where a.id = :userId")
    List<Chat> findByUserId(Long userId);
}
