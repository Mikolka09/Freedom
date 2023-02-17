package itstep.social_freedom.service;

import itstep.social_freedom.entity.Invite;
import itstep.social_freedom.entity.Status;
import itstep.social_freedom.repository.InviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class InviteService {

    @Autowired
    private InviteRepository inviteRepository;

    public boolean saveInvite(Invite invite){
       List<Invite> inviteBD = inviteRepository.findAll().stream()
               .filter(x-> Objects.equals(x.getUserFrom().getId(), invite.getUserFrom().getId()))
               .collect(Collectors.toList());
       if(inviteBD.stream().noneMatch(x -> x.getStatus() == Status.REQUEST)){
           inviteRepository.save(invite);
           return true;
       }else
           return false;
    }
}
