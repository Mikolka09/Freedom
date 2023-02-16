package itstep.social_freedom.service;

import itstep.social_freedom.entity.Role;
import itstep.social_freedom.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> allRoles(){
        return roleRepository.findAll();
    }

    public Role findRoleById(Long id){
        return roleRepository.findById(id).orElse(new Role());
    }
}
