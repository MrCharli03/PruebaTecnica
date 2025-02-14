package com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.modelo.Sensor;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long>{

	Optional<Sensor> findByTipo(String tipo);
	
}
