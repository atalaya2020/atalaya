package com.atalaya.evaluador;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.modelodatos.Indicador;
import com.modelodatos.Parametro;


public class IndicadorProxy implements IIndicadorProxy, Runnable  {
	
	private static Logger log = null;
	
	private Vector<Object[]> resultadoEjecucion;
	private volatile String estado;	//Almacena el estado en el que se encuentra el indicador
	private long crono;
	private int indice;

	private boolean autoGenerado = false;
	
	private Indicador indicador;
	public static Hashtable<String,IndicadorProxy> indicadoresProxy;
	private String nombreIndicadorPadre = null; //nombre del indicador donde queremos alamcenar el resultado de este indicador, solo utilizado por los indicadores autogenerados

	public IndicadorProxy(Indicador ind)
	{
		log = LoggerFactory.getLogger(IndicadorProxy.class);
		
		resultadoEjecucion = new Vector<Object[]>();
		estado = IIndicadorProxy.ESTADO_NOEJEUCTADO;
		crono = Calendar.getInstance().getTimeInMillis();
		indice = -1;
		
		indicador = ind;
		if (indicadoresProxy==null)
			indicadoresProxy = new Hashtable<String,IndicadorProxy>();
		
		indicadoresProxy.put(ind.getNombre(), this);
	}
	
	//Este constructor crea objetos IndicadorProxy no almacenados en el atributo de clase indicadoresProxy, no son visibles por el resto de indicadores
	public IndicadorProxy(Indicador ind, boolean autoGenerado, String nombreIndicadorPadre)
	{
		log = LoggerFactory.getLogger(IndicadorProxy.class);
		
		resultadoEjecucion = new Vector<Object[]>();
		estado = IIndicadorProxy.ESTADO_NOEJEUCTADO;
		crono = Calendar.getInstance().getTimeInMillis();
		indice = 0;
		
		indicador = ind;
		
		this.autoGenerado = autoGenerado;
		this.nombreIndicadorPadre = nombreIndicadorPadre;
	}
	
	public Vector<Object[]> getResultadoEjecucion() {
		return resultadoEjecucion;
	}
	public void setResultadoEjecucion(Vector<Object[]> resultadoEjecucion) {
		this.resultadoEjecucion = resultadoEjecucion;
	}
	
	public Indicador getIndicador() {
		return indicador;
	}

	public void setIndicador(Indicador indicador) {
		this.indicador = indicador;
	}
	
	public synchronized String getEstado() {
		return this.estado;
	}

	public synchronized void setEstado(String estado) {
		this.estado = estado;
	}
	
	public static Hashtable<String, IndicadorProxy> getIndicadoresProxy() {
		return indicadoresProxy;
	}
	
	public void setIndice(int indice) {
		this.indice = indice;
	}
	
	public int getIndice() {
		return this.indice;
	}
	
	public boolean noejecutado() {
		boolean noejecutado = false;
		if (this.getEstado().equals(IIndicadorProxy.ESTADO_NOEJEUCTADO))
			noejecutado=true;
		
		return noejecutado;
	}
	
	public boolean ejecutado() {
		boolean ejecutado = false;
		if (this.getEstado().equals(IIndicadorProxy.ESTADO_EJECUTADO))
			ejecutado=true;
		
		return ejecutado;
	}
	
	public boolean ejecutando() {
		boolean ejecutando = false;
		if (this.getEstado().equals(IIndicadorProxy.ESTADO_EJECUTANDO))
			ejecutando=true;
		
		return ejecutando;
	}
	
	public boolean isAutoGenerado() {
		return autoGenerado;
	}

	public void setAutoGenerado(boolean autoGenerado) {
		this.autoGenerado = autoGenerado;
	}
	
	public String getNombreIndicadorPadre() {
		return nombreIndicadorPadre;
	}

	public void setNombreIndicadorPadre(String nombreIndicadorPadre) {
		this.nombreIndicadorPadre = nombreIndicadorPadre;
	}
	
	public void run()
	{
		ejecutar();
	}
	
