package co.edu.unbosque.ms_users.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.ms_users.model.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    

    @Query(value = "SELECT * FROM usuario WHERE estado != :estado", nativeQuery = true)
    List<Usuario> findByNoEstado(@Param("estado") String estado);
}