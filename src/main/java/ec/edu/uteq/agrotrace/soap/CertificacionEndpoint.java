package ec.edu.uteq.agrotrace.soap;

/**
 * Punto de entrada SOAP del servicio de certificacion oficial.
 *
 * <p>La entidad certificadora nacional solo acepta integraciones SOAP con
 * contrato WSDL. El enfoque es <em>contract-first</em>: primero se escribe el
 * esquema en {@code src/main/resources/xsd/certificacion.xsd} (TODO-GA-09),
 * luego Maven genera las clases Java, y solo entonces se escribe este
 * endpoint.</p>
 *
 * <p><strong>Antes de escribir esta clase ejecute:</strong>
 * {@code .\mvnw.cmd generate-sources} para que existan las clases
 * {@code CertificarLoteRequest} y {@code CertificarLoteResponse} en el paquete
 * {@code ec.edu.uteq.agrotrace.soap.gen}.</p>
 */
public class CertificacionEndpoint {

	/** Espacio de nombres del contrato. Debe coincidir con el del XSD. */
	public static final String NAMESPACE =
			"https://agrotrace.uteq.edu.ec/soap/certificacion";

	// TODO-GA-10 (parte A): convertir esta clase en un endpoint SOAP.
	//
	//   1. Anote la clase con @Endpoint
	//      (org.springframework.ws.server.endpoint.annotation.Endpoint).
	//
	//   2. Inyecte CertificacionService por constructor.
	//
	//   3. Escriba el metodo manejador:
	//
	//        @PayloadRoot(namespace = NAMESPACE, localPart = "CertificarLoteRequest")
	//        @ResponsePayload
	//        public CertificarLoteResponse certificar(
	//                @RequestPayload CertificarLoteRequest peticion) {
	//            Certificado emitido = servicio.certificar(
	//                    peticion.getCodigoLote(), peticion.getCedulaTecnico());
	//            CertificarLoteResponse respuesta = new CertificarLoteResponse();
	//            respuesta.setNumeroCertificado(emitido.getNumero());
	//            respuesta.setCodigoLote(emitido.getLote().getCodigo());
	//            respuesta.setFincaOrigen(emitido.getLote().getFinca().getNombre());
	//            respuesta.setPesoKg(emitido.getLote().getPesoKg());
	//            respuesta.setEstadoLote(emitido.getLote().getEstado().name());
	//            respuesta.setVigente(emitido.isVigente());
	//            // La fecha se asigna con XMLGregorianCalendar: vea el README.
	//            return respuesta;
	//        }
	//
	// Nota: @PayloadRoot enruta por espacio de nombres y nombre local del
	// elemento raiz del cuerpo, no por URL. Es la diferencia esencial con
	// @RequestMapping de REST.
}
