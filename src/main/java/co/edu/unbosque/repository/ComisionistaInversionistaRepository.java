package co.edu.unbosque.repository;


import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.model.ComisionistaInversionista;
import co.edu.unbosque.model.ComisionistaInversionistaId;


public interface ComisionistaInversionistaRepository extends JpaRepository<ComisionistaInversionista, ComisionistaInversionistaId>{

	List<ComisionistaInversionista> findById_IdComisionista(String idComisionista);
	List<ComisionistaInversionista> findById_IdInversionista(String idInversionista);
}
