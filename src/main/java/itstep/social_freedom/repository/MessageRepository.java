package itstep.social_freedom.repository;

import itstep.social_freedom.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Message findMessageById(Long id);
}
