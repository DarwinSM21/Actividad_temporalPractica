package ec.edu.uteq.agrotrace.soap;

import ec.edu.uteq.agrotrace.soap.gen.CertificarLoteRequest;
import ec.edu.uteq.agrotrace.soap.gen.CertificarLoteResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

/**
 * Punto de entrada SOAP del servicio de certificacion oficial.
 *
 * <p>La entidad certificadora nacional solo acepta integraciones SOAP con
 * contrato WSDL. El enfoque es <em>contract-first</em>: primero se escribe el
 * esquema en {@code src/main/resources/xsd/certificacion.xsd} (TODO-GA-09),
 * luego Maven genera las clases Java, y solo entonces se escribe este
 * endpoint.</p>
 *
 * <p><strong>Antes de compilar esta clase ejecute:</strong>
 * {@code .\mvnw.cmd generate-sources} para que existan las clases
 * {@code CertificarLoteRequest} y {@code CertificarLoteResponse} en el paquete
 * {@code ec.edu.uteq.agrotrace.soap.gen}.</p>
 */
@Endpoint
public class CertificacionEndpoint {

	/** Espacio de nombres del contrato. Debe coincidir con el del XSD. */
	public static final String NAMESPACE =
			"https://agrotrace.uteq.edu.ec/soap/certificacion";

	private final CertificacionService servicio;

	public CertificacionEndpoint(CertificacionService servicio) {
		this.servicio = servicio;
	}

	/**
	 * Atiende la operacion de certificacion de un lote.
	 *
	 * <p>El enrutado es por espacio de nombres y nombre local del elemento raiz
	 * del cuerpo, no por URL: esa es la diferencia esencial con
	 * {@code @RequestMapping} de REST. La validacion del patron {@code LT-######}
	 * la aplica el motor SOAP contra el XSD antes de llegar aqui.</p>
	 *
	 * @param peticion elemento {@code CertificarLoteRequest} del cuerpo
	 * @return elemento {@code CertificarLoteResponse} con el certificado emitido
	 */
	@PayloadRoot(namespace = NAMESPACE, localPart = "CertificarLoteRequest")
	@ResponsePayload
	public CertificarLoteResponse certificar(@RequestPayload CertificarLoteRequest peticion) {
		Certificado emitido = servicio.certificar(
				peticion.getCodigoLote(), peticion.getCedulaTecnico());

		CertificarLoteResponse respuesta = new CertificarLoteResponse();
		respuesta.setNumeroCertificado(emitido.getNumero());
		respuesta.setCodigoLote(emitido.getLote().getCodigo());
		respuesta.setFincaOrigen(emitido.getLote().getFinca().getNombre());
		respuesta.setPesoKg(emitido.getLote().getPesoKg());
		respuesta.setEstadoLote(emitido.getLote().getEstado().name());
		respuesta.setFechaEmision(aXmlGregorian(emitido.getFechaEmision()));
		respuesta.setVigente(emitido.isVigente());
		return respuesta;
	}

	/**
	 * {@code fechaEmision} es {@code xs:date}, asi que JAXB genera un
	 * {@link javax.xml.datatype.XMLGregorianCalendar}.
	 *
	 * @param fecha fecha local a convertir
	 * @return la misma fecha como {@code XMLGregorianCalendar}
	 */
	private javax.xml.datatype.XMLGregorianCalendar aXmlGregorian(java.time.LocalDate fecha) {
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(fecha.toString());
		} catch (DatatypeConfigurationException ex) {
			throw new IllegalStateException("No se pudo construir la fecha del certificado", ex);
		}
	}
}
