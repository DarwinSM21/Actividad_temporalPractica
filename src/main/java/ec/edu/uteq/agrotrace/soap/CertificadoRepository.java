package ec.edu.uteq.agrotrace.soap;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Acceso a datos de certificados. No requiere trabajo en esta practica.
 */
public interface CertificadoRepository extends JpaRepository<Certificado, Long> {

	Optional<Certificado> findByNumero(String numero);

	long countByFechaEmision(java.time.LocalDate fecha);
}
