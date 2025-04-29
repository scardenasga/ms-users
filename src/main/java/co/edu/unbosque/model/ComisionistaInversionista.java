package co.edu.unbosque.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "comisionista_usuario")
public class ComisionistaInversionista {
	
	@EmbeddedId
	private ComisionistaInversionistaId id;
	
	@MapsId("idComisionista")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_comisionista", nullable = false)
	 @OnDelete(action = OnDeleteAction.CASCADE)
	private Comisionista comisionista;

	
	@MapsId("idInversionista")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_inversionista", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Inversionista inversionista;
	
	public ComisionistaInversionista() {
		// TODO Auto-generated constructor stub
	}

	public ComisionistaInversionistaId getId() {
		return id;
	}

	public void setId(ComisionistaInversionistaId id) {
		this.id = id;
	}

	public Comisionista getComisionista() {
		return comisionista;
	}

	public void setComisionista(Comisionista comisionista) {
		this.comisionista = comisionista;
	}

	public Inversionista getInversionista() {
		return inversionista;
	}

	public void setInversionista(Inversionista inversionista) {
		this.inversionista = inversionista;
	}

	
	
	
	

}
