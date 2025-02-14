package com.PruebaTecnica.apirest.PruebaTecnica_CarlosCruzadoEsteban.dto.response;

public class SensorResponse {

	private Long id;
	private String message;

	public SensorResponse(Long id, String message) {
		this.id = id;
		this.message = message;
	}
	
	public SensorResponse(String message) {
		this.message = message;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
