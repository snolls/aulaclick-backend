package com.aulaclick.controller;

import com.aulaclick.dto.RolDTO;
import com.aulaclick.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RoleRepository roleRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ADMIN_SEDE')")
    public List<RolDTO> listarRoles() {
        return roleRepository.findAll().stream()
                .map(RolDTO::fromEntity)
                .toList();
    }
}
