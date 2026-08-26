package ec.edu.uteq.agrotrace.lote.web;

import ec.edu.uteq.agrotrace.lote.app.RegistrarLoteCommand;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos del formulario de registro de lote. Es un objeto de la capa de vista:
 * lleva las restricciones de entrada, no las reglas de negocio.
 */
public class NuevoLoteForm {

	@NotNull(message = "Seleccione la finca de origen")
	private Long fincaId;

	@NotNull(message = "Indique la fecha de recepcion")
	@PastOrPresent(message = "La fecha de recepcion no puede ser futura")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fechaRecepcion;

	@NotNull(message = "Indique el peso en kilogramos")
	@DecimalMin(value = "0.01", message = "El peso debe ser mayor que cero")
	private BigDecimal pesoKg;

	@NotNull(message = "Indique la humedad medida")
	@DecimalMin(value = "0.00", message = "La humedad no puede ser negativa")
	@DecimalMax(value = "100.00", message = "La humedad no puede superar 100 %")
	private BigDecimal humedadPorcentaje;

	@NotNull(message = "Indique la fermentacion medida")
	@DecimalMin(value = "0.00", message = "La fermentacion no puede ser negativa")
	@DecimalMax(value = "100.00", message = "La fermentacion no puede superar 100 %")
	private BigDecimal fermentacionPorcentaje;

	/**
	 * Traduce el formulario a la orden que entiende el servicio de aplicacion.
	 *
	 * @return orden de registro equivalente
	 */
	public RegistrarLoteCommand toCommand() {
		return new RegistrarLoteCommand(
				fincaId, fechaRecepcion, pesoKg, humedadPorcentaje, fermentacionPorcentaje);
	}

	public Long getFincaId() {
		return fincaId;
	}

	public void setFincaId(Long fincaId) {
		this.fincaId = fincaId;
	}

	public LocalDate getFechaRecepcion() {
		return fechaRecepcion;
	}

	public void setFechaRecepcion(LocalDate fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}

	public BigDecimal getPesoKg() {
		return pesoKg;
	}

	public void setPesoKg(BigDecimal pesoKg) {
		this.pesoKg = pesoKg;
	}

	public BigDecimal getHumedadPorcentaje() {
		return humedadPorcentaje;
	}

	public void setHumedadPorcentaje(BigDecimal humedadPorcentaje) {
		this.humedadPorcentaje = humedadPorcentaje;
	}

	public BigDecimal getFermentacionPorcentaje() {
		return fermentacionPorcentaje;
	}

	public void setFermentacionPorcentaje(BigDecimal fermentacionPorcentaje) {
		this.fermentacionPorcentaje = fermentacionPorcentaje;
	}
}
