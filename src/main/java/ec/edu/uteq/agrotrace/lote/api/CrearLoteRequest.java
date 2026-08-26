package ec.edu.uteq.agrotrace.lote.api;

import ec.edu.uteq.agrotrace.lote.app.RegistrarLoteCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Cuerpo de la peticion de creacion de un lote desde la API REST.
 *
 * @param fincaId                finca de origen
 * @param fechaRecepcion         fecha de recepcion; si es nula se toma hoy
 * @param pesoKg                 peso neto en kilogramos
 * @param humedadPorcentaje      humedad medida
 * @param fermentacionPorcentaje fermentacion medida
 */
@Schema(description = "Datos necesarios para registrar un lote")
public record CrearLoteRequest(

		@NotNull(message = "fincaId es obligatorio")
		@Schema(example = "1")
		Long fincaId,

		@PastOrPresent(message = "La fecha de recepcion no puede ser futura")
		@Schema(example = "2026-08-24")
		LocalDate fechaRecepcion,

		@NotNull(message = "pesoKg es obligatorio")
		@DecimalMin(value = "0.01", message = "El peso debe ser mayor que cero")
		@Schema(example = "120.50")
		BigDecimal pesoKg,

		@NotNull(message = "humedadPorcentaje es obligatorio")
		@DecimalMin(value = "0.00", message = "La humedad no puede ser negativa")
		@DecimalMax(value = "100.00", message = "La humedad no puede superar 100 %")
		@Schema(example = "6.80")
		BigDecimal humedadPorcentaje,

		@NotNull(message = "fermentacionPorcentaje es obligatorio")
		@DecimalMin(value = "0.00", message = "La fermentacion no puede ser negativa")
		@DecimalMax(value = "100.00", message = "La fermentacion no puede superar 100 %")
		@Schema(example = "72.00")
		BigDecimal fermentacionPorcentaje) {

	/**
	 * Traduce la peticion a la orden que entiende el servicio de aplicacion.
	 *
	 * @return orden de registro equivalente
	 */
	public RegistrarLoteCommand toCommand() {
		return new RegistrarLoteCommand(
				fincaId, fechaRecepcion, pesoKg, humedadPorcentaje, fermentacionPorcentaje);
	}
}
