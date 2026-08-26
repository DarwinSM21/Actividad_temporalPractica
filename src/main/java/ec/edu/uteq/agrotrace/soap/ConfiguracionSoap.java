package ec.edu.uteq.agrotrace.soap;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

/**
 * Infraestructura del canal SOAP.
 *
 * <p>Publica el {@code MessageDispatcherServlet} en {@code /ws/*} y expone el
 * contrato WSDL generado a partir del esquema. El nombre del bean
 * {@code certificacion} determina la ruta del contrato:
 * {@code /ws/certificacion.wsdl}.</p>
 */
@Configuration
@EnableWs
public class ConfiguracionSoap extends WsConfigurerAdapter {

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
}
