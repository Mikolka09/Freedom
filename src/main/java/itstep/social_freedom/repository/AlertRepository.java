package itstep.social_freedom.repository;

import itstep.social_freedom.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    Alert findAlertById(Long id);
}
