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
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Vector;

public class Indicador {

	private String nombre;
	private String descripcion;
	private String fuente;
	private String tipo;
	private String comando;
	private ArrayList<Parametro> parametros;
	private String[] resultado;
	private Vector<Object[]> resultadoEjecucion;
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
		this.resultadoEjecucion = new Vector<Object[]>();
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
	public Vector<Object[]> getResultadoEjecucion() {
		return resultadoEjecucion;
	}
	public void setResultadoEjecucion(Vector<Object[]> resultadoEjecucion) {
		this.resultadoEjecucion = resultadoEjecucion;
	}

	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public int ejecutar() {
		ResultSet rs = null;

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

				try
				{
					metadata = rs.getMetaData();


				} catch (SQLException e)
				{
					return -1;
				}

				int num_columnas_extraccion;

				boolean vacio = false;

				int num_columnas = metadata.getColumnCount();

				if (this.resultado == null ||  this.resultado.length == 0)
				{
					num_columnas_extraccion = num_columnas;
					this.resultado = new String[num_columnas];
					vacio = true;
				}
				else
				{
					num_columnas_extraccion = this.resultado.length;
				}

				int[] column_types = new int[num_columnas];
				
				for(int i=0; i<num_columnas_extraccion; i++)
				{
					if(vacio) {
						try
						{
							String column_name = metadata.getColumnName(i+1);
							this.resultado[i]=column_name;
							column_types[i] = metadata.getColumnType(i+1);
						}
						catch(SQLException e)
						{
						}
					}
					else {

						boolean encontrado = false;
						for (int j=0;j<num_columnas;j++)
						{
							String column_name = metadata.getColumnName(j+1);
							int coltype = metadata.getColumnType(j+1);
							if (column_name.equalsIgnoreCase(this.resultado[i]))
							{
								column_types[i] = coltype;
								encontrado = true;
								break;
							}
						}
					}
				}
				while(rs.next())
				{
					Object[] row = new Object[num_columnas_extraccion];
					for(int i=0;i<num_columnas_extraccion;i++)
					{
						Object value = new Object();
						if (this.resultado[i] == null)
						{
							value=null;
							continue;
						}
						
						if (column_types[i] == Types.TIMESTAMP)
						{
							try
							{
								value = new Timestamp(rs.getTimestamp(this.resultado[i]).getTime());
							}
							catch (NullPointerException e)
							{
								value = null;
								
							} catch (SQLException e)
							{
								value = null;
								return -1;
							}
						}
						
						else if (column_types[i] == Types.INTEGER)
						{
							try
							{
								value  = rs.getInt(this.resultado[i]);

							}
							catch (NullPointerException e)
							{
								value = null;
							} catch (SQLException e) {

								value = null;
								return -1;
							}
						}


						else if (column_types[i] == Types.VARCHAR) {

							try
							{
								value  = rs.getString(this.resultado[i]);

							}
							catch (NullPointerException e)
							{
								value = null;
							} catch (SQLException e) {

								value = null;
								return -1;
							}

						}
						else {
							try
							{
								value = rs.getObject(this.resultado[i]);
							} catch (SQLException e)
							{

								value = null;
							}
						}
						row[i] = value;
					}
					this.resultadoEjecucion.add(row);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}


		else {

		}
		return 0;

	}

}
