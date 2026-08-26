package ec.edu.uteq.agrotrace.finca.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Finca productora afiliada a APROCAFA. Es el origen de cada lote de cacao.
 */
@Entity
@Table(name = "finca")
public class Finca {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String nombre;

	@Column(nullable = false, length = 120)
	private String productor;

	@Column(nullable = false, length = 60)
	private String canton;

	@Column(nullable = false, precision = 6, scale = 2)
	private BigDecimal hectareas;

	@Column(name = "codigo_registro", nullable = false, unique = true, length = 20)
	private String codigoRegistro;

	protected Finca() {
		// Requerido por JPA.
	}

	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public String getProductor() {
		return productor;
	}

	public String getCanton() {
		return canton;
	}

	public BigDecimal getHectareas() {
		return hectareas;
	}

	public String getCodigoRegistro() {
		return codigoRegistro;
	}
}
