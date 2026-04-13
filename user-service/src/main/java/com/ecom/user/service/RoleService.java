package com.ecom.user.service;

import com.ecom.user.model.Role;
import com.ecom.user.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> getRole(String rolename) {
        return roleRepository.findByName("ROLE_USER");
    }

}
