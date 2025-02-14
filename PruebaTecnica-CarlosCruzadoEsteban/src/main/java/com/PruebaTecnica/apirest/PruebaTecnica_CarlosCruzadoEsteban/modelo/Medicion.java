package com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.modelo;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;

@Embeddable
public class Medicion {
	
	private LocalDateTime fecha;
    private double valor;
    
    public Medicion(double valor) {
        this.fecha = LocalDateTime.now();
        this.valor = valor;
    }
    
    public Medicion() {
    	
    }
    
	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}
	
	public double getValor() {
		return valor;
	}
	
	@Override
	public String toString() {
		return "Medicion [fecha=" + fecha + ", valor=" + valor + "]";
	}
    
}
