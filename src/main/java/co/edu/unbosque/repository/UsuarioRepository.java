package co.edu.unbosque.repository;



import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    public boolean existsByEmail(String email);
    public boolean existsByContrasena(String password);
    public Usuario findByEmail(String email);
   @Query("SELECT u.saldo FROM Usuario u WHERE u.email = :email")
    Optional<BigDecimal> findSaldoByEmail(@Param("email") String email);

    
    public void deleteByEmail(String email);
}
