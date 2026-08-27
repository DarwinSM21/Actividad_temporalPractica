package ec.edu.uteq.agrotrace.soap;

import ec.edu.uteq.agrotrace.soap.gen.CertificarLoteRequest;
import ec.edu.uteq.agrotrace.soap.gen.CertificarLoteResponse;
import org.springframework.boot.webservices.client.WebServiceTemplateBuilder;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;

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

	public ClienteCertificacion(WebServiceTemplateBuilder constructor, Jaxb2Marshaller marshaller) {
		// Spring Boot entrega un WebServiceTemplateBuilder pero no un
		// WebServiceTemplate ni un marshaller ya configurados: el marshaller se
		// toma del bean definido en ConfiguracionSoap.
		this.plantilla = constructor
				.setDefaultUri("http://localhost:8080/ws")
				.setMarshaller(marshaller)
				.setUnmarshaller(marshaller)
				.build();
	}

	/**
	 * Solicita al servicio SOAP la certificacion de un lote.
	 *
	 * @param codigoLote    codigo con formato LT-000000
	 * @param cedulaTecnico cedula del tecnico solicitante
	 * @return la respuesta del servicio, del tipo generado desde el XSD
	 */
	public CertificarLoteResponse certificar(String codigoLote, String cedulaTecnico) {
		CertificarLoteRequest peticion = new CertificarLoteRequest();
		peticion.setCodigoLote(codigoLote);
		peticion.setCedulaTecnico(cedulaTecnico);
		return (CertificarLoteResponse) plantilla.marshalSendAndReceive(
				peticion, new SoapActionCallback(ACCION_CERTIFICAR));
	}
}
