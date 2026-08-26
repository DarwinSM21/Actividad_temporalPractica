package ec.edu.uteq.agrotrace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Punto de entrada de AgroTrace Quevedo.
 *
 * <p>Practica GA-U4 de Aplicaciones Web: Modelo-Vista-Controlador y Servicios Web.
 * El esqueleto arranca y responde; su trabajo consiste en completar los catorce
 * marcadores {@code TODO-GA} distribuidos en el codigo.</p>
 */
@SpringBootApplication
@EnableCaching
public class AgrotraceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgrotraceApplication.class, args);
	}
}
