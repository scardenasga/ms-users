package co.edu.unbosque.model;




import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {
	@Id
    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

	    @Column(name = "primer_nombre", length = 50, nullable = false)
	    private String primerNombre;

	    @Column(name = "segundo_nombre", length = 50)
	    private String segundoNombre;

	    @Column(name = "primer_apellido", length = 50, nullable = false)
	    private String primerApellido;

	    @Column(name = "segundo_apellido", length = 50)
	    private String segundoApellido;


	    @Column(name = "telefono", length = 10, nullable = false)
	    private String telefono;

	    @Column(name = "contrasena", length = 255, nullable = false)
	    private String contrasena;
	    

	    @Column(name = "fecha_registro", nullable = false)
	    private LocalDateTime fechaRegistro;
	    

		
		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
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


		public LocalDateTime getFechaRegistro() {
			return fechaRegistro;
		}

		public void setFechaRegistro(LocalDateTime fechaRegistro) {
			this.fechaRegistro = fechaRegistro;
		}


}
