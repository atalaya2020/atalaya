package com.example.demo;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

public class Indicador {

	private String nombre;
	private String descripcion;
	private String fuente;
	private String tipo;
	private String comando;
	private ArrayList<Parametro> parametros;
	private String[] resultado;
	private boolean flag;


	public Indicador(String nombre, String descripcion, String fuente, String tipo, String comando,
			ArrayList<Parametro> parametros, String[] resultado, boolean flag) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.fuente = fuente;
		this.tipo = tipo;
		this.comando = comando;
		this.parametros = parametros;
		this.resultado = resultado;
		this.flag = flag;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getFuente() {
		return fuente;
	}
	public void setFuente(String fuente) {
		this.fuente = fuente;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getComando() {
		return comando;
	}
	public void setComando(String comando) {
		this.comando = comando;
	}
	public ArrayList<Parametro> getParametros() {
		return parametros;
	}
	public void setParametros(ArrayList<Parametro> parametros) {
		this.parametros = parametros;
	}
	public Object getResultado() {
		return resultado;
	}
	public void setResultado(String[] resultado) {
		this.resultado = resultado;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public Object[] ejecutar() {
		ResultSet rs = null;
		Object[] salida = new Object[this.resultado.length];
		if (this.tipo.equalsIgnoreCase("Query")) {

			try {

				Connection conexion = DriverManager.getConnection(
						"jdbc:mysql://127.0.0.1:3306/my_prueba?useServerPrepStmts=true",
						"root", "amdocs");

				PreparedStatement pstmt = conexion.prepareStatement(this.comando);


				for(int i = 0; i< this.parametros.size(); i++) {
					if(this.parametros.get(i).getTipo().equalsIgnoreCase("String")) {
						pstmt.setString(i+1, this.parametros.get(i).getValor());
					}
					else if(this.parametros.get(i).getTipo().equalsIgnoreCase("Entero")) {
						pstmt.setInt(i+1,Integer.parseInt(this.parametros.get(i).getValor()));
					}
					else if(this.parametros.get(i).getTipo().equalsIgnoreCase("BigDecimal")) {
						pstmt.setBigDecimal(i+1,new BigDecimal(this.parametros.get(i).getValor()));
					}
					else if(this.parametros.get(i).getTipo().equalsIgnoreCase("Date")) {						
						pstmt.setString(i+1, this.parametros.get(i).getValor());
					}
					else {
						System.out.println("Introduce un tipo válido");
					}
				}

				rs = pstmt.executeQuery();

				ResultSetMetaData metadata=null;
				int num_columnas;

				try
				{
					metadata = rs.getMetaData();


				} catch (SQLException e)
				{
					return null;
				}

				try
				{
					num_columnas = metadata.getColumnCount();

				} catch (SQLException e)
				{
					return null;
				}

				//int[] column_types = new int[num_columnas];

				for(int i=0; i<this.resultado.length; i++)
				{
					Object value = new Object();

					if (this.resultado[i] == null)
					{
						salida[i]=null;
						continue;
					};


					//else if (column_types[i] == Types.VARCHAR)

					try
					{
						value  = rs.getString(this.resultado[i]);

					}
					catch (NullPointerException e)
					{
						value = null;
					} catch (SQLException e) {

						value = null;
					}

//					else if (column_types[i] == Types.VARCHAR)
//					
//					try
//					{
//						value = rs.getObject(this.resultado[i]);
//					} catch (SQLException e)
//					{
//						value = null;
//					}

					salida[i]=value;
				}


			} catch (Exception e) {
				e.printStackTrace();
			}

		}


		else {

		}
		return salida;

	}

}
