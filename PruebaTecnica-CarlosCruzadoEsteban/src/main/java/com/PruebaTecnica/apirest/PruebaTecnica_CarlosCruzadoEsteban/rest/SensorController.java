package com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.rest;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.dto.response.SensorErrorResponse;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.dto.response.SensorResponse;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.dto.SensorDTO;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.modelo.Sensor;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.service.ServicioSensor;

import io.swagger.v3.oas.annotations.Operation;

import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.modelo.Medicion;

@RestController
@RequestMapping("/sensores")
public class SensorController {

	private ServicioSensor servicioSensor;

	@Autowired
	public SensorController(ServicioSensor servicioSensor) {
		this.servicioSensor = servicioSensor;
	}

	@Operation(summary = "Registrar un sensor", description = "Permite registrar un sensor de tipo Temperatura, Humedad, Presión o Velocidad del viento. Sólo puede haber un sensor de cada tipo. Si se intenta registrar uno ya existente, se devolverá un error.")
	@PostMapping
	public ResponseEntity<SensorResponse> altaSensor(@RequestBody Sensor sensor) {
		Long id = servicioSensor.crearSensor(sensor.getTipo(), sensor.getMagnitud());
		SensorResponse response = new SensorResponse(id, "¡Objeto creado con exito!");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Eliminar un sensor", description = "Elimina un sensor registrado a partir de su identificador.")
	@DeleteMapping("/{idSensor}")
	public ResponseEntity<Void> bajaSensor(@PathVariable Long idSensor) {
		servicioSensor.borrarSensor(idSensor);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Listar sensores", description = "Obtiene una lista con todos los sensores registrados.")
	@GetMapping
	public ResponseEntity<List<SensorDTO>> obtenerTodosSensores() {
		List<SensorDTO> sensores = servicioSensor.listarSensores();
		return ResponseEntity.ok(sensores);
	}

	@Operation(summary = "Obtener valor actual del sensor", description = "Devuelve el valor actual del sensor. En cada invocación se genera un valor aleatorio coherente con la magnitud y se registra en base de datos la fecha y el valor.")
	@GetMapping("/{idSensor}")
	public ResponseEntity<Double> obtenerValorSensor(@PathVariable Long idSensor) {
		double valor = servicioSensor.obtenerYActualizarValor(idSensor);
		return ResponseEntity.ok(valor);
	}

	@Operation(summary = "Calcular media de medidas", description = "Calcula la media de los valores registrados para un sensor en un rango de fechas específico. El formato de fecha permitido es 'yyyy-MM-dd'T'HH:mm:ss' o 'yyyy-MM-dd'T'HH:mm:ss.SSS'.")
	@GetMapping("/{idSensor}/media/{fechaInicio}/{fechaFin}")
	public ResponseEntity<?> obtenerMediaSensor(@PathVariable Long idSensor, @PathVariable String fechaInicio,
			@PathVariable String fechaFin) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]");

		try {
			LocalDateTime inicio = LocalDateTime.parse(fechaInicio, formatter);
			LocalDateTime fin = LocalDateTime.parse(fechaFin, formatter);

			double media = servicioSensor.calcularMedia(idSensor, inicio, fin);
			return ResponseEntity.ok(media);

		} catch (DateTimeParseException e) {
			SensorErrorResponse error = new SensorErrorResponse(
					"Formato de fecha incorrecto. Use 'yyyy-MM-dd'T'HH:mm:ss' o 'yyyy-MM-dd'T'HH:mm:ss.SSS'.");

			return ResponseEntity.badRequest().body(error);
		}
	}

	@Operation(summary = "Obtener histórico de medidas", description = "Devuelve el histórico paginado de las medidas registradas para el sensor especificado.")
	@GetMapping("/{idSensor}/historico")
	public ResponseEntity<Page<Medicion>> obtenerHistoricoSensor(@PathVariable Long idSensor,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "1") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<Medicion> historicoPaginado = servicioSensor.obtenerHistoricoSensor(idSensor, pageable);
		return ResponseEntity.ok(historicoPaginado);
	}

}
