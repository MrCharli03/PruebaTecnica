package com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.dto.response.SensorErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(SensorDuplicadoException.class)
    public ResponseEntity<SensorErrorResponse> handleSensorDuplicadoException(SensorDuplicadoException e) {
        SensorErrorResponse response = new SensorErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SensorErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        SensorErrorResponse response = new SensorErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

