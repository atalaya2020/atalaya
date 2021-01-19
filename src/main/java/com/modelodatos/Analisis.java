package com.modelodatos;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="analisis")
public class Analisis {
	
	public Analisis(String nombre, String descripcion, String departamento, String valor) {
		super(); 
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.departamento = departamento;
		this.valor = valor;
	}
	private String nombre;
	private String descripcion;
	private String departamento;
	private String valor;

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
	public String getDepartamento() {
		return departamento;
	}
	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
}
