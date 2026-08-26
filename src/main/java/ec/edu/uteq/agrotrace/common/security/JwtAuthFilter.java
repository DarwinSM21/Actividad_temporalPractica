package ec.edu.uteq.agrotrace.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de lectura del token de la API.
 *
 * <p>Se incluye para que el paso 3 del ciclo de vida MVC descrito en la guia
 * exista de verdad en el codigo y pueda observarse con el depurador. En esta
 * practica solo registra la presencia del token; la verificacion criptografica
 * completa corresponde al PFC.</p>
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
	private static final String PREFIJO = "Bearer ";

	@Override
	protected void doFilterInternal(HttpServletRequest peticion,
			HttpServletResponse respuesta, FilterChain cadena)
			throws ServletException, IOException {

		String cabecera = peticion.getHeader("Authorization");
		if (cabecera != null && cabecera.startsWith(PREFIJO)) {
			log.debug("Peticion {} {} con token de {} caracteres",
					peticion.getMethod(), peticion.getRequestURI(),
					cabecera.length() - PREFIJO.length());
		}
		cadena.doFilter(peticion, respuesta);
	}
}
