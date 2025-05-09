package co.edu.unbosque.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    public boolean existsByEmail(String email);
    
    public Usuario findByEmail(String email);
    public void deleteByEmail(String email);
}
