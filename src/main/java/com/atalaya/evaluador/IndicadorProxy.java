package main.java.com.atalaya.evaluador;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Vector;

import main.java.com.modelodatos.Indicador;

public class IndicadorProxy {
	
	private Vector<Object[]> resultadoEjecucion;
	private boolean flag;
	private Indicador indicador;

	public IndicadorProxy(Indicador ind)
	{
		indicador = ind;
		flag = false;
		resultadoEjecucion = new Vector<Object[]>();
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
	
	public Indicador getIndicador() {
		return indicador;
	}

	public void setIndicador(Indicador indicador) {
		this.indicador = indicador;
	}
	
	public int ejecutar() {
		
		if (!this.isFlag())
		{
			ResultSet rs = null;
	
			if (this.indicador.getTipo().equalsIgnoreCase("Query")) 
			{
				Connection conexion = null;
				
				try {
	
					conexion = DriverManager.getConnection(
							//"jdbc:mysql://localhost:3306/alumnadodb?useServerPrepStmts=true&useSSL=false&allowPublicKeyRetrieval=true",
							"jdbc:mysql://alumnadodb:3306/alumnadodb?useServerPrepStmts=true&useSSL=false&allowPublicKeyRetrieval=true",
							"root", "atalaya"); 
	 
					PreparedStatement pstmt = conexion.prepareStatement(this.indicador.getComando());
					for(int i = 0; i< this.indicador.getParametros().size(); i++) {
						if(this.indicador.getParametros().get(i).getTipo().equalsIgnoreCase("String")) {
							pstmt.setString(i+1, this.indicador.getParametros().get(i).getValor());
						}
						else if(this.indicador.getParametros().get(i).getTipo().equalsIgnoreCase("Entero")) {
							pstmt.setInt(i+1,Integer.parseInt(this.indicador.getParametros().get(i).getValor()));
						}
						else if(this.indicador.getParametros().get(i).getTipo().equalsIgnoreCase("BigDecimal")) {
							pstmt.setBigDecimal(i+1,new BigDecimal(this.indicador.getParametros().get(i).getValor()));
						}
						else if(this.indicador.getParametros().get(i).getTipo().equalsIgnoreCase("Date")) {						
							pstmt.setString(i+1, this.indicador.getParametros().get(i).getValor());
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
	
					if (this.indicador.getResultado() == null ||  this.indicador.getResultado().length == 0)
					{
						num_columnas_extraccion = num_columnas;
						this.indicador.setResultado(new String[num_columnas]);
						vacio = true;
					}
					else
					{
						num_columnas_extraccion = this.indicador.getResultado().length;
					}
	
					int[] column_types = new int[num_columnas];
					
					for(int i=0; i<num_columnas_extraccion; i++)
					{
						if(vacio) {
							try
							{
								String column_name = metadata.getColumnName(i+1);
								this.indicador.getResultado()[i]=column_name;
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
								if (column_name.equalsIgnoreCase(this.indicador.getResultado()[i]))
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
							if (this.indicador.getResultado()[i] == null)
							{
								value=null;
								continue;
							}
							
							if (column_types[i] == Types.TIMESTAMP)
							{
								try
								{
									value = new Timestamp(rs.getTimestamp(this.indicador.getResultado()[i]).getTime());
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
									value  = rs.getInt(this.indicador.getResultado()[i]);
	
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
									value  = rs.getString(this.indicador.getResultado()[i]);
	
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
									value = rs.getObject(this.indicador.getResultado()[i]);
								} catch (SQLException e)
								{
	
									value = null;
								}
							}
							row[i] = value;
						}
						this.resultadoEjecucion.add(row);
						this.setFlag(true);
					}
	
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally
				{
					try {
						conexion.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		return 0;

	}

	public boolean validar() {
		boolean valido = false;
		
		if(this.indicador.getNombre().equals("")) {
			valido = false;
			// Falta nombre
		}
		else if(this.indicador.getDescripcion().equals("")) {
			valido = false;
			// Falta descripcion
		}
		else if(this.indicador.getFuente().equals("")) {
			valido = false;
			// Falta descripcion
		}
		else if(this.indicador.getTipo().equals("")) {
			valido = false;
			// Falta descripcion
		}
		else if(this.indicador.getComando().equals("")) {
			valido = false;
			// Falta descripcion
		}
		else if(this.indicador.getResultado() == null) {
			valido = false;
			// Falta descripcion
		}
		else {
		for (int i = 0; i<this.indicador.getParametros().size(); i++) {
			valido = this.indicador.getParametros().get(i).validar();
			if(valido == false) {
				return valido;
				// un criterio no es valido
			}
		}
		
			valido = true;
		}		
		
		return valido;
	}
}
