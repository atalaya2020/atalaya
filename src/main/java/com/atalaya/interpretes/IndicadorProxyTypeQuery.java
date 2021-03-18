package com.atalaya.interpretes;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Hashtable;
import java.util.Vector;
import com.modelodatos.Indicador;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class IndicadorProxyTypeQuery implements IIndicadorProxyType {
	
	private static IndicadorProxyTypeQuery instance;
	
	private int numFilasParaLeer = 1000;
	
	private static Hashtable<String,HikariDataSource>almacenPool; 
	//statement.setFetchSize(Integer.MIN_VALUE);
	
	/** 
	 * Constructor.
	 */
	protected IndicadorProxyTypeQuery(){}
	

	public static IndicadorProxyTypeQuery getInstance()
	{
		//Si el atributo instance no tiene valor significa que no existe ninguna instancia de la clase
		if (instance==null)
		{
			//Da valor al atributo instance, creando una instancia de la clase
			instance = new IndicadorProxyTypeQuery();
		}
		//Devuelve la instancia unica de la clase AliasTypeLoop
		return instance;
	}
	
	public boolean ejecutar(IndicadorProxy indicador) {
		
		boolean exec = false;
		
		try
		{			
			HikariDataSource pool = null;
			String nombreFuente = indicador.getIndicador().getFuente();
			
			
			if (almacenPool==null || (almacenPool!=null && almacenPool.size()==0))
				almacenPool = new Hashtable<String,HikariDataSource>();
			
			if (almacenPool.containsKey(nombreFuente))
				pool = almacenPool.get(nombreFuente);
			
			
			if (!almacenPool.containsKey(nombreFuente))
			{
				HikariConfig config = new HikariConfig();
				config.setJdbcUrl(Ejecutable.getConfFuentes().getProperty(nombreFuente+"."+IIndicadorProxyType.propiedad_url));
				config.setUsername(Ejecutable.getConfFuentes().getProperty(nombreFuente+"."+IIndicadorProxyType.propiedad_user));
				config.setPassword(Ejecutable.getConfFuentes().getProperty(nombreFuente+"."+IIndicadorProxyType.propiedad_password));
				config.addDataSourceProperty("cachePrepStmts",Ejecutable.getConfFuentes().getProperty(nombreFuente+"."+IIndicadorProxyType.propiedad_cachePrepStmts));
				config.addDataSourceProperty("prepStmtCacheSize",Ejecutable.getConfFuentes().getProperty(nombreFuente+"."+IIndicadorProxyType.propiedad_prepStmtCacheSize));
				config.addDataSourceProperty("prepStmtCacheSqlLimit",Ejecutable.getConfFuentes().getProperty(nombreFuente+"."+IIndicadorProxyType.propiedad_prepStmtCacheSqlLimit));
				config.addDataSourceProperty("maximum-pool-size", Ejecutable.getConfFuentes().getProperty(nombreFuente+"."+IIndicadorProxyType.propiedad_maximumpoolsize));
	
				pool = new HikariDataSource(config);
				almacenPool.put(indicador.getIndicador().getFuente(), pool);
			}
			
			if (pool!=null)
			{
				Connection conexion = null;
				ResultSet rs = null;
				
				try
				{
					String sComando = indicador.getIndicador().getComando();
				
					conexion = pool.getConnection();
					
					Indicador def_indicador = indicador.getIndicador();
					
					PreparedStatement pstmt = conexion.prepareStatement(sComando);
					pstmt.setFetchSize(numFilasParaLeer);
					
					for(int i = 0; i< def_indicador.getParametros().size(); i++) {
						if(def_indicador.getParametros().get(i).getTipo().equalsIgnoreCase("String")) {
							pstmt.setString(i+1, def_indicador.getParametros().get(i).getValor());
						}
						else if(def_indicador.getParametros().get(i).getTipo().equalsIgnoreCase("Entero")) {
							pstmt.setInt(i+1,Integer.parseInt(def_indicador.getParametros().get(i).getValor()));
						}
						else if(def_indicador.getParametros().get(i).getTipo().equalsIgnoreCase("BigDecimal")) {
							pstmt.setBigDecimal(i+1,new BigDecimal(def_indicador.getParametros().get(i).getValor()));
						}
						else if(def_indicador.getParametros().get(i).getTipo().equalsIgnoreCase("Date")) {						
							pstmt.setString(i+1, def_indicador.getParametros().get(i).getValor());
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
						return false;
					}
	
					int num_columnas_extraccion;
	
					boolean vacio = false;
	
					int num_columnas = metadata.getColumnCount();
	
					if (def_indicador.getResultado() == null || def_indicador.getResultado().length == 0)
					{
						num_columnas_extraccion = num_columnas;
						def_indicador.setResultado(new String[num_columnas]);
						vacio = true;
					}
					else
					{
						num_columnas_extraccion = def_indicador.getResultado().length;
					}
	
					int[] column_types = new int[num_columnas];
					
					for(int i=0; i<num_columnas_extraccion; i++)
					{
						if(vacio) 
						{
							try
							{
								String column_name = metadata.getColumnName(i+1);
								def_indicador.getResultado()[i]=column_name;
								column_types[i] = metadata.getColumnType(i+1);
							}
							catch(SQLException e)
							{
							}
						}
						else {
	
							for (int j=0;j<num_columnas;j++)
							{
								String column_name = metadata.getColumnName(j+1);
								int coltype = metadata.getColumnType(j+1);
								if (column_name.equalsIgnoreCase(def_indicador.getResultado()[i]))
								{
									column_types[i] = coltype;
									break;
								}
							}
						}
					}
					
					Object[] row = null;
					while(rs.next())
					{
						row = new Object[num_columnas_extraccion];							
						for(int i=0;i<num_columnas_extraccion;i++)
						{
							Object value = new Object();
							if (def_indicador.getResultado()[i] == null)
							{
								value=null;
								continue;
							}
							
							if (column_types[i] == Types.TIMESTAMP)
							{
								try
								{
									value = new Timestamp(rs.getTimestamp(def_indicador.getResultado()[i]).getTime());
								}
								catch (NullPointerException e)
								{
									value = null;
									
								} catch (SQLException e)
								{
									value = null;
									return false;
								}
							}
							
							else if (column_types[i] == Types.INTEGER)
							{
								try
								{
									value  = rs.getInt(def_indicador.getResultado()[i]);
	
								}
								catch (NullPointerException e)
								{
									value = null;
								} catch (SQLException e) {
	
									value = null;
									return false;
								}
							}
	
	
							else if (column_types[i] == Types.VARCHAR) 
							{
								try
								{
									value  = rs.getString(def_indicador.getResultado()[i]);
	
								}
								catch (NullPointerException e)
								{
									value = null;
								} catch (SQLException e) {
	
									value = null;
									return false;
								}
							}
							else 
							{
								try
								{
									value = rs.getObject(def_indicador.getResultado()[i]);
								} catch (SQLException e)
								{
	
									value = null;
								}
							}
							
							row[i] = value;	
							//rs.deleteRow();
						}
						
						//añadimos las filas al resultado del indicador si llega al numero de filas que definen el bloque de volcado
						indicador.setResultadoEjecucion(row,false);
					}
					
					//añadimos las filas al resultado del indicador si no han sido añadidas por no llegar al numero de filas que definen el bloque de volcado
					indicador.setResultadoEjecucion(null,true);
					indicador.setEstado(Ejecutable.ESTADO_EJECUTADO);
					
					exec = true;
	
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					indicador.setEstado(Ejecutable.ESTADO_EJECUTADO);
					indicador.setDescripcionEstado(Ejecutable.FIN_OK_SIN_RESULTADO);
				}
				finally
				{
					if (rs!=null)
						rs.close();
					
					if (conexion!=null)
						conexion.close();
				}
			}
			else
			{
				indicador.setEstado(Ejecutable.ESTADO_EJECUTADO);
				indicador.setDescripcionEstado(Ejecutable.FIN_OK_SIN_RESULTADO);
			}
			
		}
		catch (Exception e)
		{
			indicador.setEstado(Ejecutable.ESTADO_EJECUTADO);
			indicador.setDescripcionEstado(Ejecutable.FIN_OK_SIN_RESULTADO);
		}
		
		return exec;
	}
}
