package com.atalaya.evaluador;

import java.util.ArrayList;
import com.modelodatos.Analisis;
import com.modelodatos.Criterio;
import com.modelodatos.Parametro;


public class AnalisisProxy {

	private String nombre;
	private String descripcion;
	private ArrayList<IndicadorProxy> indicadores;
	private ArrayList<CriterioProxy> criterios;
	private ArrayList<EventoProxy> eventos;
	private ArrayList<ParametroProxy> parametros;

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
	
	public ArrayList<IndicadorProxy> getIndicadores() {
		return indicadores;
	}
	
	public void setIndicadores(ArrayList<IndicadorProxy> indicadores) {
		this.indicadores = indicadores;
	}
	
	public ArrayList<CriterioProxy> getCriterios() {
		return criterios;
	}
	
	public void setCriterios(ArrayList<CriterioProxy> criterios) {
		this.criterios = criterios;
	}	
	
	public ArrayList<EventoProxy> getEventos() {
		return eventos;
	}
	
	public void setEventos(ArrayList<EventoProxy> eventos) {
		this.eventos = eventos;
	}	
	
	public ArrayList<ParametroProxy> getParametros() {
		return parametros;
	}
	public void setParametros(ArrayList<ParametroProxy> parametros) {
		this.parametros = parametros;
	}	
	
	public AnalisisProxy (Analisis analisis) {			
		this.parametros = new ArrayList<ParametroProxy>();
		cargarAnalisisProxy(analisis);
	}
	
	public AnalisisProxy (Analisis analisis, ArrayList<ParametroProxy> parametros) {
		this.parametros = parametros;
		cargarAnalisisProxy(analisis);
	}
	
	private void cargarAnalisisProxy(Analisis analisis ) {
		
		this.nombre = analisis.getNombre();
		this.descripcion = analisis.getDescripcion();
		this.indicadores = new ArrayList<IndicadorProxy>();
		this.eventos = new ArrayList<EventoProxy>();
		this.criterios = new ArrayList<CriterioProxy>();	
		
		for (int i = 0; i < analisis.getIndicadores().size(); i++) {
			IndicadorProxy indProxy = new IndicadorProxy(analisis.getIndicadores().get(i));
			this.indicadores.add(i, indProxy);
		}
		
		for (int i = 0; i < analisis.getCriterios().size(); i++) {
			CriterioProxy criProxy = new CriterioProxy(analisis.getCriterios().get(i));
			this.criterios.add(i, criProxy);
		}
		
		for (int i = 0; i < analisis.getEventos().size(); i++) {
			EventoProxy eveProxy = new EventoProxy(analisis.getEventos().get(i));
			this.eventos.add(i, eveProxy);
		}
		
	}
}
