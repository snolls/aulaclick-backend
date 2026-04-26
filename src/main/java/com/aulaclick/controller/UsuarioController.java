package com.aulaclick.controller;

import com.aulaclick.dto.*;
import com.aulaclick.util.PasswordValidatorUtil;
import com.aulaclick.entity.Role;
import com.aulaclick.entity.Sede;
import com.aulaclick.entity.Usuario;
import com.aulaclick.repository.RoleRepository;
import com.aulaclick.repository.SedeRepository;
import com.aulaclick.repository.UsuarioRepository;
import com.aulaclick.security.SecurityUtils;
import com.aulaclick.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final SedeRepository sedeRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ADMIN_SEDE')")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        String rol = SecurityUtils.getRol();
        List<UsuarioDTO> usuarios;

        if ("ADMIN".equals(rol)) {
            usuarios = usuarioRepository.findAll().stream()
                    .map(UsuarioDTO::fromEntity).toList();
        } else {
            Long sedeId = SecurityUtils.getSedeIdOrForbidden();
            usuarios = usuarioRepository.findBySedeIdSede(sedeId).stream()
                    .filter(u -> !"ADMIN".equals(u.getRole().getNombreRol()))
                    .map(UsuarioDTO::fromEntity).toList();
        }

        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/verificar-email")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ADMIN_SEDE')")
    public ResponseEntity<Void> verificarEmail(@RequestParam String email) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ADMIN_SEDE')")
    public ResponseEntity<UsuarioDTO> crearUsuario(@RequestBody CrearUsuarioDTO dto) {
        String rolPeticionario = SecurityUtils.getRol();
        if (!"ADMIN".equals(rolPeticionario) && !"ADMIN_SEDE".equals(rolPeticionario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (dto.getIdRol() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol es obligatorio.");
        }
        Role role = roleRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol seleccionado no existe."));

        if ("ADMIN_SEDE".equals(rolPeticionario)) {
            if ("ADMIN".equals(role.getNombreRol())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Un Admin de sede no puede crear usuarios con rol ADMIN.");
            }
            dto.setIdSede(SecurityUtils.getSedeIdOrForbidden());
        }

        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está en uso.");
        }

        PasswordValidatorUtil.validar(dto.getPassword(), dto.getNombreCompleto(), dto.getEmail());

        // Los ADMIN globales no pertenecen a ninguna sede
        Sede sede = null;
        if (!"ADMIN".equals(role.getNombreRol()) && dto.getIdSede() != null) {
            sede = sedeRepository.findById(dto.getIdSede())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La sede seleccionada no existe."));
        }

        Usuario nuevo = new Usuario();
        nuevo.setEmail(dto.getEmail());
        nuevo.setNombreCompleto(dto.getNombreCompleto());
        nuevo.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        nuevo.setRole(role);
        nuevo.setSede(sede);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioDTO.fromEntity(usuarioRepository.save(nuevo)));
    }

    @PutMapping("/{id}/rol-sede")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ADMIN_SEDE')")
    public ResponseEntity<UsuarioDTO> actualizarRolSede(@PathVariable Long id,
                                                         @RequestBody ActualizarRolSedeDTO dto) {
        String rolPeticionario = SecurityUtils.getRol();
        if (!"ADMIN".equals(rolPeticionario) && !"ADMIN_SEDE".equals(rolPeticionario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));

        if (dto.getIdRol() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol es obligatorio.");
        }
        Role nuevoRol = roleRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol seleccionado no existe."));

        if ("ADMIN_SEDE".equals(rolPeticionario)) {
            Long sedeId = SecurityUtils.getSedeIdOrForbidden();

            if (usuario.getSede() == null || !sedeId.equals(usuario.getSede().getIdSede())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Solo puedes modificar usuarios de tu propia sede.");
            }
            if ("ADMIN".equals(nuevoRol.getNombreRol())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Un Admin de sede no puede ascender a rol ADMIN.");
            }
            if (dto.getIdSede() != null && !sedeId.equals(dto.getIdSede())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "No puedes mover usuarios a otra sede.");
            }
        }

        // Actualizar nombre y email si se proporcionan
        if (dto.getNombreCompleto() != null && !dto.getNombreCompleto().isBlank()) {
            usuario.setNombreCompleto(dto.getNombreCompleto());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && !dto.getEmail().equalsIgnoreCase(usuario.getEmail())) {
            if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está en uso.");
            }
            usuario.setEmail(dto.getEmail());
        }

        usuario.setRole(nuevoRol);

        // Los ADMIN globales no pertenecen a ninguna sede
        if ("ADMIN".equals(nuevoRol.getNombreRol())) {
            usuario.setSede(null);
        } else if (dto.getIdSede() != null) {
            Sede sede = sedeRepository.findById(dto.getIdSede())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sede no encontrada."));
            usuario.setSede(sede);
        } else {
            usuario.setSede(null);
        }

        return ResponseEntity.ok(UsuarioDTO.fromEntity(usuarioRepository.save(usuario)));
    }

    @PutMapping("/{id}/admin-cambiar-password")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ADMIN_SEDE')")
    public ResponseEntity<Void> adminCambiarPassword(@PathVariable Long id,
                                                      @RequestBody AdminCambiarPasswordDTO dto) {
        String rolPeticionario = SecurityUtils.getRol();
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));

        if ("ADMIN_SEDE".equals(rolPeticionario)) {
            Long sedeId = SecurityUtils.getSedeIdOrForbidden();
            if (usuario.getSede() == null || !sedeId.equals(usuario.getSede().getIdSede())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Solo puedes cambiar contraseñas de usuarios de tu propia sede.");
            }
            if ("ADMIN".equals(usuario.getRole().getNombreRol())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "No puedes cambiar la contraseña de un administrador global.");
            }
        }

        PasswordValidatorUtil.validar(dto.getNuevaPassword(), usuario.getNombreCompleto(), usuario.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(dto.getNuevaPassword()));
        usuarioRepository.save(usuario);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ADMIN_SEDE')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        String rolPeticionario = SecurityUtils.getRol();
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));

        if ("ADMIN_SEDE".equals(rolPeticionario)) {
            Long sedeId = SecurityUtils.getSedeIdOrForbidden();
            if (usuario.getSede() == null || !sedeId.equals(usuario.getSede().getIdSede())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Solo puedes eliminar usuarios de tu propia sede.");
            }
            if ("ADMIN".equals(usuario.getRole().getNombreRol())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "No puedes eliminar un administrador global.");
            }
        }

        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cambiar-password")
    public ResponseEntity<Void> cambiarPassword(@PathVariable Long id, @RequestBody CambioPasswordDTO dto) {
        usuarioService.cambiarPassword(id, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@RequestBody LoginRequest request) {
        UsuarioResponse response = usuarioService.login(request);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
