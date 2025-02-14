package com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.dto.SensorDTO;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.exception.SensorDuplicadoException;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.modelo.Medicion;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.modelo.Sensor;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.rest.SensorController;
import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.service.ServicioSensor;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(SensorController.class)
class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

	@MockBean
    private ServicioSensor servicioSensor;

    @Autowired
    private ObjectMapper objectMapper;

    private Sensor sensor;
    private final Long SENSOR_ID = 1L;

    @BeforeEach
    void setUp() {
        sensor = new Sensor("Temperatura", "°C");
        sensor.setId(SENSOR_ID);
    }
    
    @Test
    void altaSensor_Correcto_Devuelve201() throws Exception {
        when(servicioSensor.crearSensor(any(), any())).thenReturn(SENSOR_ID);
        
        mockMvc.perform(post("/sensores")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"Temperatura\",\"magnitud\":\"°C\"}")) // Cambiar a JSON directo
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(SENSOR_ID))
                .andExpect(jsonPath("$.message").value("¡Objeto creado con exito!"));
    }

    @Test
    void altaSensor_Duplicado_Devuelve400() throws Exception {
        // Configuramos el mock: la primera llamada devuelve el id exitoso y la segunda lanza la excepción
        when(servicioSensor.crearSensor(any(), any()))
            .thenReturn(SENSOR_ID)
            .thenThrow(new SensorDuplicadoException("Ya existe un sensor con el tipo: Temperatura"));
        
        // Primera invocación: se guarda correctamente el sensor
        mockMvc.perform(post("/sensores")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"Temperatura\",\"magnitud\":\"°C\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(SENSOR_ID))
                .andExpect(jsonPath("$.message").value("¡Objeto creado con exito!"));
        
        // Segunda invocación: se intenta guardar el mismo sensor y se espera la excepción
        mockMvc.perform(post("/sensores")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"Temperatura\",\"magnitud\":\"°C\"}"))
                .andExpect(status().isPreconditionFailed()) // O isBadRequest() si se espera 400
                .andExpect(jsonPath("$.message").value("Ya existe un sensor con el tipo: Temperatura"));
    }


    @Test
    void bajaSensor_Correcto_Devuelve204() throws Exception {
        mockMvc.perform(delete("/sensores/{idSensor}", SENSOR_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void obtenerTodosSensores_ConDatos_Devuelve200() throws Exception {
        SensorDTO dto = new SensorDTO(SENSOR_ID, "Temperatura", "°C", Collections.emptyList());
        when(servicioSensor.listarSensores()).thenReturn(Collections.singletonList(dto));
        
        mockMvc.perform(get("/sensores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(SENSOR_ID));
    }

    @Test
    void obtenerValorSensor_Existente_Devuelve200() throws Exception {
        when(servicioSensor.obtenerYActualizarValor(anyLong())).thenReturn(50.0);
        
        mockMvc.perform(get("/sensores/{idSensor}", SENSOR_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("50.0"));
    }

    @Test
    void obtenerMediaSensor_FechasValidas_DevuelveMedia() throws Exception {
        when(servicioSensor.calcularMedia(anyLong(), any(), any())).thenReturn(25.5);
        
        mockMvc.perform(get("/sensores/{idSensor}/media/2024-01-01T00:00:00/2024-01-02T00:00:00", SENSOR_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("25.5"));
    }

    @Test
    void obtenerMediaSensor_FormatoFechaInvalido_Devuelve400() throws Exception {
        mockMvc.perform(get("/sensores/{idSensor}/media/20240101/invalid-date", SENSOR_ID)) 
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Formato de fecha incorrecto. Use 'yyyy-MM-dd'T'HH:mm:ss' o 'yyyy-MM-dd'T'HH:mm:ss.SSS'."))); // Validación más flexible
    }
    
    @Test
    void obtenerHistoricoSensor_Paginacion_DevuelvePagina() throws Exception {
        Page<Medicion> pagina = new PageImpl<>(Collections.singletonList(new Medicion(25.0)));
        when(servicioSensor.obtenerHistoricoSensor(anyLong(), any())).thenReturn(pagina);
        
        mockMvc.perform(get("/sensores/{idSensor}/historico?page=0&size=1", SENSOR_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].valor").value(25.0));
    }

    // Pruebas adicionales para cobertura completa
    @Test
    void obtenerValorSensor_NoExistente_Devuelve400() throws Exception {
        when(servicioSensor.obtenerYActualizarValor(anyLong()))
            .thenThrow(new IllegalArgumentException("Sensor no encontrado"));
        
        mockMvc.perform(get("/sensores/{idSensor}", 999))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerMediaSensor_FechasInvertidas_Devuelve400() throws Exception {
        when(servicioSensor.calcularMedia(anyLong(), any(), any()))
            .thenThrow(new IllegalArgumentException("Fechas invertidas"));
        
        mockMvc.perform(get("/sensores/{idSensor}/media/2024-01-02T00:00:00/2024-01-01T00:00:00", SENSOR_ID))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerHistoricoSensor_IdInvalido_Devuelve400() throws Exception {
        when(servicioSensor.obtenerHistoricoSensor(anyLong(), any()))
            .thenThrow(new IllegalArgumentException("ID inválido"));
        
        mockMvc.perform(get("/sensores/{idSensor}/historico", 999))
                .andExpect(status().isBadRequest());
    }
}
