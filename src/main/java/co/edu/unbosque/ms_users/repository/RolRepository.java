package co.edu.unbosque.ms_users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.ms_users.model.entity.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    @Query(value = "SELECT * FROM rol WHERE nombre = :nombre", nativeQuery = true)
    Optional<Rol> findByNombre(@Param("nombre") String nombre);
    
}
