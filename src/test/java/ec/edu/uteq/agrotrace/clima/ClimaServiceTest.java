package ec.edu.uteq.agrotrace.clima;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Degradacion elegante del Bloque 4: cuando el origen meteorologico no
 * responde, {@code consultarTolerante()} devuelve un pronostico "no disponible"
 * en lugar de propagar el error y romper el tablero del tecnico.
 */
class ClimaServiceTest {

	private ClimaService nuevoServicio(ClimaService self) {
		return new ClimaService(RestClient.builder(), self);
	}

	@Test
	@DisplayName("Si el origen responde 5xx, se sirve un pronostico no disponible")
	void degradaAnteClimaNoDisponible() {
		ClimaService self = mock(ClimaService.class);
		when(self.consultar()).thenThrow(new ClimaNoDisponibleException("origen 503"));

		PronosticoSecado resultado = nuevoServicio(self).consultarTolerante();

		assertEquals("no-disponible", resultado.zona());
		assertFalse(resultado.tieneDatos());
	}

	@Test
	@DisplayName("Si la conexion con el origen falla, tampoco se rompe el tablero")
	void degradaAnteFalloDeRed() {
		ClimaService self = mock(ClimaService.class);
		when(self.consultar()).thenThrow(new ResourceAccessException("connection refused"));

		PronosticoSecado resultado = nuevoServicio(self).consultarTolerante();

		assertEquals("no-disponible", resultado.zona());
	}
}
