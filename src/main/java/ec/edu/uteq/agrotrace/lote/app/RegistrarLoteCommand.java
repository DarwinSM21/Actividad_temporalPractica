package ec.edu.uteq.agrotrace.lote.app;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Orden de registro de un lote nuevo. Es un objeto de transporte entre la capa
 * de entrada (web o API) y el servicio de aplicacion: ni la vista ni el
 * controlador conocen la entidad {@code Lote}.
 *
 * @param fincaId                identificador de la finca de origen
 * @param fechaRecepcion         fecha en que el lote entro al acopio
 * @param pesoKg                 peso neto en kilogramos
 * @param humedadPorcentaje      humedad medida, en porcentaje
 * @param fermentacionPorcentaje fermentacion medida, en porcentaje
 */
public record RegistrarLoteCommand(
		Long fincaId,
		LocalDate fechaRecepcion,
		BigDecimal pesoKg,
		BigDecimal humedadPorcentaje,
		BigDecimal fermentacionPorcentaje) {
}
