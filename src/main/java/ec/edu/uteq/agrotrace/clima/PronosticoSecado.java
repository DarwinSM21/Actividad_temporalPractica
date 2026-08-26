package ec.edu.uteq.agrotrace.clima;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Pronostico horario relevante para el secado del cacao al sol.
 *
 * <p>Mapea la respuesta de Open-Meteo. Implementa {@link Serializable} porque
 * se guarda en Redis mediante la cache de Spring.</p>
 *
 * @param latitud  latitud efectiva devuelta por el servicio
 * @param longitud longitud efectiva devuelta por el servicio
 * @param zona     zona horaria aplicada
 * @param horario  series horarias de temperatura, humedad y precipitacion
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PronosticoSecado(

		@JsonProperty("latitude") Double latitud,
		@JsonProperty("longitude") Double longitud,
		@JsonProperty("timezone") String zona,
		@JsonProperty("hourly") SerieHoraria horario) implements Serializable {

	/**
	 * Series horarias devueltas por el servicio.
	 *
	 * @param tiempo        marcas de tiempo en ISO 8601
	 * @param temperatura   temperatura a 2 m, en grados Celsius
	 * @param humedad       humedad relativa a 2 m, en porcentaje
	 * @param precipitacion precipitacion acumulada por hora, en milimetros
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record SerieHoraria(
			@JsonProperty("time") List<String> tiempo,
			@JsonProperty("temperature_2m") List<Double> temperatura,
			@JsonProperty("relative_humidity_2m") List<Integer> humedad,
			@JsonProperty("precipitation") List<Double> precipitacion)
			implements Serializable {
	}

	/**
	 * Valor de reemplazo cuando el origen esta caido y no hay dato en cache.
	 * Permite que el tablero siga respondiendo en lugar de romperse.
	 *
	 * @return pronostico vacio marcado como no disponible
	 */
	public static PronosticoSecado noDisponible() {
		return new PronosticoSecado(null, null, "no-disponible",
				new SerieHoraria(List.of(), List.of(), List.of(), List.of()));
	}

	/**
	 * Indica si este pronostico contiene datos reales.
	 *
	 * @return {@code true} si hay al menos una hora con dato
	 */
	public boolean tieneDatos() {
		return horario != null && horario.tiempo() != null && !horario.tiempo().isEmpty();
	}
}
