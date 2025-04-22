package co.edu.unbosque.ms_users.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * The primary key class for the usuario_suscripcion database table.
 * 
 */
@Embeddable
public class UsuarioSuscripcionPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="id_usuario", insertable=false, updatable=false)
	private String idUsuario;

	@Column(name="id_suscripcion")
	private long idSuscripcion;

}