package ec.edu.uteq.agrotrace.clima;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expone el pronostico de secado al tablero del tecnico.
 * Esta clase ya viene resuelta.
 */
@RestController
@RequestMapping("/api/v1/clima")
@Tag(name = "Clima", description = "Pronostico para las marquesinas de secado")
public class ClimaRestController {

	private final ClimaService climaService;

	public ClimaRestController(ClimaService climaService) {
		this.climaService = climaService;
	}

	/**
	 * Devuelve el pronostico de las proximas 48 horas para el centro de acopio.
	 *
	 * @return pronostico, servido desde cache cuando esta caliente
	 */
	@Operation(summary = "Pronostico de 48 h para el centro de acopio",
			description = "Consumido desde Open-Meteo y cacheado en Redis.")
	@GetMapping("/secado")
	public ResponseEntity<PronosticoSecado> secado() {
		return ResponseEntity.ok(climaService.consultarTolerante());
	}
}
