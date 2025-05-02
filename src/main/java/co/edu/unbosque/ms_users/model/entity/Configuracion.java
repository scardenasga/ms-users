package co.edu.unbosque.ms_users.model.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * The persistent class for the configuracion database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@NamedQuery(name="Configuracion.findAll", query="SELECT c FROM Configuracion c")
public class Configuracion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id_usuario", length = 255)
	private String idUsuario;

	@Enumerated(EnumType.STRING)
	@Column(name="moneda_base", nullable = false, columnDefinition = "ENUM('USD', 'EUR', 'BTC') DEFAULT 'USD'" )
	@Builder.Default
	private Moneda monedaBase = Moneda.USD;

	@Builder.Default
	@Column(name="recibir_notificaciones", nullable = false)
	private Boolean recibirNotificaciones = true;

	//bi-directional one-to-one association to Usuario
	@OneToOne
	@JoinColumn(name="id_usuario")
	private Usuario usuario;


}