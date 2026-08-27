package ec.edu.uteq.agrotrace.soap;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurer;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.List;

/**
 * Infraestructura del canal SOAP.
 *
 * <p>Publica el {@code MessageDispatcherServlet} en {@code /ws/*} y expone el
 * contrato WSDL generado a partir del esquema. El nombre del bean
 * {@code certificacion} determina la ruta del contrato:
 * {@code /ws/certificacion.wsdl}.</p>
 *
 * <p>En Spring WS 5.x ya no existe {@code WsConfigurerAdapter}: para registrar
 * interceptores se implementa directamente la interfaz {@link WsConfigurer}.</p>
 */
@Configuration
@EnableWs
public class ConfiguracionSoap implements WsConfigurer {

	/**
	 * Registra el servlet de mensajeria SOAP bajo la ruta {@code /ws/*}.
	 *
	 * @param contexto contexto de la aplicacion
	 * @return registro del servlet
	 */
	@Bean
	public ServletRegistrationBean<MessageDispatcherServlet> mensajeriaSoap(
			ApplicationContext contexto) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(contexto);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean<>(servlet, "/ws/*");
	}

	/**
	 * Define el contrato WSDL 1.1 publicado en {@code /ws/certificacion.wsdl}.
	 *
	 * @param esquema esquema XSD del contrato
	 * @return definicion WSDL
	 */
	@Bean(name = "certificacion")
	public DefaultWsdl11Definition definicionWsdl(XsdSchema esquema) {
		DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
		wsdl.setPortTypeName("CertificacionPort");
		wsdl.setLocationUri("/ws");
		wsdl.setTargetNamespace(CertificacionEndpoint.NAMESPACE);
		wsdl.setSchema(esquema);
		return wsdl;
	}

	/**
	 * Carga el esquema del contrato desde el classpath.
	 *
	 * @return esquema XSD
	 */
	@Bean
	public XsdSchema esquemaCertificacion() {
		return new SimpleXsdSchema(new ClassPathResource("xsd/certificacion.xsd"));
	}

	/**
	 * Marshaller JAXB sobre las clases generadas desde el XSD. Lo usan tanto el
	 * canal de servidor como {@link ClienteCertificacion}; Spring Boot no crea
	 * uno por defecto.
	 *
	 * @return marshaller/unmarshaller del contrato de certificacion
	 */
	@Bean
	public Jaxb2Marshaller jaxb2Marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan("ec.edu.uteq.agrotrace.soap.gen");
		return marshaller;
	}

	/**
	 * Registra un interceptor que valida cada peticion y respuesta contra el
	 * XSD. Es lo que hace que un {@code codigoLote} mal formado (por ejemplo
	 * {@code CACAO-1}) produzca un SOAP Fault del motor antes de ejecutar el
	 * endpoint, en lugar de un error generico.
	 */
	@Override
	public void addInterceptors(List<EndpointInterceptor> interceptores) {
		PayloadValidatingInterceptor validacion = new PayloadValidatingInterceptor();
		validacion.setSchema(new ClassPathResource("xsd/certificacion.xsd"));
		validacion.setValidateRequest(true);
		validacion.setValidateResponse(true);
		try {
			// No es un bean de Spring: hay que inicializar el validador a mano.
			validacion.afterPropertiesSet();
		} catch (Exception ex) {
			throw new IllegalStateException("No se pudo inicializar la validacion del XSD", ex);
		}
		interceptores.add(validacion);
	}
}
