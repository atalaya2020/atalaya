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
	
	public ParametroProxy(Parametro parametro) {
		this.parametro = parametro;		
	}
}
