package com.modelodatos;

import java.util.ArrayList;
import com.atalaya.evaluador.IndicadorProxy;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="analisis")
public class Analisis {
	private String nombre;
	private String descripcion;
	private ArrayList<Indicador> indicadores;
	private ArrayList<Criterio> criterios;
	private ArrayList<Indicador> eventos;

	public Analisis(String nombre, String descripcion , ArrayList<Indicador> indicadores, ArrayList<Criterio> criterios, ArrayList<Indicador> eventos) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.indicadores = indicadores;
		this.criterios = criterios;
		this.eventos = eventos;
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

	public ArrayList<Indicador> getIndicadores() {
		return indicadores;
	}

	public void setIndicadores(ArrayList<Indicador> indicadores) {
		this.indicadores = indicadores;
	}

	public ArrayList<Criterio> getCriterios() {
		return criterios;
	}

	public void setCriterios(ArrayList<Criterio> criterios) {
		this.criterios = criterios;
	}

	public ArrayList<Indicador> getEventos() {
		return eventos;
	}

	public void setEventos(ArrayList<Indicador> eventos) {
		this.eventos = eventos;
	}
	
	
	public void eventosCriterio (String [] eventosCriterio) {
		
		for (int ec = 0; ec < eventosCriterio.length; ec++) {
			for (int e = 0; e < this.eventos.size(); e++) {
				if (eventosCriterio[ec].equals(this.eventos.get(e).getNombre())) {
					IndicadorProxy evento = new IndicadorProxy(this.eventos.get(e));
					evento.generarEvento();					
				}
			}
		}
	}

	/*public boolean validar() {
		boolean valido = true;

		if(this.getNombre().equals("")) {
			valido = false;
						// Falta nombre
		}
		else if(this.getDescripcion().equals("")) {
			valido = false;
			// Falta descripcion
		}

		/*else {

			ArrayList<Indicador> indicadores= this.getIndicadores();

			for (int i = 0; i<indicadores.size(); i++) {
				valido = indicadores.get(i).validar();
				if(valido == false) {
					return valido;
					// un indicador no es valido
				}
			}

			ArrayList<Criterio> criterios= this.getCriterios();

			for (int i = 0; i<criterios.size(); i++) {
				valido = criterios.get(i).validar();
				if(valido == false) {
					return valido;
					// un criterio no es valido
				}
			}

			ArrayList<Evento> eventos= this.getEventos();

			for (int i = 0; i<eventos.size(); i++) {
				valido = eventos.get(i).validar();
				if(valido == false) {
					return valido;
					// un criterio no es valido
				}
			}

		}






		return valido;
	}*/

}
