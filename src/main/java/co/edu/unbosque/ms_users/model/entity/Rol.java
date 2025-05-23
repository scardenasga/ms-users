package co.edu.unbosque.ms_users.model.entity;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import lombok.NoArgsConstructor;


/**
 * The persistent class for the rol database table.
 * 
 */
@Data
@NoArgsConstructor
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
	@JsonIgnore
	@OneToMany(mappedBy="rol", fetch = FetchType.LAZY)
	private List<Usuario> usuarios;

}