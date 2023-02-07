package itstep.social_freedom.repository;

import itstep.social_freedom.entity.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteRepository extends JpaRepository<Invite, Long> {
}
