package ec.edu.uteq.agrotrace.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadatos de la especificacion OpenAPI 3.2 publicada en /api/docs.
 * Ya viene resuelta: lo que falta documentar son los endpoints (TODO-GA-08).
 */
@Configuration
public class OpenApiConfig {

	/**
	 * Describe la API para las exportadoras.
	 *
	 * @return metadatos de la especificacion
	 */
	@Bean
	public OpenAPI agrotraceOpenApi() {
		return new OpenAPI().info(new Info()
				.title("AgroTrace Quevedo - API de lotes")
				.version("v1")
				.description("Trazabilidad de lotes de cacao fino de aroma de APROCAFA. "
						+ "Practica GA-U4 de Aplicaciones Web, UTEQ.")
				.contact(new Contact()
						.name("APROCAFA - Centro de acopio Quevedo")
						.email("acopio@aprocafa.ec"))
				.license(new License().name("Uso academico")));
	}
}
