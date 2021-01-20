package com.atalaya.evaluador;
import com.example.demo.Indicador;

public class Operando {
	private String nombre;
	private int tipo;
	private int tipoValor;
	private boolean negado;
	private Object resultado;
	private boolean ejecutado;
	private Indicador indicador;
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
		return resultado;
	}
	public void setResultado(Object resultado) {
		this.resultado = resultado;			
	}
	
	public Indicador getIndicador() {		
		return indicador;
	}
	
	public void setIndicador(Indicador indicador) {
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
		
		if (!this.ejecutado) {
			if (this.tipo == constantes.tpIndicador) {
				if (this.indicador.ejecutar() == 0) {
					this.resultado = this.indicador.getResultadoEjecucion();	
					if (this.nombre.toUpperCase().endsWith("ROWCOUNT")) {
						this.resultado = this.indicador.getResultadoEjecucion().size();
					}
				} else {
					this.resultado = null;
				}				
			} else {
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
	
}
