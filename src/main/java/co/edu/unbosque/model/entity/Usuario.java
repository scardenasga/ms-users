package co.edu.unbosque.model.entity;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u")
public class Usuario implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "email", length = 255)
	private String email;

	@Column(name = "primer_nombre", nullable = false, length = 50)
	private String primerNombre;

	@Column(name = "segundo_nombre", length = 50)
	private String segundoNombre;

	@Column(name = "primer_apellido", nullable = false, length = 50)
	private String primerApellido;

	@Column(name = "segundo_apellido", length = 50)
	private String segundoApellido;

	@Column(name = "saldo", precision = 10, scale = 2)
	private BigDecimal saldo;

	@Column(name = "telefono", nullable = false, length = 10)
	private String telefono;

	@Column(name = "contrasena", nullable = false, length = 255)
	private String contrasena;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	@Column(name = "estado", nullable = false)
	private EstadoUsuario estado = EstadoUsuario.ACTIVO;

	@Column(name = "fecha_registro")
	private LocalDateTime fechaRegistro;

	// bi-directional one-to-one association to Configuracion
	@OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
	private Configuracion configuracion;

	// bi-directional many-to-many association to Usuario
	@ManyToMany
	@JoinTable(name = "comisionista_usuario", joinColumns = {
			@JoinColumn(name = "id_usuario")
	}, inverseJoinColumns = {
			@JoinColumn(name = "id_comisionista")
	})
	private List<Usuario> usuarios1;

	// bi-directional many-to-many association to Usuario
	@ManyToMany(mappedBy = "usuarios1", fetch = FetchType.LAZY)
	private List<Usuario> usuarios2;

	@OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private UsuarioSuscripcion suscripcion;

}
