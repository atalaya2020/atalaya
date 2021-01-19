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
		
		Indicador ind = new Indicador("DNI mayor 0", "Personas con DNI mayor a 1", "Mysql", "Query", query, array, resultado , false);
		Object[] rs = ind.ejecutar();

				for(int i = 0; i< rs.length; i++) {
				System.out.println(rs[i].toString());
				}
			
	}
}


