package co.edu.unbosque.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id_usuario", nullable = false)
	    private Long idUsuario;

	    @Column(name = "primer_nombre", length = 50, nullable = false)
	    private String primerNombre;

	    @Column(name = "segundo_nombre", length = 50)
	    private String segundoNombre;

	    @Column(name = "primer_apellido", length = 50, nullable = false)
	    private String primerApellido;

	    @Column(name = "segundo_apellido", length = 50)
	    private String segundoApellido;

	    @Column(name = "saldo", precision = 10, scale = 2)
	    private BigDecimal saldo;

	    @Column(name = "email", length = 255, unique = true, nullable = false)
	    private String email;

	    @Column(name = "telefono", length = 10, nullable = false)
	    private String telefono;

	    @Column(name = "contrasena", length = 255, nullable = false)
	    private String contrasena;
	    
	    @Column(name = "estado", columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVO'", nullable = false)
	    private String estado;
	    
	    @Column(name = "fecha_registro", nullable = false)
	    private LocalDateTime fechaRegistro;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "id_rol")
	    private Rol rol;

		public Long getIdUsuario() {
			return idUsuario;
		}

		public void setIdUsuario(Long idUsuario) {
			this.idUsuario = idUsuario;
		}

		public String getPrimerNombre() {
			return primerNombre;
		}

		public void setPrimerNombre(String primerNombre) {
			this.primerNombre = primerNombre;
		}

		public String getSegundoNombre() {
			return segundoNombre;
		}

		public void setSegundoNombre(String segundoNombre) {
			this.segundoNombre = segundoNombre;
		}

		public String getPrimerApellido() {
			return primerApellido;
		}

		public void setPrimerApellido(String primerApellido) {
			this.primerApellido = primerApellido;
		}

		public String getSegundoApellido() {
			return segundoApellido;
		}

		public void setSegundoApellido(String segundoApellido) {
			this.segundoApellido = segundoApellido;
		}

		public BigDecimal getSaldo() {
			return saldo;
		}

		public void setSaldo(BigDecimal saldo) {
			this.saldo = saldo;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getTelefono() {
			return telefono;
		}

		public void setTelefono(String telefono) {
			this.telefono = telefono;
		}

		public String getContrasena() {
			return contrasena;
		}

		public void setContrasena(String contrasena) {
			this.contrasena = contrasena;
		}

		public String getEstado() {
			return estado;
		}

		public void setEstado(String estado) {
			this.estado = estado;
		}

		public LocalDateTime getFechaRegistro() {
			return fechaRegistro;
		}

		public void setFechaRegistro(LocalDateTime fechaRegistro) {
			this.fechaRegistro = fechaRegistro;
		}

		public Rol getRol() {
			return rol;
		}

		public void setRol(Rol rol) {
			this.rol = rol;
		}
	    
	    

}
