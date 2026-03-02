package com.aulaclick.service;

import com.aulaclick.dto.LoginRequest;
import com.aulaclick.dto.UsuarioResponse;
import com.aulaclick.entity.Usuario;
import com.aulaclick.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioResponse login(LoginRequest request) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(request.getEmail());

        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            // Comparación simple de texto plano para el prototipo estudiantil
            if (usuario.getPasswordHash().equals(request.getPassword())) {
                return new UsuarioResponse(
                        usuario.getIdUsuario(),
                        usuario.getNombreCompleto(),
                        usuario.getEmail(),
                        usuario.getRole().getNombreRol());
            }
        }

        return null; // O lanza excepción, para el controlador será más fácil manejar null y devolver
                     // 401
    }
}
