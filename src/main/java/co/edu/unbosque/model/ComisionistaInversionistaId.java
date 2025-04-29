package co.edu.unbosque.model;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.Hibernate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ComisionistaInversionistaId implements Serializable {

	private static final long serialVersionUID = 5573493780495296100L;

	@Column(name = "id_comisionista", nullable = false)
	private String idComisionista;

	@Column(name = "id_inversionista", nullable = false)
	private String idInversionista;

	public ComisionistaInversionistaId() {
		// TODO Auto-generated constructor stub
	}

	public String getIdComisionista() {
		return idComisionista;
	}

	public void setIdComisionista(String idComisionista) {
		this.idComisionista = idComisionista;
	}

	public String getIdInversionista() {
		return idInversionista;
	}

	public void setIdInversionista(String idInversionista) {
		this.idInversionista = idInversionista;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
			return false;
		ComisionistaInversionistaId that = (ComisionistaInversionistaId) o;
		return Objects.equals(idComisionista, that.idComisionista)
				&& Objects.equals(idInversionista, that.idInversionista);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idComisionista, idInversionista);
	}
}
