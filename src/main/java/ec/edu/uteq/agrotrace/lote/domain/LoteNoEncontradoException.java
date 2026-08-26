package ec.edu.uteq.agrotrace.lote.domain;

/**
 * Se lanza cuando se solicita un lote cuyo codigo no existe en el acopio.
 * El manejador global la traduce a una respuesta 404 con formato problem details.
 */
public class LoteNoEncontradoException extends RuntimeException {

	private final String codigo;

	public LoteNoEncontradoException(String codigo) {
		super("No existe un lote con codigo " + codigo);
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}
}
