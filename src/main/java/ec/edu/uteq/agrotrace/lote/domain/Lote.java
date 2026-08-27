package ec.edu.uteq.agrotrace.lote.domain;

import ec.edu.uteq.agrotrace.finca.domain.Finca;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Lote de cacao recibido en el centro de acopio.
 *
 * <p>Esta entidad concentra la unica regla de negocio de la practica: decidir
 * su propio estado a partir de la humedad y la fermentacion medidas. La regla
 * vive aqui, y no en el servicio ni en el controlador, porque solo necesita
 * datos del propio objeto para resolverse.</p>
 */
@Entity
@Table(name = "lote")
public class Lote {

	/** Por encima de este porcentaje de humedad el grano se enmohece. */
	public static final BigDecimal UMBRAL_HUMEDAD = new BigDecimal("7.50");

	/** Por debajo de este porcentaje de fermentacion no es cacao fino de aroma. */
	public static final BigDecimal UMBRAL_FERMENTACION = new BigDecimal("60.00");

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 12)
	private String codigo;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "finca_id", nullable = false)
	private Finca finca;

	@Column(name = "fecha_recepcion", nullable = false)
	private LocalDate fechaRecepcion;

	@Column(name = "peso_kg", nullable = false, precision = 8, scale = 2)
	private BigDecimal pesoKg;

	@Column(name = "humedad_porcentaje", nullable = false, precision = 4, scale = 2)
	private BigDecimal humedadPorcentaje;

	@Column(name = "fermentacion_porcentaje", nullable = false, precision = 4, scale = 2)
	private BigDecimal fermentacionPorcentaje;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoLote estado;

	@Column(name = "creado_en", nullable = false)
	private Instant creadoEn;

	protected Lote() {
		// Requerido por JPA.
	}

	public Lote(String codigo, Finca finca, LocalDate fechaRecepcion, BigDecimal pesoKg,
			BigDecimal humedadPorcentaje, BigDecimal fermentacionPorcentaje) {
		this.codigo = codigo;
		this.finca = finca;
		this.fechaRecepcion = fechaRecepcion;
		this.pesoKg = pesoKg;
		this.humedadPorcentaje = humedadPorcentaje;
		this.fermentacionPorcentaje = fermentacionPorcentaje;
		this.creadoEn = Instant.now();
		this.estado = evaluarEstado();
	}

	/**
	 * Aplica la politica de recepcion de APROCAFA y devuelve el estado que
	 * corresponde a este lote.
	 *
	 * <p>Politica vigente:</p>
	 * <ul>
	 *   <li>Humedad mayor que 7,50 % =&gt; {@link EstadoLote#SECADO_ADICIONAL}.</li>
	 *   <li>Humedad valida y fermentacion menor que 60,00 % =&gt; {@link EstadoLote#RECHAZADO}.</li>
	 *   <li>En cualquier otro caso =&gt; {@link EstadoLote#ACEPTADO}.</li>
	 * </ul>
	 *
	 * @return el estado resultante de la evaluacion
	 * @throws IllegalStateException si falta la humedad o la fermentacion
	 */
	public EstadoLote evaluarEstado() {
		if (this.humedadPorcentaje == null || this.fermentacionPorcentaje == null) {
			throw new IllegalStateException(
					"No se puede evaluar un lote sin humedad y fermentacion medidas");
		}
		if (this.humedadPorcentaje.compareTo(UMBRAL_HUMEDAD) > 0) {
			return EstadoLote.SECADO_ADICIONAL;
		}
		if (this.fermentacionPorcentaje.compareTo(UMBRAL_FERMENTACION) < 0) {
			return EstadoLote.RECHAZADO;
		}
		return EstadoLote.ACEPTADO;
	}

	/**
	 * Vuelve a evaluar el estado tras una nueva medicion de laboratorio.
	 *
	 * @param humedad      nuevo porcentaje de humedad medido
	 * @param fermentacion nuevo porcentaje de fermentacion medido
	 */
	public void remedir(BigDecimal humedad, BigDecimal fermentacion) {
		this.humedadPorcentaje = humedad;
		this.fermentacionPorcentaje = fermentacion;
		this.estado = evaluarEstado();
	}

	public Long getId() {
		return id;
	}

	public String getCodigo() {
		return codigo;
	}

	public Finca getFinca() {
		return finca;
	}

	public LocalDate getFechaRecepcion() {
		return fechaRecepcion;
	}

	public BigDecimal getPesoKg() {
		return pesoKg;
	}

	public BigDecimal getHumedadPorcentaje() {
		return humedadPorcentaje;
	}

	public BigDecimal getFermentacionPorcentaje() {
		return fermentacionPorcentaje;
	}

	public EstadoLote getEstado() {
		return estado;
	}

	public Instant getCreadoEn() {
		return creadoEn;
	}
}
