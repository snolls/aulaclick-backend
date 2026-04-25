package com.aulaclick.service;

import com.aulaclick.dto.CambioPasswordDTO;
import com.aulaclick.dto.LoginRequest;
import com.aulaclick.dto.UsuarioResponse;
import com.aulaclick.entity.Usuario;
import com.aulaclick.repository.UsuarioRepository;
import com.aulaclick.security.JwtUtil;
import com.aulaclick.util.PasswordValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UsuarioResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe ninguna cuenta con este correo."));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La contraseña es incorrecta.");
        }

        String rol = usuario.getRole().getNombreRol();
        Long sedeId = usuario.getSede() != null ? usuario.getSede().getIdSede() : null;
        String token = jwtUtil.generarToken(usuario.getIdUsuario(), usuario.getEmail(), rol, sedeId);

        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombreCompleto(),
                usuario.getEmail(),
                rol,
                token);
    }

    public void cambiarPassword(Long idUsuario, CambioPasswordDTO dto) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (!passwordEncoder.matches(dto.getPasswordActual(), usuario.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta");
        }
        PasswordValidatorUtil.validar(dto.getNuevaPassword(), usuario.getNombreCompleto(), usuario.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(dto.getNuevaPassword()));
        usuarioRepository.save(usuario);
    }
}
