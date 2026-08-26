package ec.edu.uteq.agrotrace.lote.api;

import ec.edu.uteq.agrotrace.lote.domain.EstadoLote;
import ec.edu.uteq.agrotrace.lote.domain.Lote;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representacion publica de un lote para las exportadoras.
 *
 * <p>Se serializa este objeto y nunca la entidad: eso evita exponer el modelo
 * interno, evita LazyInitializationException y permite que la API evolucione
 * sin arrastrar cambios de la base de datos.</p>
 */
@Schema(description = "Lote de cacao disponible en el centro de acopio")
public record LoteResponse(

		@Schema(description = "Codigo de trazabilidad", example = "LT-000001")
		String codigo,

		@Schema(description = "Nombre de la finca de origen", example = "La Envidia")
		String finca,

		@Schema(description = "Canton de la finca", example = "Quevedo")
		String canton,

		@Schema(description = "Fecha de recepcion en el acopio")
		LocalDate fechaRecepcion,

		@Schema(description = "Peso neto en kilogramos", example = "120.50")
		BigDecimal pesoKg,

		@Schema(description = "Humedad medida en porcentaje", example = "6.80")
		BigDecimal humedadPorcentaje,

		@Schema(description = "Fermentacion medida en porcentaje", example = "72.00")
		BigDecimal fermentacionPorcentaje,

		@Schema(description = "Estado resultante de la evaluacion de recepcion")
		EstadoLote estado) {

	/**
	 * Construye la representacion publica a partir de la entidad de dominio.
	 *
	 * @param lote entidad persistida
	 * @return representacion lista para serializar
	 */
	public static LoteResponse desde(Lote lote) {
		return new LoteResponse(
				lote.getCodigo(),
				lote.getFinca().getNombre(),
				lote.getFinca().getCanton(),
				lote.getFechaRecepcion(),
				lote.getPesoKg(),
				lote.getHumedadPorcentaje(),
				lote.getFermentacionPorcentaje(),
				lote.getEstado());
	}
}
