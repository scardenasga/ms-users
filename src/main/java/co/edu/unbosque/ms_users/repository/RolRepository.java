package co.edu.unbosque.ms_users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.ms_users.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    
}
