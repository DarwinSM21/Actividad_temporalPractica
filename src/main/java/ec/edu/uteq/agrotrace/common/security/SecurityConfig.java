package ec.edu.uteq.agrotrace.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion de seguridad del esqueleto.
 *
 * <p>Deliberadamente permisiva para que la practica de tres horas no se
 * atasque en autenticacion: la API queda abierta y solo se deja instalado el
 * filtro JWT para que el Bloque 4 pueda enviar la cabecera Authorization y
 * observarla. Endurecer esta configuracion es trabajo del PFC, no de la GA.</p>
 */
@Configuration
public class SecurityConfig {

	/**
	 * Cadena de filtros de seguridad.
	 *
	 * @param http  constructor de la cadena
	 * @param filtro filtro de lectura del token
	 * @return cadena configurada
	 * @throws Exception si la configuracion es invalida
	 */
	@Bean
	public SecurityFilterChain cadena(HttpSecurity http, JwtAuthFilter filtro) throws Exception {
		return http
				.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/ws/**"))
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(reglas -> reglas.anyRequest().permitAll())
				.headers(Customizer.withDefaults())
				.build();
	}
}
