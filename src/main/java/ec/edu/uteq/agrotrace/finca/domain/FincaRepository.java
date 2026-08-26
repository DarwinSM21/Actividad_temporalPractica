package ec.edu.uteq.agrotrace.finca.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a datos de fincas. No requiere trabajo en esta practica.
 */
public interface FincaRepository extends JpaRepository<Finca, Long> {

	List<Finca> findAllByOrderByNombreAsc();
}
