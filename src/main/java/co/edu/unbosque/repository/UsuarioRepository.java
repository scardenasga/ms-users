package co.edu.unbosque.repository;

import co.edu.unbosque.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    public boolean existsByEmail(String email);
    
    public Usuario findByEmail(String email);
    public void deleteByEmail(String email);
}
