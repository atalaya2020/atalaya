package com.modelodatos;


public class Configuracion {

	
	private String nombre;
	private String descripcion;
	private Parametro parametro;
	
	public Configuracion(String nombre, String descripcion, Parametro parametro) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.parametro = parametro;
		
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Parametro getParametro() {
		return parametro;
	}

	public void setParametro(Parametro parametro) {
		this.parametro = parametro;
	}
	
	
}
