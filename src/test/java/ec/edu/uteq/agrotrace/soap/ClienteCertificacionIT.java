package ec.edu.uteq.agrotrace.soap;

import ec.edu.uteq.agrotrace.soap.gen.CertificarLoteResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ws.soap.client.SoapFaultClientException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba de integracion del Bloque 3: levanta la aplicacion completa en el
 * puerto 8080 y hace que {@link ClienteCertificacion} consuma el propio
 * endpoint SOAP, tal como lo haria la entidad certificadora.
 *
 * <p>Requiere PostgreSQL y Redis levantados (docker compose). Por eso solo se
 * ejecuta cuando se pide de forma explicita:</p>
 * <pre>.\mvnw.cmd test -Dagrotrace.it=true</pre>
 * <p>y, en esta maquina, con el contenedor publicado en 5433:</p>
 * <pre>$env:SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5433/agrotrace'</pre>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnabledIfSystemProperty(named = "agrotrace.it", matches = "true")
class ClienteCertificacionIT {

	@Autowired
	private ClienteCertificacion cliente;

	@Test
	@DisplayName("El cliente Java obtiene el mismo certificado que curl")
	void clienteObtieneCertificado() {
		CertificarLoteResponse respuesta = cliente.certificar("LT-000002", "1204567890");

		assertNotNull(respuesta.getNumeroCertificado());
		assertEquals("LT-000002", respuesta.getCodigoLote());
		assertTrue(respuesta.isVigente());
	}

	@Test
	@DisplayName("Un codigo mal formado produce un SOAP Fault de validacion")
	void codigoMalFormadoEsFault() {
		SoapFaultClientException fault = assertThrows(SoapFaultClientException.class,
				() -> cliente.certificar("CACAO-1", "1204567890"));

		assertTrue(fault.getFaultStringOrReason().toLowerCase().contains("validation"));
	}
}
