package com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.exception.SensorDuplicadoException;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.exception.SensorNoEncontradoException;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.modelo.Sensor;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.repository.SensorRepository;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.dto.SensorDTO;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.modelo.Medicion;

@Service
public class ServicioSensorImpl implements ServicioSensor{
	
	private SensorRepository sensorRepository;
	
	@Autowired
	public ServicioSensorImpl (SensorRepository sensorRepository) {
		this.sensorRepository=sensorRepository;
	}

	@Override
	public Long crearSensor(String tipo, String magnitud) {
	    if (tipo == null || tipo.isEmpty())
	        throw new IllegalArgumentException("tipo: no debe ser nulo ni vacio");
	    if (magnitud == null || magnitud.isEmpty())
	        throw new IllegalArgumentException("magnitud: no debe ser nulo ni vacio");

	    if (sensorRepository.findByTipo(tipo).isPresent()) {
	        throw new SensorDuplicadoException("Ya existe un sensor con el tipo: " + tipo);
	    }

	    Sensor sensor = new Sensor(tipo, magnitud);
	    Sensor sensorSave = sensorRepository.save(sensor);
	    return sensorSave.getId();
	}

	@Override
	public void borrarSensor(Long id) {
	    if (id == null)
	        throw new IllegalArgumentException("id del sensor: no debe ser nulo ni vacio");
	    
	    Sensor sensor = sensorRepository.findById(id)
	        .orElseThrow(() -> new SensorNoEncontradoException("Sensor no encontrado con ID: " + id));
	    
	    sensorRepository.delete(sensor);
	}
	
	@Override
	public List<SensorDTO> listarSensores() {
		return sensorRepository.findAll().stream()
			.map(sensor -> new SensorDTO(
				sensor.getId(),
				sensor.getTipo(),
				sensor.getMagnitud(),
				sensor.getHistorico()))
			.toList();
	}

	@Override
	public double obtenerYActualizarValor(Long idSensor) {
		Optional<Sensor> sensorOpt = sensorRepository.findById(idSensor);
		if (sensorOpt.isPresent()) {
			Sensor sensor = sensorOpt.get();
			sensor.actualizarValor();
			sensorRepository.save(sensor);
			return sensor.getValorActual();
		}
		throw new IllegalArgumentException("Sensor no encontrado con ID: " + idSensor);
	}

	@Override
	public double calcularMedia(Long idSensor, LocalDateTime fechaInicio, LocalDateTime fechaFin) {

		if (idSensor == null)
			throw new IllegalArgumentException("id del sensor: no debe ser nulo");
		if (fechaInicio == null)
        	throw new IllegalArgumentException("fechaInicio: no debe ser nula");
    	if (fechaFin == null)
        	throw new IllegalArgumentException("fechaFin: no debe ser nula");
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("fechaInicio no puede ser posterior a fechaFin");
        }

		Sensor sensor = sensorRepository.findById(idSensor)
			.orElseThrow(() -> new IllegalArgumentException("Sensor no encontrado con ID: " + idSensor));
		
		List<Medicion> mediciones = sensor.getHistorico();
		
		if(mediciones.isEmpty()) {
			return 0.0;
		}
		
		double suma = 0.0;
		int contador = 0;
		
		for(Medicion medicion : mediciones) {
			LocalDateTime fechaMedicion = medicion.getFecha();
			if(!fechaMedicion.isBefore(fechaInicio) && !fechaMedicion.isAfter(fechaFin)) {
				suma += medicion.getValor();
				contador++;
			}
		}
		
		if(contador == 0) {
			return 0.0;
		}
		
		return suma / contador;
	}

	@Override
	public Page<Medicion> obtenerHistoricoSensor(Long idSensor, Pageable pageable) {
	    if (idSensor == null) {
	        throw new IllegalArgumentException("id del sensor: no debe ser nulo");
	    }
	    
	    Sensor sensor = sensorRepository.findById(idSensor)
	        .orElseThrow(() -> new IllegalArgumentException("Sensor no encontrado con ID: " + idSensor));
	    
	    List<Medicion> historico = sensor.getHistorico();
	    
	    int start = (int) pageable.getOffset();
	    int end = start + pageable.getPageSize();
	    if (end > historico.size()) {
	        end = historico.size();
	    }
	    
	    List<Medicion> subList = new ArrayList<>();
	    if (start < end) {
	        subList = historico.subList(start, end);
	    }
	    
	    return new PageImpl<>(subList, pageable, historico.size());
	}

}
