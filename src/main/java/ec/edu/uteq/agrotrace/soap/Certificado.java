package ec.edu.uteq.agrotrace.soap;

import ec.edu.uteq.agrotrace.lote.domain.Lote;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Certificado oficial emitido para un lote, a solicitud de la entidad
 * certificadora nacional a traves del servicio SOAP.
 */
@Entity
@Table(name = "certificado")
public class Certificado {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 24)
	private String numero;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "lote_id", nullable = false)
	private Lote lote;

	@Column(name = "cedula_tecnico", nullable = false, length = 10)
	private String cedulaTecnico;

	@Column(name = "fecha_emision", nullable = false)
	private LocalDate fechaEmision;

	@Column(nullable = false)
	private boolean vigente;

	protected Certificado() {
		// Requerido por JPA.
	}

	public Certificado(String numero, Lote lote, String cedulaTecnico) {
		this.numero = numero;
		this.lote = lote;
		this.cedulaTecnico = cedulaTecnico;
		this.fechaEmision = LocalDate.now();
		this.vigente = true;
	}

	public Long getId() {
		return id;
	}

	public String getNumero() {
		return numero;
	}

	public Lote getLote() {
		return lote;
	}

	public String getCedulaTecnico() {
		return cedulaTecnico;
	}

	public LocalDate getFechaEmision() {
		return fechaEmision;
	}

	public boolean isVigente() {
		return vigente;
	}
}
