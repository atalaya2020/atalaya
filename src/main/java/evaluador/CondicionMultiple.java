package evaluador;

import java.util.ArrayList;

public class CondicionMultiple {
	private String tipo;
	private boolean resultado;
	private Integer madre;
	private Integer idCondicion;
	private String texto;
	private Integer nivel;	
	private Condicion condicion;

	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public boolean getResultado() {
		return resultado;
	}
	public void setResultado(boolean resultado) {
		this.resultado = resultado;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}	
	public Integer getMadre() {
		return madre;
	}
	public void setMadre(Integer madre) {
		this.madre = madre;
	}		
	public Integer getIdCondicion() {
		return idCondicion;
	}
	public void setIdCondicion(Integer idCondicion) {
		this.idCondicion = idCondicion;
	}		
	public Integer getNivel() {
		return nivel;
	}
	public void setNivel(Integer nivel) {
		this.nivel = nivel;
	}		
	public Condicion getCondicion() {
		return condicion;
	}
	public void setCondicion(Condicion condicion) {
		this.condicion = condicion;
	}	
	
	public CondicionMultiple () {
		this.madre = null;
		this.condicion = null;
		this.tipo = null;
		this.tipo = null;
	}
	
	public boolean evalua (ArrayList<CondicionMultiple> evaluacion) {
		boolean result = true;
		CondicionMultiple multiple;
		
		if (tipo == "AND")		{	resultado = true;		}
		else if (tipo == "OR") 	{	resultado = false;		}
		
		if (condicion == null) {
			for (int i = 0; i < evaluacion.size(); i++) { 
				multiple = evaluacion.get(i);
				if (multiple.getMadre() == idCondicion)	{					
					if (tipo == "AND" && multiple.getResultado() == false) {
						resultado = false;
					}
					if (tipo == "OR" && multiple.getResultado() == true) {
						resultado = true;
					}					
				}			
			}			
		} else {
			resultado = condicion.evalua();
		}		
		return result;	
	}		
		

}
