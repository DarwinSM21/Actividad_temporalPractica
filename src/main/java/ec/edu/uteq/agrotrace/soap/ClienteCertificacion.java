package ec.edu.uteq.agrotrace.soap;

import org.springframework.boot.webservices.client.WebServiceTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Cliente del propio servicio SOAP de certificacion.
 *
 * <p>Consumir el servicio que uno mismo publica parece redundante, pero es
 * exactamente lo que hace la entidad certificadora desde su plataforma, y es
 * la forma de comprobar que el contrato funciona de extremo a extremo.</p>
 */
@Service
public class ClienteCertificacion {

	/** Accion SOAP declarada en el contrato. */
	public static final String ACCION_CERTIFICAR =
			"https://agrotrace.uteq.edu.ec/soap/certificacion/Certificar";

	private final WebServiceTemplate plantilla;

	public ClienteCertificacion(WebServiceTemplateBuilder constructor) {
		// TODO-GA-11 (parte A): construir la plantilla apuntando a /ws.
		//
		//   this.plantilla = constructor
		//           .setDefaultUri("http://localhost:8080/ws")
		//           .build();
		//
		// Spring Boot no expone un bean WebServiceTemplate ya configurado:
		// entrega un WebServiceTemplateBuilder para que usted lo construya.
		this.plantilla = null;
	}

	/**
	 * Solicita al servicio SOAP la certificacion de un lote.
	 *
	 * @param codigoLote    codigo con formato LT-000000
	 * @param cedulaTecnico cedula del tecnico solicitante
	 * @return la respuesta del servicio, del tipo generado desde el XSD
	 */
	public Object certificar(String codigoLote, String cedulaTecnico) {
		// TODO-GA-11 (parte B): invocar el servicio.
		//
		//   CertificarLoteRequest peticion = new CertificarLoteRequest();
		//   peticion.setCodigoLote(codigoLote);
		//   peticion.setCedulaTecnico(cedulaTecnico);
		//   return (CertificarLoteResponse) plantilla.marshalSendAndReceive(
		//           peticion, new SoapActionCallback(ACCION_CERTIFICAR));
		//
		// Cambie el tipo de retorno Object por CertificarLoteResponse una vez
		// que haya generado las clases con: .\mvnw.cmd generate-sources
		throw new UnsupportedOperationException(
				"TODO-GA-11: completar ClienteCertificacion.certificar()");
	}
}
