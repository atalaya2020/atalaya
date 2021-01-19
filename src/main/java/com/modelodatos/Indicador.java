package com.modelodatos;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="indicador")
public class Indicador {
	@Id
	private String id;
	
	private String nombre;
	private String fuente;
	private String comando;
	
	public Indicador() {
		// TODO Auto-generated constructor stub
	}
	
	public Indicador(String nombre, String fuente, String comando) {
		
		super();
		this.setNombre(nombre);
		this.setFuente(fuente);
		this.setComando(comando);
		// TODO Auto-generated constructor stub
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getFuente() {
		return fuente;
	}
	public void setFuente(String fuente) {
		this.fuente = fuente;
	}
	public String getComando() {
		return comando;
	}
	public void setComando(String comando) {
		this.comando = comando;
	}
}
