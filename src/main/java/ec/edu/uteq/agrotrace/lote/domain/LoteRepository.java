package ec.edu.uteq.agrotrace.lote.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos de lotes.
 *
 * <p>Todas las consultas de esta interfaz se derivan del nombre del metodo.
 * Spring Data construye el JPQL y enlaza los parametros, de modo que no existe
 * superficie de inyeccion SQL. Nunca escriba aqui una consulta concatenando
 * texto con datos del usuario.</p>
 */
public interface LoteRepository extends JpaRepository<Lote, Long> {

	Optional<Lote> findByCodigo(String codigo);

	Page<Lote> findByEstado(EstadoLote estado, Pageable pageable);

	Page<Lote> findByFincaId(Long fincaId, Pageable pageable);

	Page<Lote> findByEstadoAndFincaId(EstadoLote estado, Long fincaId, Pageable pageable);

	// (a) Lotes de una finca en un estado dado, mas recientes primero.
	List<Lote> findByFincaIdAndEstadoOrderByFechaRecepcionDesc(Long fincaId, EstadoLote estado);

	// (b) Contador de lotes por estado, para el tablero.
	long countByEstado(EstadoLote estado);
}
