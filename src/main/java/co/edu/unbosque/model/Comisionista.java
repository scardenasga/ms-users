package co.edu.unbosque.model;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comisionista")
public class Comisionista {

	@Id
	private String email;

	@OneToOne
	@MapsId
	@JoinColumn(name = "email", referencedColumnName = "email")
	private Usuario usuario;

	@Column(name = "experiencia_anios", nullable = false)
	private int experienciaAnios;

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public int getExperienciaAnios() {
		return experienciaAnios;
	}

	public void setExperienciaAnios(int experienciaAnios) {
		this.experienciaAnios = experienciaAnios;
	}

}
