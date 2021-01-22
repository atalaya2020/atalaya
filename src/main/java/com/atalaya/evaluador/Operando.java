package com.atalaya.evaluador;

import java.util.Vector;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;

import com.modelodatos.Indicador;
import com.modelodatos.Parametro;

public class Operando {
	private String nombre;
	private int tipo;
	private int tipoValor;
	private boolean negado;
	private Object resultado;
	private boolean ejecutado;
	private IndicadorProxy indicador;
	
	private Comunes constantes;	
	
	public String getNombre() {		
		return nombre;
	}
	public void setNombre(String nombre) {
		this.negado = operandoNegado(nombre);
		this.nombre = nombre.replaceAll("NOT ", "").trim();	
	}

	public int getTipo() {		
		return tipo;
	}
	
	public void setTipo(int tipo) {
		this.tipo = tipo;			
	}	
	
	public int getTipoValor() {		
		return tipoValor;
	}
	public void setTipoValor(int tipoValor) {
		this.tipoValor = tipoValor;			
	}		
	
	public boolean getNegado() {		
		return negado;
	}
	public void setNegado(boolean negado) {
		this.negado = negado;			
	}	
	
	public boolean getEjecutado() {		
		return ejecutado;
	}
	public void setEjecutado(boolean ejecutado) {
		this.ejecutado = ejecutado;			
	}
	
	public Object getResultado() {		
		ejecutar();
		return resultado;
	}
	public void setResultado(Object resultado) {
		this.resultado = resultado;			
	}	
	
	public IndicadorProxy getIndicador() {
		return indicador;
	}
	public void setIndicador(IndicadorProxy indicador) {
		this.indicador = indicador;
	}
	
	public void Operando () {	
		this.tipo = 0;
		this.negado = false;
	}
	
	public void Operando (String nombre) {
		this.nombre = nombre;
		this.tipo = 0;
		this.negado = false;
	}	
	
	private boolean operandoNegado(String operando) {
		boolean negado = false;
		if (operando.trim().startsWith("NOT")) {
			negado = true;
		}		
		return negado;
	}	
	
	private void ejecutar() {
	// Ejecuta el operando. Si es un indicador, ejecutará el indicador. Si no, convertirá el valor al tipo correspondiente.	
		if (!this.ejecutado)
		{
			if (this.tipo == constantes.tpIndicador) 
			{
				if (!this.getIndicador().isFlag()) {
					//parametrosIndicador();
					if (this.getIndicador().ejecutar() == 0) {
						String[] tramos = new String [] {};
						tramos = this.getNombre().split("\\.");
						Object valParam = new Object();
						Object[] linea = this.getIndicador().getResultadoEjecucion().elementAt(0);
						int c = 0;
						while (c < this.getIndicador().getIndicador().getResultado().length) {
							if (tramos[1].equals(this.getIndicador().getIndicador().getResultado()[c])) {
								valParam = linea[c];
								break;
							}
							c++;
						}
						this.resultado = valParam;	
						
						if (this.nombre.toUpperCase().endsWith("ROWCOUNT")) {
							this.resultado = this.indicador.getResultadoEjecucion().size();
						}
					} else {
						this.resultado = null;
					}
				}
				else
				{
					String[] tramos = new String [] {};
					tramos = this.getNombre().split("\\.");
					Object valParam = new Object();
					Object[] linea = this.getIndicador().getResultadoEjecucion().elementAt(0);
					int c = 0;
					while (c < this.getIndicador().getIndicador().getResultado().length) {
						if (tramos[1].equals(this.getIndicador().getIndicador().getResultado()[c])) {
							valParam = linea[c];
							break;
						}
						c++;
					}
					this.resultado = valParam;	
					
					if (this.nombre.toUpperCase().endsWith("ROWCOUNT")) {
						this.resultado = this.indicador.getResultadoEjecucion().size();
					}
				}
			} 
			else 
			{
				if (this.tipo == constantes.tpValor) {
					if (this.tipoValor == constantes.tpVlBoolean) {
						this.resultado = Boolean.parseBoolean(this.nombre);
					} else {
						if (this.tipoValor == constantes.tpVlString) {
							this.resultado = this.nombre;
						} else {
							if (this.tipoValor == constantes.tpVlInt) {
								this.resultado = Integer.parseInt(this.nombre);
							} 
						}
					}
					
				}				
			}
		}
		if (this.resultado != null) {
			this.ejecutado = true;
		}
	}
	
/*	private void parametrosIndicador() {
		// Recorre los parámetros del indicador para asignar los valores que tengan referencias a otros indicadores. 
		IndicadorProxy indOper = this.indicador;			
	
		for (int p = 0; p < indOper.getIndicador().getParametros().size(); p++) {			
			if (indOper.getIndicador().getParametros().get(p).getValor().startsWith(constantes.tpMarcaIndicador)) {
				
				String[] tramos = new String [] {};
				tramos = indOper.getIndicador().getParametros().get(p).getValor().split("\\.");
				
				for (int i = 0; i < indicadores.size(); i++) 
				{	
					if (indicadores.get(i).getIndicador().getNombre().equals(tramos[0].substring(1))) 
					{
						oper.setIndicador(indicadores.get(i));
						break;
					}
				}
				
				if (indOper.getParametros().get(p).getIndicador().ejecutar() == 0) {					
					Object valor = calcularValorParametro (indOper.getParametros().get(p));	
					indOper.getParametros().get(p).setValor(valor.toString());					
				}
			}
		}
	}
	
	private Object calcularValorParametro(Parametro param) {
	// Si el valor de un parámetro, extrae del resultado de la ejecución de ese indicador el valor que debe asignar al parámetro 
		Object valParam = new Object();
		Indicador indParam = param.getIndicador();		

	// Recupera la primera fila del resultado de ejecución
		Object[] linea = param.getIndicador().getResultadoEjecucion().elementAt(0);
		
	// Del nombre del indicador obtiene el nombre de la columna cuyo valor debe sacar. 
		String[] tramos = indParam.getNombre().split("\\.");
		String columna = tramos [tramos.length -1];		

	// 
		int c = 0;
		while (c < param.getIndicador().getResultado().length ) {
			if (columna.equals(param.getIndicador().getResultado()[c])) {
				valParam = linea[c];
				c = param.getIndicador().getResultado().length + 5;
			}
			c++;
		}		
		return valParam;
	}*/
	
}