package co.edu.unbosque.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.model.entity.Configuracion;

public interface ConfiguracionRepository extends JpaRepository<Configuracion, String>{

    Configuracion findByIdUsuario(String idUsuario); 
    
}
