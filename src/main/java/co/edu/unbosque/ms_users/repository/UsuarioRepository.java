package co.edu.unbosque.ms_users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.ms_users.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    
}