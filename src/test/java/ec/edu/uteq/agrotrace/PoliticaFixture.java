package ec.edu.uteq.agrotrace;

import ec.edu.uteq.agrotrace.lote.domain.EstadoLote;
import ec.edu.uteq.agrotrace.lote.domain.Lote;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Ayuda de pruebas: construye un lote suelto, sin base de datos, y evalua su
 * politica de recepcion. Permite probar la regla de negocio en aislamiento,
 * que es exactamente la ventaja de tener la regla dentro de la entidad.
 */
final class PoliticaFixture {

	private PoliticaFixture() {
	}

	/**
	 * Evalua la politica con los valores dados.
	 *
	 * @param humedad      humedad en porcentaje, como texto decimal
	 * @param fermentacion fermentacion en porcentaje, como texto decimal
	 * @return estado resultante
	 */
	static EstadoLote evaluar(String humedad, String fermentacion) {
		Lote lote = new Lote(
				"LT-000000",
				null,
				LocalDate.now(),
				new BigDecimal("100.00"),
				new BigDecimal(humedad),
				new BigDecimal(fermentacion));
		return lote.getEstado();
	}
}
