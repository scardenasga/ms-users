package co.edu.unbosque.ms_users.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The persistent class for the usuario_suscripcion database table.
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="usuario_suscripcion")
@NamedQuery(name="UsuarioSuscripcion.findAll", query="SELECT u FROM UsuarioSuscripcion u")
public class UsuarioSuscripcion implements Serializable {
	private static final long serialVersionUID = 1L;

	
	
	@Id
	@Column(name = "id_usuario")
	private String idUsuario;

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

	@OneToOne
	@JoinColumn(name = "id_usuario", insertable = false, updatable = false)
	private Usuario usuario;



}