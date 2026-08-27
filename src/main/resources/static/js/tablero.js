/*
 * AgroTrace Quevedo -- tablero del tecnico de acopio.
 *
 * Tema 4, subtema 1: consumo de una API REST desde el cliente.
 * Este codigo corre EN EL NAVEGADOR y consume la propia API de AgroTrace.
 */

'use strict';

/**
 * Error de negocio devuelto por la API en formato problem details (RFC 9457).
 */
class ErrorApi extends Error {
	/**
	 * @param {string} titulo  valor del campo "title" del problema
	 * @param {string} detalle valor del campo "detail" del problema
	 * @param {number} estado  codigo de estado HTTP recibido
	 */
	constructor(titulo, detalle, estado) {
		super(`${titulo} (${estado})`);
		this.name = 'ErrorApi';
		this.titulo = titulo;
		this.detalle = detalle;
		this.estado = estado;
	}
}

/**
 * Devuelve el token de la sesion. En esta practica es un valor de ejemplo:
 * sirve para que la cabecera Authorization viaje y pueda observarse en
 * JwtAuthFilter, que es el paso 3 del ciclo de vida MVC.
 *
 * @returns {string} token portador
 */
function obtenerToken() {
	return 'demo-token-ga-u4';
}

/**
 * Carga los lotes desde la API REST y los pinta en la tabla del tablero.
 *
 * @param {string|null} estado filtro opcional por estado
 * @returns {Promise<void>}
 */
async function cargarLotes(estado = null) {
	const cuerpo = document.querySelector('#tabla-lotes tbody');
	const aviso = document.querySelector('#aviso');
	if (!cuerpo) {
		return;
	}
	mostrarCargando(cuerpo);

	const url = new URL('/api/v1/lotes', window.location.origin);
	if (estado) {
		url.searchParams.set('estado', estado);
	}
	url.searchParams.set('size', '50');

	try {
		const respuesta = await fetch(url, {
			method: 'GET',
			headers: {
				'Accept': 'application/json',
				'Authorization': `Bearer ${obtenerToken()}`
			}
		});

		// fetch NO lanza excepcion ante 4xx ni 5xx: hay que comprobarlo.
		if (!respuesta.ok) {
			const problema = await respuesta.json().catch(() => ({}));
			throw new ErrorApi(problema.title, problema.detail, respuesta.status);
		}

		const pagina = await respuesta.json();
		pintarLotes(cuerpo, pagina.content);
		if (aviso) {
			const total = pagina.page ? pagina.page.totalElements : pagina.content.length;
			aviso.textContent = `${total} lote(s) cargado(s) desde la API.`;
			aviso.className = 'aviso ok';
		}
	} catch (error) {
		cuerpo.innerHTML = '';
		if (error instanceof ErrorApi) {
			if (aviso) {
				aviso.textContent = `${error.titulo || 'Error'}: ${error.detalle || 'petición rechazada'} (${error.estado})`;
				aviso.className = 'aviso error';
			}
		} else {
			if (aviso) {
				aviso.textContent = 'No se pudo contactar al servidor. Reintente en unos segundos.';
				aviso.className = 'aviso error';
			}
			console.error('Fallo de red al cargar lotes', error);
		}
	}
}

/**
 * Pinta la lista de lotes en el cuerpo de la tabla.
 * Ya viene resuelta: uselo desde cargarLotes.
 *
 * @param {HTMLElement} cuerpo elemento tbody de destino
 * @param {Array<Object>} lotes lotes devueltos por la API
 */
function pintarLotes(cuerpo, lotes) {
	cuerpo.innerHTML = '';

	if (!lotes || lotes.length === 0) {
		cuerpo.innerHTML =
			'<tr><td colspan="6" class="vacio">No hay lotes con ese criterio.</td></tr>';
		return;
	}

	for (const lote of lotes) {
		const fila = document.createElement('tr');
		fila.innerHTML = `
			<td>${lote.codigo}</td>
			<td>${lote.finca}</td>
			<td>${formatearFecha(lote.fechaRecepcion)}</td>
			<td class="num">${lote.pesoKg} kg</td>
			<td class="num">${lote.humedadPorcentaje} %</td>
			<td><span class="badge ${claseEstado(lote.estado)}">${lote.estado}</span></td>`;
		cuerpo.appendChild(fila);
	}
}

/**
 * Traduce el estado del lote a la clase CSS del distintivo.
 *
 * @param {string} estado estado del lote
 * @returns {string} clase CSS
 */
function claseEstado(estado) {
	switch (estado) {
		case 'ACEPTADO':
			return 'ok';
		case 'RECHAZADO':
			return 'no';
		default:
			return 'espera';
	}
}

/**
 * Formatea una fecha ISO a dd/MM/yyyy.
 *
 * @param {string} iso fecha en formato ISO 8601
 * @returns {string} fecha legible
 */
function formatearFecha(iso) {
	const partes = String(iso).split('-');
	return partes.length === 3 ? `${partes[2]}/${partes[1]}/${partes[0]}` : iso;
}

/**
 * Muestra el estado de carga mientras llega la respuesta.
 *
 * @param {HTMLElement} cuerpo elemento tbody de destino
 */
function mostrarCargando(cuerpo) {
	cuerpo.innerHTML = '<tr><td colspan="6" class="vacio">Cargando…</td></tr>';
}

/**
 * Carga el pronostico de secado desde la API propia, que a su vez lo obtiene
 * del servicio meteorologico externo. Ya viene resuelta.
 *
 * @returns {Promise<void>}
 */
async function cargarClima() {
	const aviso = document.querySelector('#aviso-clima');
	const caja = document.querySelector('#clima');
	if (!aviso || !caja) {
		return;
	}

	try {
		const respuesta = await fetch('/api/v1/clima/secado', {
			headers: { 'Accept': 'application/json' }
		});

		if (!respuesta.ok) {
			aviso.textContent = 'El pronóstico no está disponible en este momento.';
			aviso.className = 'aviso error';
			return;
		}

		const datos = await respuesta.json();
		const horas = datos.hourly ? datos.hourly.time : [];

		if (horas.length === 0) {
			aviso.textContent = 'Sin datos meteorológicos por ahora.';
			return;
		}

		aviso.textContent = `Pronóstico de ${horas.length} horas para el acopio.`;
		aviso.className = 'aviso ok';
		caja.textContent =
			`Primera hora: ${horas[0]} — ${datos.hourly.temperature_2m[0]} °C, ` +
			`humedad ${datos.hourly.relative_humidity_2m[0]} %, ` +
			`lluvia ${datos.hourly.precipitation[0]} mm`;

	} catch (error) {
		aviso.textContent = 'No se pudo contactar al servidor.';
		aviso.className = 'aviso error';
		console.error('Fallo al cargar el clima', error);
	}
}

document.addEventListener('DOMContentLoaded', () => {
	cargarClima();

	// El listado se repuebla desde la API REST (Tema 4, subtema 1). Respeta el
	// filtro que el formulario MVC haya aplicado en el servidor.
	const filtro = document.querySelector('#estado');
	cargarLotes(filtro && filtro.value ? filtro.value : null);
});
