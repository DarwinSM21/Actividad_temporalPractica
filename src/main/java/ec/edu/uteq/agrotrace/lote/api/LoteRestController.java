package ec.edu.uteq.agrotrace.lote.api;

import ec.edu.uteq.agrotrace.lote.app.LoteService;
import ec.edu.uteq.agrotrace.lote.domain.EstadoLote;
import ec.edu.uteq.agrotrace.lote.domain.Lote;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * API REST de lotes para las exportadoras.
 *
 * <p>Es la segunda vista sobre el mismo modelo: no duplica ni una regla de
 * negocio respecto de la vista web. Esa es la demostracion practica de la
 * ventaja del patron MVC enunciada en el subtema 1.4.</p>
 */
@RestController
@RequestMapping("/api/v1/lotes")
@Tag(name = "Lotes", description = "Consulta y registro de lotes de cacao")
public class LoteRestController {

	private final LoteService loteService;

	public LoteRestController(LoteService loteService) {
		this.loteService = loteService;
	}

	/**
	 * Lista los lotes de forma paginada, con filtros opcionales.
	 *
	 * @param estado   filtro opcional por estado
	 * @param fincaId  filtro opcional por finca
	 * @param pageable paginacion solicitada
	 * @return pagina de lotes
	 */
	@Operation(summary = "Lista los lotes de cacao del centro de acopio",
			description = "Devuelve una pagina de lotes, filtrable por estado y por finca.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Pagina de lotes"),
			@ApiResponse(responseCode = "401", description = "Token ausente o invalido",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
			@ApiResponse(responseCode = "422", description = "Parametro de filtro invalido",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
	})
	@GetMapping
	public ResponseEntity<PagedModel<LoteResponse>> listar(
			@RequestParam(required = false) EstadoLote estado,
			@RequestParam(required = false) Long fincaId,
			@PageableDefault(size = 20, sort = "fechaRecepcion",
					direction = Sort.Direction.DESC) Pageable pageable) {

		Page<LoteResponse> pagina = loteService
				.buscarPaginado(estado, fincaId, pageable)
				.map(LoteResponse::desde);
		return ResponseEntity.ok(new PagedModel<>(pagina));
	}

	/**
	 * Recupera un lote por su codigo de trazabilidad.
	 *
	 * @param codigo codigo con formato LT-000000
	 * @return el lote solicitado
	 */
	@Operation(summary = "Recupera un lote por su codigo de trazabilidad")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lote encontrado"),
			@ApiResponse(responseCode = "404", description = "No existe un lote con ese codigo",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
	})
	@GetMapping("/{codigo}")
	public ResponseEntity<LoteResponse> obtener(@PathVariable String codigo) {
		return ResponseEntity.ok(LoteResponse.desde(loteService.porCodigo(codigo)));
	}

	/**
	 * Registra un lote nuevo.
	 *
	 * @param peticion   datos del lote
	 * @param uriBuilder constructor de URI para la cabecera Location
	 * @return 201 Created con la cabecera Location y el lote creado
	 */
	@Operation(summary = "Registra un lote nuevo",
			description = "Crea el lote, evalua su estado de recepcion y devuelve 201 con la cabecera Location.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Lote creado; la cabecera Location apunta al recurso"),
			@ApiResponse(responseCode = "422", description = "El cuerpo tiene campos invalidos",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
	})
	@PostMapping
	public ResponseEntity<LoteResponse> crear(
			@Valid @RequestBody CrearLoteRequest peticion,
			UriComponentsBuilder uriBuilder) {

		Lote creado = loteService.registrar(peticion.toCommand());
		URI ubicacion = uriBuilder
				.path("/api/v1/lotes/{codigo}")
				.buildAndExpand(creado.getCodigo())
				.toUri();
		return ResponseEntity.created(ubicacion).body(LoteResponse.desde(creado));
	}
}
