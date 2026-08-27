package ec.edu.uteq.agrotrace.clima;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.Duration;
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

	/**
	 * Referencia al propio bean, obtenida a traves del contenedor. Es necesaria
	 * porque {@code @Cacheable} solo actua cuando la llamada pasa por el proxy
	 * de Spring: una llamada {@code this.consultar()} desde dentro de la misma
	 * clase se saltaria la cache.
	 */
	private final ClimaService self;

	public ClimaService(RestClient.Builder constructor, @Lazy ClimaService self) {
		// Un cliente sin tiempo limite deja hilos bloqueados cuando el tercero
		// se cuelga, y termina tumbando la aplicacion entera.
		this.cliente = constructor
				.baseUrl("https://api.open-meteo.com")
				.requestFactory(fabricaConTiempoLimite())
				.build();
		this.self = self;
	}

	/**
	 * Consulta el origen sin pasar por la cache.
	 *
	 * @return pronostico de las proximas 48 horas
	 * @throws ClimaNoDisponibleException si el origen responde 4xx o 5xx
	 */
	public PronosticoSecado consultarOrigen() {
		return cliente.get()
				.uri(uri -> uri.path("/v1/forecast")
						.queryParam("latitude", LATITUD)
						.queryParam("longitude", LONGITUD)
						.queryParam("hourly",
								"temperature_2m,relative_humidity_2m,precipitation")
						.queryParam("forecast_days", 2)
						.queryParam("timezone", "America/Guayaquil")
						.build())
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, (peticion, respuesta) -> {
					throw new ClimaNoDisponibleException(
							"Peticion invalida al servicio meteorologico: "
									+ respuesta.getStatusCode());
				})
				.onStatus(HttpStatusCode::is5xxServerError, (peticion, respuesta) -> {
					throw new ClimaNoDisponibleException(
							"El servicio meteorologico no responde: "
									+ respuesta.getStatusCode());
				})
				.body(PronosticoSecado.class);
	}

	/**
	 * Consulta el pronostico pasando por la cache.
	 *
	 * <p>El TTL se configura en {@code application.yml}
	 * ({@code spring.cache.redis.time-to-live}) y su justificacion esta en
	 * {@code docs/E2-cache.md}: el pronostico horario se actualiza en el origen
	 * cada hora, asi que 15 minutos es defendible.</p>
	 *
	 * @return pronostico, posiblemente servido desde Redis
	 */
	@Cacheable(cacheNames = "pronostico-secado", key = "'acopio-principal'",
			unless = "#result == null")
	public PronosticoSecado consultar() {
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
		try {
			return self.consultar();
		} catch (ClimaNoDisponibleException | ResourceAccessException ex) {
			log.warn("Origen meteorologico inaccesible: {}", ex.getMessage());
			return ultimoConocido().orElse(PronosticoSecado.noDisponible());
		}
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

	/**
	 * Construye la fabrica de peticiones con tiempos limite de conexion y de
	 * lectura, para que un tercero colgado no bloquee hilos indefinidamente.
	 *
	 * @return fabrica de peticiones con 3 s de conexion y 5 s de lectura
	 */
	private ClientHttpRequestFactory fabricaConTiempoLimite() {
		HttpClientSettings ajustes = HttpClientSettings.defaults()
				.withConnectTimeout(Duration.ofSeconds(3))
				.withReadTimeout(Duration.ofSeconds(5));
		return ClientHttpRequestFactoryBuilder.detect().build(ajustes);
	}
}
