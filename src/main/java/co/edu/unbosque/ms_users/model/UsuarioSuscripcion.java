package co.edu.unbosque.ms_users.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;


/**
 * The persistent class for the usuario_suscripcion database table.
 * 
 */
@Data
@Builder
@Entity
@Table(name="usuario_suscripcion")
@NamedQuery(name="UsuarioSuscripcion.findAll", query="SELECT u FROM UsuarioSuscripcion u")
public class UsuarioSuscripcion implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private UsuarioSuscripcionPK id;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado", nullable = false)
	@Builder.Default
	private EstadoSuscripcion estado = EstadoSuscripcion.ACTIVA;

	@Column(name="fecha_fin", nullable = false)
	private LocalDateTime fechaFin;

	@Column(name="fecha_inicio", nullable = false)
	private LocalDateTime fechaInicio;

	@Column(name="nombre", nullable = false, length = 50)
	private String nombre;

	@Column(name="precio", nullable = false, precision = 10, scale = 2)
	private BigDecimal precio;

	//bi-directional many-to-one association to Usuario
	@ManyToOne
	@JoinColumn(name="id_usuario")
	private Usuario usuario;


}