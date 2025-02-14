package com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.dto;

import java.util.List;

import com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.modelo.Medicion;

public class SensorDTO {
	private Long id;
	private String tipo;
	private String magnitud;
	private List<Medicion> historico;

	public SensorDTO(Long id, String tipo, String magnitud, List<Medicion> historico) {
		this.id = id;
		this.tipo = tipo;
		this.magnitud = magnitud;
		this.historico = historico;
	}

	public Long getId() {
		return id;
	}

	public String getTipo() {
		return tipo;
	}

	public String getMagnitud() {
		return magnitud;
	}

	public List<Medicion> getHistorico() {
		return historico;
	}

}
