package ec.edu.uteq.agrotrace.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadatos de la especificacion OpenAPI publicada en /api/docs. Los endpoints
 * se documentan con anotaciones @Operation/@ApiResponses en cada controlador.
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
