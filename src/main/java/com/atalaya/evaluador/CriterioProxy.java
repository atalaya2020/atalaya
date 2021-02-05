package com.atalaya.evaluador;

import com.modelodatos.Criterio;

public class CriterioProxy {

	Criterio criterio;

	public CriterioProxy(Criterio criterio2) {
		this.criterio = criterio2;
	}

	public void setCriterio(Criterio criterio)  {
		this.criterio = criterio;
	}
	
	public Criterio getCriterio()  {
		return this.criterio;
	}	

}
