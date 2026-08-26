package ec.edu.uteq.agrotrace.clima;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/**
 * Consumo del servicio meteorologico externo desde el servidor.
 *
 * <p>Este consumo va en el servidor y no en el navegador por tres razones:
 * una eventual clave de API no quedaria expuesta en JavaScript; desde aqui se
 * puede cachear y servir a todo el centro de acopio con una sola llamada al
 * origen; y no se depende de que un tercero publique cabeceras CORS
 * permisivas.</p>
 *
 * <p>Servicio consumido: Open-Meteo, gratuito y sin clave para uso no
 * comercial. Endpoint: {@code https://api.open-meteo.com/v1/forecast}.</p>
 */
@Service
public class ClimaService {

	private static final Logger log = LoggerFactory.getLogger(ClimaService.class);

	/** Centro de acopio de APROCAFA, km 7 via Quevedo - El Empalme. */
	public static final double LATITUD = -1.0286;

	/** Longitud del centro de acopio. */
	public static final double LONGITUD = -79.4636;

	private final RestClient cliente;

	public ClimaService(RestClient.Builder constructor) {
		// TODO-GA-13 (parte A): construir el cliente HTTP con tiempos limite.
		//
		//   this.cliente = constructor
		//           .baseUrl("https://api.open-meteo.com")
		//           .requestFactory(fabricaConTiempoLimite())
		//           .build();
		//
		// Un cliente sin tiempo limite deja hilos bloqueados cuando el tercero
		// se cuelga, y termina tumbando la aplicacion entera.
		this.cliente = constructor.baseUrl("https://api.open-meteo.com").build();
	}

	/**
	 * Consulta el origen sin pasar por la cache.
	 *
	 * @return pronostico de las proximas 48 horas
	 * @throws ClimaNoDisponibleException si el origen responde 4xx o 5xx
	 */
	public PronosticoSecado consultarOrigen() {
		// TODO-GA-13 (parte B): invocar el endpoint de Open-Meteo.
		//
		//   return cliente.get()
		//       .uri(uri -> uri.path("/v1/forecast")
		//           .queryParam("latitude", LATITUD)
		//           .queryParam("longitude", LONGITUD)
		//           .queryParam("hourly",
		//               "temperature_2m,relative_humidity_2m,precipitation")
		//           .queryParam("forecast_days", 2)
		//           .queryParam("timezone", "America/Guayaquil")
		//           .build())
		//       .retrieve()
		//       .onStatus(HttpStatusCode::is4xxClientError, (peticion, respuesta) -> {
		//           throw new ClimaNoDisponibleException(
		//               "Peticion invalida al servicio meteorologico: "
		//               + respuesta.getStatusCode());
		//       })
		//       .onStatus(HttpStatusCode::is5xxServerError, (peticion, respuesta) -> {
		//           throw new ClimaNoDisponibleException(
		//               "El servicio meteorologico no responde: "
		//               + respuesta.getStatusCode());
		//       })
		//       .body(PronosticoSecado.class);
		//
		throw new UnsupportedOperationException(
				"TODO-GA-13: completar ClimaService.consultarOrigen()");
	}

	/**
	 * Consulta el pronostico pasando por la cache.
	 *
	 * @return pronostico, posiblemente servido desde Redis
	 */
	public PronosticoSecado consultar() {
		// TODO-GA-14 (parte A): anotar este metodo para cachear su resultado.
		//
		//   @Cacheable(cacheNames = "pronostico-secado", key = "'acopio-principal'",
		//              unless = "#result == null")
		//
		// El TTL se configura en application.yml, no aqui. Debera justificar
		// ese TTL en docs/E2-cache.md segun la volatilidad del dato.
		log.info("Fallo de cache: consultando origen open-meteo");
		return consultarOrigen();
	}

	/**
	 * Consulta tolerante a fallos: si el origen cae, sirve el ultimo valor
	 * conocido en lugar de romper el tablero completo del tecnico.
	 *
	 * @return pronostico real, ultimo conocido, o marcador de no disponible
	 */
	public PronosticoSecado consultarTolerante() {
		// TODO-GA-14 (parte B): degradacion elegante.
		//
		//   try {
		//       return consultar();
		//   } catch (ClimaNoDisponibleException | ResourceAccessException ex) {
		//       log.warn("Origen meteorologico inaccesible: {}", ex.getMessage());
		//       return ultimoConocido().orElse(PronosticoSecado.noDisponible());
		//   }
		//
		// Que el clima falle no puede dejar sin tablero al tecnico de acopio.
		throw new UnsupportedOperationException(
				"TODO-GA-14: completar ClimaService.consultarTolerante()");
	}

	/**
	 * Recupera el ultimo pronostico almacenado en cache, si lo hubiera.
	 * Metodo auxiliar ya resuelto.
	 *
	 * @return el ultimo pronostico conocido, o vacio si la cache esta fria
	 */
	protected Optional<PronosticoSecado> ultimoConocido() {
		return Optional.empty();
	}
}
