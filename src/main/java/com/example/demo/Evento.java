package com.example.demo;

public class Evento {
	
	private String formatoSalida;
	private Object salida;
	
	public Evento(String formatoSalida, Object salida) {
		super();
		this.formatoSalida = formatoSalida;
		this.salida = salida;
	}
	public String getFormatoSalida() {
		return formatoSalida;
	}
	public void setFormatoSalida(String formatoSalida) {
		this.formatoSalida = formatoSalida;
	}
	public Object getSalida() {
		return salida;
	}
	public void setSalida(Object salida) {
		this.salida = salida;
	}
	
}