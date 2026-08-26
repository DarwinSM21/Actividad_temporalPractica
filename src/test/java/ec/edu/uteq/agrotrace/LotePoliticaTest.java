package ec.edu.uteq.agrotrace;

import ec.edu.uteq.agrotrace.lote.domain.EstadoLote;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas de la politica de recepcion de APROCAFA.
 *
 * <p>Estan anotadas con {@code @Disabled} porque el esqueleto todavia no
 * implementa {@code Lote.evaluarEstado()}. <strong>Al resolver TODO-GA-01,
 * borre las anotaciones {@code @Disabled} y ejecute
 * {@code .\mvnw.cmd test}</strong>: las tres deben pasar. Son su comprobacion
 * objetiva del Bloque 1, antes de mirar la pantalla.</p>
 */
class LotePoliticaTest {

	@Test
	@Disabled("Habilitar al resolver TODO-GA-01")
	@DisplayName("Humedad sobre 7,5 % manda el lote a secado adicional")
	void humedadAltaVaASecado() {
		assertEquals(EstadoLote.SECADO_ADICIONAL, PoliticaFixture.evaluar("8.20", "70.00"));
	}

	@Test
	@Disabled("Habilitar al resolver TODO-GA-01")
	@DisplayName("Humedad valida con fermentacion bajo 60 % se rechaza")
	void fermentacionBajaSeRechaza() {
		assertEquals(EstadoLote.RECHAZADO, PoliticaFixture.evaluar("6.00", "55.00"));
	}

	@Test
	@Disabled("Habilitar al resolver TODO-GA-01")
	@DisplayName("Los valores en el limite exacto se aceptan")
	void limitesExactosSeAceptan() {
		assertEquals(EstadoLote.ACEPTADO, PoliticaFixture.evaluar("7.50", "60.00"));
	}
}
