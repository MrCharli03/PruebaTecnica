package com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.context.annotation.ComponentScan;
import java.util.List;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.dto.SensorDTO;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.modelo.Medicion;

import java.time.LocalDateTime;
@ComponentScan
public interface ServicioSensor {

	Long crearSensor(String tipo, String magnitud);
	
	void borrarSensor(Long id);
	
	List<SensorDTO> listarSensores();
	
	double obtenerYActualizarValor(Long idSensor);
	
	double calcularMedia(Long idSensor, LocalDateTime fechaInicio, LocalDateTime fechaFin);
	
	Page<Medicion> obtenerHistoricoSensor(Long idSensor, Pageable pageable);
	
}
