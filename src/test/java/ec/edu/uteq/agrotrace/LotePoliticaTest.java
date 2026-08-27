package ec.edu.uteq.agrotrace;

import ec.edu.uteq.agrotrace.lote.domain.EstadoLote;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas de la politica de recepcion de APROCAFA sobre {@code Lote.evaluarEstado()}.
 * Son la comprobacion objetiva del Bloque 1.
 */
class LotePoliticaTest {

	@Test
	@DisplayName("Humedad sobre 7,5 % manda el lote a secado adicional")
	void humedadAltaVaASecado() {
		assertEquals(EstadoLote.SECADO_ADICIONAL, PoliticaFixture.evaluar("8.20", "70.00"));
	}

	@Test
	@DisplayName("Humedad valida con fermentacion bajo 60 % se rechaza")
	void fermentacionBajaSeRechaza() {
		assertEquals(EstadoLote.RECHAZADO, PoliticaFixture.evaluar("6.00", "55.00"));
	}

	@Test
	@DisplayName("Los valores en el limite exacto se aceptan")
	void limitesExactosSeAceptan() {
		assertEquals(EstadoLote.ACEPTADO, PoliticaFixture.evaluar("7.50", "60.00"));
	}
}
