package com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name="sensores", uniqueConstraints = @UniqueConstraint(columnNames = "tipo"))
public class Sensor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true)
	private String tipo;
	@Column(nullable = false)
    private String magnitud;
    private double valorActual;
    @ElementCollection
    private List<Medicion> historico;
    private Random random;
    
    public Sensor() {
    	
    }
    
    public Sensor(String tipo, String magnitud) {
        this.tipo = tipo;
        this.magnitud = magnitud;
        this.historico = new ArrayList<>();
        this.random = new Random();
        this.valorActual = generarValorAleatorio(); // Inicializa con un valor aleatorio
    }
    
    private double generarValorAleatorio() {
        switch (this.magnitud.toLowerCase()) {
            case "temperatura":
                return -20 + (50 - (-20)) * random.nextDouble(); // -20°C a 50°C
            case "humedad":
                return random.nextDouble() * 100; // 0-100%
            case "presion":
                return 900 + (1100 - 900) * random.nextDouble(); // 900-1100 hPa
            case "luz":
                return random.nextDouble() * 1000; // 0-1000 lux
            default:
                return random.nextDouble() * 100; // Valor por defecto
        }
    }

    public void actualizarValor() {
        this.valorActual = generarValorAleatorio();
        historico.add(new Medicion(valorActual)); // La fecha se registrará automáticamente en Medicion
    }
    
	public String getTipo() {
		return tipo;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public String getMagnitud() {
		return magnitud;
	}
	
	public void setMagnitud(String magnitud) {
		this.magnitud = magnitud;
	}
	
	public double getValorActual() {
		return valorActual;
	}
	
	public void setValorActual(double valorActual) {
		this.valorActual = valorActual;
	}
	
	public List<Medicion> getHistorico() {
		return historico;
	}
	
	public void setHistorico(List<Medicion> historico) {
		this.historico = historico;
	}
	
	@Override
	public String toString() {
		return "Sensor [id=" + id + ", tipo=" + tipo + ", magnitud=" + magnitud + ", valorActual=" + valorActual
				+ ", historico=" + historico + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
