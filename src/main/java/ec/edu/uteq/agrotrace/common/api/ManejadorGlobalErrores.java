package ec.edu.uteq.agrotrace.common.api;

import ec.edu.uteq.agrotrace.clima.ClimaNoDisponibleException;
import ec.edu.uteq.agrotrace.lote.domain.LoteNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

/**
 * Traduce las excepciones de la aplicacion al formato uniforme de error
 * definido por el RFC 9457, que sustituyo al RFC 7807.
 *
 * <p>Todas las respuestas de error de la API deben salir por aqui, con
 * {@code Content-Type: application/problem+json}. Devolver un 200 con un
 * cuerpo que describa el error incumple el criterio C4 de la rubrica.</p>
 */
@RestControllerAdvice(basePackages = "ec.edu.uteq.agrotrace")
public class ManejadorGlobalErrores {

	private static final String BASE_TIPO = "https://agrotrace.uteq.edu.ec/errores/";

	/**
	 * Traduce un lote inexistente a 404 con problem details.
	 *
	 * @param ex excepcion capturada
	 * @return cuerpo del problema
	 */
	@ExceptionHandler(LoteNoEncontradoException.class)
	public ProblemDetail loteNoEncontrado(LoteNoEncontradoException ex) {
		// TODO-GA-07 (parte A): construir el ProblemDetail de 404.
		//
		//   ProblemDetail pd = ProblemDetail.forStatusAndDetail(
		//           HttpStatus.NOT_FOUND, ex.getMessage());
		//   pd.setType(URI.create(BASE_TIPO + "lote-no-encontrado"));
		//   pd.setTitle("Lote no encontrado");
		//   pd.setProperty("codigoBuscado", ex.getCodigo());
		//   pd.setProperty("marcaTiempo", Instant.now());
		//   return pd;
		//
		throw new UnsupportedOperationException(
				"TODO-GA-07: completar ManejadorGlobalErrores.loteNoEncontrado()");
	}

	/**
	 * Traduce un cuerpo invalido a 422 con la lista de campos que fallaron.
	 *
	 * @param ex excepcion de validacion de Bean Validation
	 * @return cuerpo del problema con el detalle por campo
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail validacion(MethodArgumentNotValidException ex) {
		// TODO-GA-07 (parte B): construir el ProblemDetail de 422.
		//
		// Debe incluir una propiedad "campos" que sea un mapa
		// nombreDelCampo -> mensajeDeError, construido a partir de
		// ex.getBindingResult().getFieldErrors().
		//
		throw new UnsupportedOperationException(
				"TODO-GA-07: completar ManejadorGlobalErrores.validacion()");
	}

	/**
	 * Traduce un fallo del servicio meteorologico externo a 503.
	 * Este manejador ya viene resuelto: uselo como plantilla de los anteriores.
	 *
	 * @param ex excepcion del consumo externo
	 * @return cuerpo del problema
	 */
	@ExceptionHandler(ClimaNoDisponibleException.class)
	public ProblemDetail climaNoDisponible(ClimaNoDisponibleException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(
				HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
		pd.setType(URI.create(BASE_TIPO + "clima-no-disponible"));
		pd.setTitle("Servicio meteorologico no disponible");
		pd.setProperty("marcaTiempo", Instant.now());
		return pd;
	}

	/**
	 * Traduce argumentos invalidos de negocio a 400.
	 *
	 * @param ex excepcion capturada
	 * @return cuerpo del problema
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ProblemDetail argumentoInvalido(IllegalArgumentException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(
				HttpStatus.BAD_REQUEST, ex.getMessage());
		pd.setType(URI.create(BASE_TIPO + "argumento-invalido"));
		pd.setTitle("Peticion invalida");
		pd.setProperty("marcaTiempo", Instant.now());
		return pd;
	}
}
