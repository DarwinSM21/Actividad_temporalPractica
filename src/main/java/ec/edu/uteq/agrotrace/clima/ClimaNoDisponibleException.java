package ec.edu.uteq.agrotrace.clima;

/**
 * Se lanza cuando el servicio meteorologico externo no responde o responde mal.
 * El manejador global la traduce a un 503 con formato problem details.
 */
public class ClimaNoDisponibleException extends RuntimeException {

	public ClimaNoDisponibleException(String mensaje) {
		super(mensaje);
	}

	public ClimaNoDisponibleException(String mensaje, Throwable causa) {
		super(mensaje, causa);
	}
}
