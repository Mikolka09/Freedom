package itstep.social_freedom.service;

import itstep.social_freedom.entity.Alert;
import itstep.social_freedom.entity.Status;
import itstep.social_freedom.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    public List<Alert> allAlerts(){
        return alertRepository.findAll();
    }

    public List<Alert> findAllAlertsUserById(Long userId){
        return alertRepository.findAll().stream()
                .filter(x-> Objects.equals(x.getInvite().getUserTo().getId(), userId)).collect(Collectors.toList());
    }

    public boolean saveAlert(Alert alert) {
        Alert alertBD = alertRepository.findAlertById(alert.getId());
        if (alertBD != null) {
            if (!Objects.equals(alertBD.getStatus(), alert.getStatus()))
                return false;
        }
        alertRepository.save(alert);
        return true;
    }

    public void deleteAlert(Long id) {
        if (alertRepository.findById(id).isPresent()) {
            Alert alert = alertRepository.findById(id).orElse(new Alert());
            alert.setStatus(Status.DELETED);
            alertRepository.save(alert);
        }
    }
}
