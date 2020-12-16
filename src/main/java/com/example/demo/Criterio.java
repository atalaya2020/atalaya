package com.example.demo;

public class Criterio {

	private String nombre;
	private String descripcion;
	private String evaluacion;
	private String tipoResultado;
	private Object resultado;

	public Criterio(String nombre, String descripcion, String evaluacion, String tipoResultado, Object resultado) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.evaluacion = evaluacion;
		this.tipoResultado = tipoResultado;
		this.resultado = resultado;
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

	public String getEvaluacion() {
		return evaluacion;
	}

	public void setEvaluacion(String evaluacion) {
		this.evaluacion = evaluacion;
	}

	public String getTipoResultado() {
		return tipoResultado;
	}

	public void setTipoResultado(String tipoResultado) {
		this.tipoResultado = tipoResultado;
	}

	public Object getResultado() {
		return resultado;
	}

	public void setResultado(Object resultado) {
		this.resultado = resultado;
	}

}