	public boolean ejecutar() {
		
		if (this.noejecutado())
		{
			this.setEstado(IIndicadorProxy.ESTADO_EJECUTANDO);
			
			if (this.getIndicador().getTipo().equals(IIndicadorProxyType.tipo_bucle)
				|| this.getIndicador().getTipo().equals(IIndicadorProxyType.tipo_ws))
			{
				IIndicadorProxyType indicadorProxyType = null;
				
				try 
				{
					indicadorProxyType = IndicadorProxyType.getInstanceByAliasType(this.getIndicador().getTipo());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try 
				{
					indicadorProxyType.ejecutar(this);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else
			{
				ResultSet rs = null;
		
				if (this.indicador.getTipo().equalsIgnoreCase("Query")) 
				{
					Connection conexion = null;
					
					try {
		
						conexion = DriverManager.getConnection(
								"jdbc:mysql://localhost:3306/alumnadodb?useServerPrepStmts=true&useSSL=false&allowPublicKeyRetrieval=true",
								//"jdbc:mysql://alumnadodb:3306/alumnadodb?useServerPrepStmts=true&useSSL=false&allowPublicKeyRetrieval=true",
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
							return false;
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
										return false;
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
										return false;
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
										return false;
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
						}
						
						this.setEstado(IIndicadorProxy.ESTADO_EJECUTADO);
		
					} catch (Exception e) {
						e.printStackTrace();
						this.setEstado(IIndicadorProxy.ESTADO_EJECUTADO);
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
			
			//Si se trata de un indice autogenerado, por un bucle, tenemos que incorporar a cada uno de los resultados obtenidos del indicador_a el resultado del indicador_b
			if (this.autoGenerado && this.getIndice()>-1)
			{
				Object[] linea = null;
				Object[] linea_a = IndicadorProxy.getIndicadoresProxy().get(this.getNombreIndicadorPadre()).getResultadoEjecucion().elementAt(this.getIndice());
				
				if (this.getResultadoEjecucion()!=null && this.getResultadoEjecucion().size()>0)
				{
					Object[] linea_b = this.getResultadoEjecucion().elementAt(0);
						
					linea = new Object[linea_a.length+linea_b.length];
					for (int i=0;i<linea_a.length;i++)
						linea[i] = linea_a[i];
						
					for (int i=0;i<linea_b.length;i++)
						linea[linea_a.length+i] = linea_b[i];
				}
				else
					//AGUJERO se deberia crear un objeto con la longitud de campos correspondiente al numero de elementos que el atributo resultado del indicador_b
					linea = linea_a;
				
				IndicadorProxy.getIndicadoresProxy().get(this.getNombreIndicadorPadre()).getResultadoEjecucion().setElementAt(linea, this.getIndice());
			}
			
			this.setEstado(ESTADO_EJECUTADO);
		}
		
		return true;

	}
	
	public void parametrosIndicador() {
		
		for (int p = 0; p < this.getIndicador().getParametros().size(); p++) {			
			if (this.getIndicador().getParametros().get(p).getValor().startsWith(Comunes.tpMarcaIndicador)) {
				
				String[] tramos = new String [] {};
				tramos = this.getIndicador().getParametros().get(p).getValor().split("\\.");
				String nombreIndicador = tramos[0].substring(1);
				
				if (indicadoresProxy.containsKey(nombreIndicador)) 
				{
					if (indicadoresProxy.get(nombreIndicador).ejecutar() && (indicadoresProxy.get(nombreIndicador).getResultadoEjecucion()!=null) && indicadoresProxy.get(nombreIndicador).getResultadoEjecucion().size()>0) 
					{					
						String columna = tramos[1];
						Object valParam = new Object();
						Object[] linea = indicadoresProxy.get(nombreIndicador).getResultadoEjecucion().elementAt(0);
						int c = 0;
						while (c < indicadoresProxy.get(nombreIndicador).getIndicador().getResultado().length ) {
							if (columna.equals(indicadoresProxy.get(nombreIndicador).getIndicador().getResultado()[c])) {
								valParam = linea[c];
								break;
							}
							c++;
						}
						this.getIndicador().getParametros().get(p).setValor(valParam.toString());					
					}
				}
			}
		}
	}
	
	public boolean esDependiente()
	{
		boolean esDependiente = false;
		System.out.println("Valido si el indicador depende de otro indicador "+ this.getIndicador().getNombre());
		
		//Recorro los parametros del indicador para averiguar si todos su parametros estan interpretados o no
		for (int p = 0; p < this.getIndicador().getParametros().size(); p++) 
		{			
			System.out.println("parametro "+ p + ":" + this.getIndicador().getParametros().get(p).getValor());
			
			//Identifico aquellos parametros tipo Indicador, comienzan por #
			if (this.getIndicador().getParametros().get(p).getValor().startsWith(Comunes.tpMarcaIndicador)) 
			{
				String[] tramos = new String [] {};
				tramos = this.getIndicador().getParametros().get(p).getValor().split("\\.");
				String nombreIndicador = tramos[0].substring(1);
				
				//Busco en la lista de indicadores
				if (IndicadorProxy.getIndicadoresProxy().containsKey(nombreIndicador))
				{
					if (IndicadorProxy.getIndicadoresProxy().get(nombreIndicador).noejecutado() || IndicadorProxy.getIndicadoresProxy().get(nombreIndicador).ejecutando())
					{
						esDependiente = true;
						System.out.println("ES DEPENDIENTE . El parametro " + this.getIndicador().getParametros().get(p).getNombre() + " apunta al indicador " + nombreIndicador + " y tiene estado: " + IndicadorProxy.getIndicadoresProxy().get(nombreIndicador).getEstado());
						break;
					}
					else
						System.out.println("NO ES DEPENDIENTE . El parametro " + this.getIndicador().getParametros().get(p).getNombre() + " apunta al indicador " + nombreIndicador + " y tiene estado: " + IndicadorProxy.getIndicadoresProxy().get(nombreIndicador).getEstado());
				}
			}
		}
		
		return esDependiente;
	}
	
	//Este metodo recopila en una Lista todos los objetos IndicadorProxy asociados al IndicadorProxy pasado como parametro
	public List<String> ObtenerIndicadorAsociado(List<String> listaInd) {
		
		if (listaInd==null)
			listaInd = new ArrayList<String>();
		
		//Recorro los parametros del indicador
		for (int p = 0; p < this.getIndicador().getParametros().size(); p++) 
		{			
			//Identifico aquellos parametros tipo Indicador, comienzan por #
			if (this.getIndicador().getParametros().get(p).getValor().startsWith(Comunes.tpMarcaIndicador)) 
			{
				
				String[] tramos = null;
				tramos = this.getIndicador().getParametros().get(p).getValor().split("\\.");
				
				String nombreIndicador = tramos[0].substring(1);
				//Busco en la lista de indicadores
				if ((nombreIndicador.equals("CURSOR") || IndicadorProxy.getIndicadoresProxy().containsKey(nombreIndicador)) && !listaInd.contains(nombreIndicador)) 
				{
					listaInd.add(nombreIndicador);
					//Llamamos recursivamente a este metodo hasta llegar al nivel de profundidad necesario y almacenando en un lista todos aquellos indicadores que esten asociados al indicador inicial
					IndicadorProxy.getIndicadoresProxy().get(nombreIndicador).ObtenerIndicadorAsociado(listaInd);
				}
			}
		}
		
		return listaInd;
	}
	
	public String volcado (String modo)
	{
		StringBuffer volcado = new StringBuffer();
		
		if (this.getResultadoEjecucion()!=null && this.getResultadoEjecucion().size()>0)
		{
			if (modo.equals("html"))
				volcado.append("</br>");
			
			Object[] linea = null;
			for (int i=0;i<this.getResultadoEjecucion().size();i++)
			{
				linea = this.getResultadoEjecucion().get(i);
				
				if (linea!=null && linea.length>0)
				{
					if (modo.equals("html"))
						volcado.append("<br>");
					
					for (int j=0; j<linea.length;j++)
					{
						volcado.append(linea[j]);
						volcado.append("|");
					}
					
					if (modo.equals("html"))
						volcado.append("</br>");
					else
						volcado.append("\\n");
				}
			}
		}
		else 
			volcado = volcado.append("");
		
		return volcado.toString();
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
