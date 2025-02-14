package com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.modelo.Sensor;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.repository.SensorRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SensorRepository sensorRepository;

    public DataInitializer(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Se crea un sensor de temperatura asignando "temperatura" como magnitud
        Sensor sensorTemperatura = new Sensor("Temperatura", "ºC");
        sensorRepository.save(sensorTemperatura);


        Sensor sensorHumedad = new Sensor("Humedad", "%");
        sensorRepository.save(sensorHumedad);

    }
}
