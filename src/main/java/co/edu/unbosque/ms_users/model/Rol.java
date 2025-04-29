package co.edu.unbosque.ms_users.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import lombok.Data;


/**
 * The persistent class for the rol database table.
 * 
 */
@Data
@Entity
@NamedQuery(name="Rol.findAll", query="SELECT r FROM Rol r")
public class Rol implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_rol")
	private long idRol;

	@Enumerated(EnumType.STRING)
	@Column(name="nombre", unique = true, nullable = false)
	private TipoRol nombre;

	//bi-directional many-to-one association to Usuario
	@OneToMany(mappedBy="rol", fetch = FetchType.EAGER)
	private List<Usuario> usuarios;

}