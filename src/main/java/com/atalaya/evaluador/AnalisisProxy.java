package com.atalaya.evaluador;
import java.util.ArrayList;
import com.modelodatos.Criterio;

public class AnalisisProxy {

	private ArrayList<IndicadorProxy> indicadores;
	private ArrayList<Criterio> criterios;
	private ArrayList<IndicadorProxy> eventos;
	
	public void setIndicadores(ArrayList<IndicadorProxy> indicadores) {
		this.indicadores = indicadores;
	}	
	
	public ArrayList<IndicadorProxy> getIndicadores() {
		return indicadores;
	}
	
	public void setCriterios(ArrayList<Criterio> criterios) {
		this.criterios = criterios;
	}	
	
	public ArrayList<Criterio> getCriterios() {
		return criterios;
	}	

	public void setEventos(ArrayList<IndicadorProxy> eventos) {
		this.eventos = eventos;
	}	
	
	public ArrayList<IndicadorProxy> getEventos() {
		return eventos;
	}
	
	public AnalisisProxy (ArrayList<IndicadorProxy> indicadores, ArrayList<Criterio> criterios, ArrayList<IndicadorProxy> eventos) {
		this.indicadores = indicadores;
		this.criterios = criterios;
		this.eventos = eventos;
	}
}
