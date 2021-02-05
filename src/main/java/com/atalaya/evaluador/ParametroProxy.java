package com.atalaya.evaluador;

import com.modelodatos.Parametro;

public class ParametroProxy {

	Parametro parametro;
	
	public void setParametro(Parametro parametro)  {
		this.parametro = parametro;
	}
	
	public Parametro getParametro()  {
		return this.parametro;
	}
	
	public void ParametroProxy(Parametro parametro) {
		this.parametro = parametro;		
	}
	
	public void ParametroProxi(String nombre, String tipo, String valor) {
		Parametro parametro = new Parametro(nombre, tipo, valor);
	}
}
