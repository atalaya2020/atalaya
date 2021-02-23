package com.modelodatos;


import java.util.ArrayList;

public class Indicador {

	public Indicador() {
		super();
		// TODO Auto-generated constructor stub
	}

	private String nombre;
	private String descripcion;
	private String fuente;
	private String tipo;
	private String comando;
	private ArrayList<Parametro> parametros;
	private String[] resultado;

	public Indicador(String nombre, String descripcion, String fuente, String tipo, String comando,
			ArrayList<Parametro> parametros, String[] resultado) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.fuente = fuente;
		this.tipo = tipo;
		this.comando = comando;
		this.parametros = parametros;
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
	public String getFuente() {
		return fuente;
	}
	public void setFuente(String fuente) {
		this.fuente = fuente;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getComando() {
		return comando;
	}
	public void setComando(String comando) {
		this.comando = comando;
	}
	public ArrayList<Parametro> getParametros() {
		return parametros;
	}
	public void setParametros(ArrayList<Parametro> parametros) {
		this.parametros = parametros;
	}
	public String[] getResultado() {
		return resultado;
	}
	public void setResultado(String[] resultado) {
		this.resultado = resultado;
	}
}
