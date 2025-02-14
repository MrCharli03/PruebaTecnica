package com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.dto.SensorDTO;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.exception.SensorDuplicadoException;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.modelo.Medicion;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.modelo.Sensor;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.repository.SensorRepository;

@ExtendWith(MockitoExtension.class)
class ServicioSensorImplTest {

    @Mock
    private SensorRepository sensorRepository;

    @InjectMocks
    private ServicioSensorImpl servicioSensor;

    private Sensor sensor;
    private final Long SENSOR_ID = 1L;

    @BeforeEach
    void setUp() {
        sensor = new Sensor("Temperatura", "°C");
        sensor.setId(SENSOR_ID);
    }

    @Test
    void crearSensor_DatosValidos_RetornaId() {
        when(sensorRepository.findByTipo(any())).thenReturn(Optional.empty());
        when(sensorRepository.save(any())).thenReturn(sensor);

        Long result = servicioSensor.crearSensor("Temperatura", "°C");
        
        assertEquals(SENSOR_ID, result);
        verify(sensorRepository).save(any());
    }

    @Test
    void crearSensor_SensorDuplicado_LanzaExcepcion() {
        when(sensorRepository.findByTipo(any())).thenReturn(Optional.of(sensor));
        
        assertThrows(SensorDuplicadoException.class, () -> {
            servicioSensor.crearSensor("Temperatura", "°C");
        });
    }

    @Test
    void borrarSensor_IdValido_EliminaSensor() {
        servicioSensor.borrarSensor(SENSOR_ID);
        verify(sensorRepository).deleteById(SENSOR_ID);
    }

    @Test
    void listarSensores_SensoresExistentes_RetornaListaDTO() {
        List<Sensor> sensores = List.of(sensor);
        when(sensorRepository.findAll()).thenReturn(sensores);

        List<SensorDTO> result = servicioSensor.listarSensores();
        
        assertEquals(1, result.size());
        assertEquals(SENSOR_ID, result.get(0).getId());
    }

    @Test
    void obtenerYActualizarValor_SensorExistente_RetornaValorActualizado() {
        when(sensorRepository.findById(SENSOR_ID)).thenReturn(Optional.of(sensor));
        
        double result = servicioSensor.obtenerYActualizarValor(SENSOR_ID);
        
        assertTrue(result >= 0 && result <= 100);
        verify(sensorRepository).save(sensor);
    }

    @Test
    void calcularMedia_RangoValido_RetornaMediaCorrecta() {
        List<Medicion> mediciones = new ArrayList<>();
        mediciones.add(new Medicion(10.0));
        mediciones.add(new Medicion(20.0));
        sensor.setHistorico(mediciones);
        
        when(sensorRepository.findById(SENSOR_ID)).thenReturn(Optional.of(sensor));

        LocalDateTime inicio = LocalDateTime.now().minusDays(2);
        LocalDateTime fin = LocalDateTime.now();
        
        double media = servicioSensor.calcularMedia(SENSOR_ID, inicio, fin);
        
        assertEquals(15.0, media);
    }

    @Test
    void obtenerHistoricoSensor_PaginacionValida_RetornaPagina() {
        List<Medicion> mediciones = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            mediciones.add(new Medicion(i * 10.0));
        }
        sensor.setHistorico(mediciones);
        
        when(sensorRepository.findById(SENSOR_ID)).thenReturn(Optional.of(sensor));
        
        Pageable pageable = PageRequest.of(0, 5);
        Page<Medicion> resultado = servicioSensor.obtenerHistoricoSensor(SENSOR_ID, pageable);
        
        assertEquals(5, resultado.getContent().size());
        assertEquals(10, resultado.getTotalElements());
    }

    // Pruebas para validaciones
    @Test
    void crearSensor_CamposNulos_LanzaExcepcion() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, 
                () -> servicioSensor.crearSensor(null, "°C")),
            () -> assertThrows(IllegalArgumentException.class, 
                () -> servicioSensor.crearSensor("Temperatura", null))
        );
    }

    @Test
    void calcularMedia_FechasInvertidas_LanzaExcepcion() {
        LocalDateTime ahora = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> {
            servicioSensor.calcularMedia(SENSOR_ID, ahora, ahora.minusDays(1));
        });
    }

    @Test
    void obtenerHistoricoSensor_IdNulo_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            servicioSensor.obtenerHistoricoSensor(null, PageRequest.of(0, 10));
        });
    }
}
