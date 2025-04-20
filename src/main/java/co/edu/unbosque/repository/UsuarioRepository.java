package co.edu.unbosque.repository;

import co.edu.unbosque.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    public boolean existsByEmail(String email);
}
