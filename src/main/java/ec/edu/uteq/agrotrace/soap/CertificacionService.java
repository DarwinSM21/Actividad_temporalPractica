package ec.edu.uteq.agrotrace.soap;

import ec.edu.uteq.agrotrace.lote.app.LoteService;
import ec.edu.uteq.agrotrace.lote.domain.Lote;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Logica de emision de certificados oficiales.
 *
 * <p>Esta clase ya viene resuelta: no forma parte de los marcadores. Se apoya
 * en {@link LoteService}, de modo que la regla de negocio del lote no se
 * duplica para el canal SOAP. Es el mismo modelo con una tercera vista.</p>
 */
@Service
@Transactional
public class CertificacionService {

	private final LoteService loteService;
	private final CertificadoRepository certificadoRepository;

	public CertificacionService(LoteService loteService,
			CertificadoRepository certificadoRepository) {
		this.loteService = loteService;
		this.certificadoRepository = certificadoRepository;
	}

	/**
	 * Emite el certificado oficial de un lote.
	 *
	 * @param codigoLote    codigo de trazabilidad del lote
	 * @param cedulaTecnico cedula del tecnico que solicita la certificacion
	 * @return el certificado persistido
	 */
	public Certificado certificar(String codigoLote, String cedulaTecnico) {
		Lote lote = loteService.porCodigo(codigoLote);
		Certificado certificado = new Certificado(
				siguienteNumero(), lote, cedulaTecnico);
		return certificadoRepository.save(certificado);
	}

	private String siguienteNumero() {
		LocalDate hoy = LocalDate.now();
		long correlativo = certificadoRepository.countByFechaEmision(hoy) + 1;
		return String.format("CERT-%s-%04d", hoy.toString().replace("-", ""), correlativo);
	}
}
