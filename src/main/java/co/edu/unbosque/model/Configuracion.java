package co.edu.unbosque.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import co.edu.unbosque.model.entity.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "configuracion")
public class Configuracion {

	@Id
	@Column(name = "id_usuario", length = 255)
	private String idUsuario;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@MapsId
	@JoinColumn(name = "id_usuario", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Usuario usuario;

	@Column(name = "moneda_base", nullable = false)
	private String monedaBase = "USD";

	@Column(name = "recibir_notificaciones", nullable = false)
	private boolean recibirNotificaciones = true;


	public Configuracion() {
		// TODO Auto-generated constructor stub
	}

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getMonedaBase() {
		return monedaBase;
	}

	public void setMonedaBase(String monedaBase) {
		this.monedaBase = monedaBase;
	}

	public boolean isRecibirNotificaciones() {
		return recibirNotificaciones;
	}

	public void setRecibirNotificaciones(boolean recibirNotificaciones) {
		this.recibirNotificaciones = recibirNotificaciones;
	}

	
}
