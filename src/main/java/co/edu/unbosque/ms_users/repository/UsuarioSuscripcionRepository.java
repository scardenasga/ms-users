package co.edu.unbosque.ms_users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.ms_users.model.UsuarioSuscripcion;
import co.edu.unbosque.ms_users.model.UsuarioSuscripcionPK;

public interface UsuarioSuscripcionRepository extends JpaRepository<UsuarioSuscripcion, UsuarioSuscripcionPK> {
    
}
