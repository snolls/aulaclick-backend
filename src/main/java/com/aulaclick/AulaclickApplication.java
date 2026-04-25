package com.aulaclick;

import com.aulaclick.entity.Usuario;
import com.aulaclick.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootApplication
public class AulaclickApplication {

	public static void main(String[] args) {
		SpringApplication.run(AulaclickApplication.class, args);
	}

	@Bean
	public CommandLineRunner resetAdminPassword(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			Optional<Usuario> adminOpt = usuarioRepository.findByEmail("admin@aulaclick.edu");
			if (adminOpt.isPresent()) {
				Usuario admin = adminOpt.get();
				admin.setPasswordHash(passwordEncoder.encode("Pruebas1623."));
				usuarioRepository.save(admin);
				System.out.println("Contraseña del admin reseteada con BCrypt exitosamente.");
			}
		};
	}

}
