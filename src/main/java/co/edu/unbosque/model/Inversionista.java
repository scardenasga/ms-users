package co.edu.unbosque.model;

import java.math.BigDecimal;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inversionista")
public class Inversionista {

	@Id
	private String email;

	@OneToOne
	@MapsId
	@JoinColumn(name = "email", referencedColumnName = "email")
	private Usuario usuario;

	@Column(name = "saldo", precision = 10, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;
	
	@Column(name = "estado", nullable = false)
    private String estado = "ACTIVO";

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	
	
}
