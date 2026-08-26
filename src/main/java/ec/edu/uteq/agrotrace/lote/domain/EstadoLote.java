package ec.edu.uteq.agrotrace.lote.domain;

/**
 * Estados posibles de un lote tras la evaluacion de recepcion de APROCAFA.
 */
public enum EstadoLote {

	/** Cumple humedad y fermentacion: entra al inventario vendible. */
	ACEPTADO,

	/** Humedad por encima del umbral: vuelve a las marquesinas de secado. */
	SECADO_ADICIONAL,

	/** Fermentacion insuficiente: no alcanza el perfil de cacao fino de aroma. */
	RECHAZADO
}
