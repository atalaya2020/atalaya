package com.example.demo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class main {

	public static void main(String[] args) {
		String query = "select id,  nombre, apellidos FROM my_prueba.alumnos WHERE id > ?" ;
		String[] resultado = new String[]{"nombre", "apellidos"};
		
		ArrayList<Parametro> array= new ArrayList<Parametro>();
		Parametro param = new Parametro("numero", "entero", "0");
		array.add(param);
		
		Indicador ind = new Indicador("DNI mayor 0", "Personas con DNI mayor a 0", "Mysql", "Query", query, array, resultado , false);
		int ok = ind.ejecutar();
		if(ok == 0) {
			for(int i = 0; i< ind.getResultadoEjecucion().size(); i++) {
			for(int j = 0; j< ind.getResultadoEjecucion().size(); j++)	{
			try
			{
				System.out.println(ind.getResultadoEjecucion().elementAt(i)[j]);
			}
			catch (NullPointerException e)
			{
				System.out.println("triste");
			}
			}
				
				
				}
		}
			
	}
}


