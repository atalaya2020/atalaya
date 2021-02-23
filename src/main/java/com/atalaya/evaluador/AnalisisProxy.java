package com.atalaya.evaluador;

import java.util.ArrayList;
import java.util.Hashtable;

import com.modelodatos.Analisis;
import com.modelodatos.Parametro;


public class AnalisisProxy {

	
	private Analisis analisis;
	
	private Hashtable<String,IndicadorProxy> indicadores;
	
	private ArrayList<CriterioProxy> criterios;
	private ArrayList<EventoProxy> eventos;
	private ArrayList<Parametro> parametros;

	
	
	public Hashtable<String,IndicadorProxy> getIndicadores() {
		return indicadores;
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
	
	public ArrayList<Parametro> getParametros() {
		return parametros;
	}
	public void setParametros(ArrayList<Parametro> parametros) {
		this.parametros = parametros;
	}
	
	public Analisis getAnalisis() {
		return analisis;
	}
	
	public void setAnalisis(Analisis analisis) {
		this.analisis = analisis;
	}
	
	public AnalisisProxy (Analisis analisis) {			
		this.parametros = new ArrayList<Parametro>();
		cargarAnalisisProxy(analisis);
	}
	
	public AnalisisProxy (Analisis analisis, ArrayList<Parametro> parametros) {
		this.setParametros(parametros);
		this.setAnalisis(analisis);

		cargarAnalisisProxy(analisis);
	}
	
	//Este metodo tendria mas sentido si le metemos la validacion de cada uno de los elementos del analisis
	private void cargarAnalisisProxy(Analisis analisis) {
		
		//Aquí tengo que recoger los datos de configuración de los hilos
		this.indicadores = new Hashtable<String,IndicadorProxy>();
		this.eventos = new ArrayList<EventoProxy>();
		this.criterios = new ArrayList<CriterioProxy>();	
		
		for (int i = 0; i < analisis.getIndicadores().size(); i++) {
			IndicadorProxy indProxy = new IndicadorProxy(analisis.getIndicadores().get(i));
			
			//Recorro los parametros del indicador para identificar aquellos que hacen referencia a un parametro de entrada #PARAM.NOMBRE_PARAMETRO
			ArrayList<Parametro> parametros = indProxy.getIndicador().getParametros();
			for(int j=0;j<parametros.size();j++)
			{
				//Si el parametro es del tipo #PARAM debo darle el valor recibido como parametro
				if (parametros.get(j).getValor().startsWith("#PARAM"))
				{
					String[] arValor = parametros.get(j).getValor().split("\\.");
					if (this.parametros!=null)
					{
						for (int k=0;k<this.parametros.size();k++)
						{
							if (this.parametros.get(k).getNombre().equals(arValor[1]))
								parametros.get(j).setValor(this.parametros.get(k).getValor());
						}
					}
				}
			}
			
			indicadores.put(analisis.getIndicadores().get(i).getNombre(), indProxy);
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
