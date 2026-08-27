package ec.edu.uteq.agrotrace.lote.app;

import ec.edu.uteq.agrotrace.finca.domain.Finca;
import ec.edu.uteq.agrotrace.finca.domain.FincaRepository;
import ec.edu.uteq.agrotrace.lote.domain.EstadoLote;
import ec.edu.uteq.agrotrace.lote.domain.Lote;
import ec.edu.uteq.agrotrace.lote.domain.LoteNoEncontradoException;
import ec.edu.uteq.agrotrace.lote.domain.LoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de aplicacion de lotes.
 *
 * <p>Orquesta: busca la finca, genera el codigo, construye la entidad y la
 * persiste. La decision sobre el estado del lote NO se toma aqui: la toma la
 * propia entidad en {@code Lote.evaluarEstado()}.</p>
 */
@Service
@Transactional(readOnly = true)
public class LoteService {

	private final LoteRepository loteRepository;
	private final FincaRepository fincaRepository;

	public LoteService(LoteRepository loteRepository, FincaRepository fincaRepository) {
		this.loteRepository = loteRepository;
		this.fincaRepository = fincaRepository;
	}

	/**
	 * Lista lotes para la vista web, opcionalmente filtrados por estado.
	 *
	 * @param estado estado por el que filtrar, o {@code null} para traerlos todos
	 * @return lotes ordenados por fecha de recepcion descendente
	 */
	public List<Lote> buscar(EstadoLote estado) {
		Sort orden = Sort.by(Sort.Direction.DESC, "fechaRecepcion");
		if (estado == null) {
			return loteRepository.findAll(orden);
		}
		return loteRepository.findByEstado(estado, Pageable.unpaged(orden)).getContent();
	}

	/**
	 * Busqueda paginada para la API REST.
	 *
	 * @param estado   filtro opcional por estado
	 * @param fincaId  filtro opcional por finca
	 * @param pageable paginacion y orden solicitados
	 * @return pagina de lotes que cumplen los filtros
	 */
	public Page<Lote> buscarPaginado(EstadoLote estado, Long fincaId, Pageable pageable) {
		if (estado != null && fincaId != null) {
			return loteRepository.findByEstadoAndFincaId(estado, fincaId, pageable);
		}
		if (estado != null) {
			return loteRepository.findByEstado(estado, pageable);
		}
		if (fincaId != null) {
			return loteRepository.findByFincaId(fincaId, pageable);
		}
		return loteRepository.findAll(pageable);
	}

	/**
	 * Recupera un lote por su codigo de trazabilidad.
	 *
	 * @param codigo codigo con formato {@code LT-000000}
	 * @return el lote encontrado
	 * @throws LoteNoEncontradoException si no existe
	 */
	public Lote porCodigo(String codigo) {
		return loteRepository.findByCodigo(codigo)
				.orElseThrow(() -> new LoteNoEncontradoException(codigo));
	}

	/**
	 * Registra un lote nuevo aplicando la politica de recepcion.
	 *
	 * @param orden datos del lote a registrar
	 * @return el lote persistido, ya con su estado evaluado
	 */
	@Transactional
	public Lote registrar(RegistrarLoteCommand orden) {
		Finca finca = fincaRepository.findById(orden.fincaId())
				.orElseThrow(() -> new IllegalArgumentException(
						"No existe la finca " + orden.fincaId()));

		LocalDate fecha = orden.fechaRecepcion() == null
				? LocalDate.now()
				: orden.fechaRecepcion();

		Lote lote = new Lote(
				siguienteCodigo(),
				finca,
				fecha,
				orden.pesoKg(),
				orden.humedadPorcentaje(),
				orden.fermentacionPorcentaje());

		return loteRepository.save(lote);
	}

	/**
	 * Cuenta los lotes que estan en un estado dado. Lo usa el tablero.
	 *
	 * @param estado estado a contar
	 * @return numero de lotes en ese estado
	 */
	public long contarPorEstado(EstadoLote estado) {
		return loteRepository.countByEstado(estado);
	}

	private String siguienteCodigo() {
		long siguiente = loteRepository.count() + 1;
		return String.format("LT-%06d", siguiente);
	}
}
