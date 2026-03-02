package com.aulaclick.config;

import com.aulaclick.entity.Departamento;
import com.aulaclick.entity.Role;
import com.aulaclick.entity.Usuario;
import com.aulaclick.repository.DepartamentoRepository;
import com.aulaclick.repository.RoleRepository;
import com.aulaclick.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final DepartamentoRepository departamentoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {

        // 1. Create Roles if none exist
        if (roleRepository.count() == 0) {
            Role roleAdmin = new Role();
            roleAdmin.setNombreRol("ADMIN");
            roleRepository.save(roleAdmin);

            Role roleProfesor = new Role();
            roleProfesor.setNombreRol("PROFESOR");
            roleRepository.save(roleProfesor);
        }

        // 2. Create Departamento if none exist
        if (departamentoRepository.count() == 0) {
            Departamento deptInformatica = new Departamento();
            deptInformatica.setNombreDepartamento("Informática");
            departamentoRepository.save(deptInformatica);
        }

        // 3. Create Admin User if none exist
        if (usuarioRepository.count() == 0) {
            // Retrieve the 'ADMIN' role
            Optional<Role> adminRoleOpt = roleRepository.findByNombreRol("ADMIN");

            if (adminRoleOpt.isPresent()) {
                Usuario adminUser = new Usuario();
                adminUser.setNombreCompleto("Administrador Principal");
                adminUser.setEmail("admin@aulaclick.edu");
                adminUser.setPasswordHash("admin123"); // Password as plain text for student prototype
                adminUser.setRole(adminRoleOpt.get());

                usuarioRepository.save(adminUser);
            }
        }
    }
}
