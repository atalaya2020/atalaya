package com.atalaya.evaluador;

import java.util.ArrayList;

import com.example.demo.Indicador;
import com.example.demo.Parametro;
public class IniciaEvaluacion {
	
	public static void main(String args[]) {
		Indicador indicar;		
		
		ArrayList<Parametro> parametros = new ArrayList<Parametro>();
		ArrayList<Indicador> indicadores = new ArrayList<Indicador>();
		String resultado [] = new String [] {};	
		
		for (int i = 0; i < 11;i++) {			
			indicar = new Indicador("Indicador" + i, "", "", "" ,"", parametros, resultado, false);
			indicadores.add(i, indicar);
		}
		
//		Evaluador clase = new Evaluador("Indicador1=Indicador2 and Indicador3.rowcount= Indicador4 AND (Indicador1 > Indicador3 OR NOT (Indicador3 = Indicador7 AND Indicador2 < Indicador4)) AND (Indicador5 = Indicador6 OR Indicador7 < Indicador8) AND (Indicador9 >= ((6+Indicador4*0,6)/7) AND NOT Indicador10)", indicadores);
//		Evaluador clase = new Evaluador("Indicador1=Indicador2 and Indicador4 = (Indicador5 and Indicador6)");
		Evaluador clase = new Evaluador("(Indicador1.ROWCOUNT > 10 AND Indicador3 > 0)", indicadores);
		clase.setIndicadores(indicadores);
		
		clase.evaluar();
	}
}