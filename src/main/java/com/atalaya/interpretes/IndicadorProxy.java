package com.atalaya.interpretes;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import com.atalaya.evaluador.Comunes;
import com.modelodatos.Indicador;

/** 
 * Clase encargada de interpretar una entidad de tipo Indicador. Es utilizado para obtener informacion de 
 * diversos tipos de fuentes (base de datos, servicios web, almacen de ficheros ...) en base a los valores de los atributos tipo y comando.
 */
public class IndicadorProxy extends Ejecutable implements Runnable  {
	
	private Indicador indicador;
	private long analisis;
	
	
	private int indice; 												//Exclusivo de indicadores bucle, define para un indicador hijo (B) con que posicion del indicador padre (A) está relacionado
	private int countRecorridos;										//Exclusivo de indicadores bucle, define para un indicador bucle la posicion dentro de los resultados del indicador_a
	private volatile int countEjecutado;								//Exclusivo de indicadores bucle, define cuantos de los indicadores bucle pasado a estado EJECUTADO 
	protected ArrayList<IndicadorProxy> listaIndicadoresHijos = null; 	//Exclusivo de indicadores bucle, almacena el listado de indicadores hijos
	private boolean autoGenerado = false;								//Define si esta clase ha sido autogenerada
	private String nombreIndicadorPadre = null; 						//Define el nombre del indicador donde queremos alamcenar el resultado de este indicador, solo utilizado por los indicadores autogenerados 
	
	private Vector<Object[]> resultadoEjecucionTemp = null;				//Almacen temporal de resultados
	private int countResultados = 0;										//Define el numero total de filas obtenidas por el indicador
	private int countParticion = -1;											//Define el numero de particion recorrida

	public final String TIPO_QUERY = "Query"; 							//Este tipo de indicador define una query con bind variables que pueden tomar valores fijos o dinamicos
	public final String TIPO_BUCLE = "Bucle";							//Este tipo de alias define una relacion entre alias tipo para cada elemento del alias "a" aplica el alias "b" 
	public final String TIPO_WS = "Ws"; 								//Este tipo de indicador define un recurso tipo WebService
	
	String cabeceralog;													//Cabecera comun para el traceado de la clase

	/**
	 * Constructor de clase IndicadorProxy, en base al esquema de ejecucion definido en el objeto de base de datos Indicador
	 * Inicializa el estado de ejecucion a NOEJECUTADO, obtiene los parametros de configuracion, define su hashcode e inicializa el resto de indicadores 
	 * @param ind
	 */
	public IndicadorProxy(Indicador ind)
	{
		setEstado(ESTADO_NOEJEUCTADO);

		countRecorridos = -1;
		countEjecutado = 0;
		countResultados = 0;
		indicador = ind;
		log.info(cabeceralog + "Cargando configuracion");
		obtenerConfiguracion(indicador, "Threads");
		obtenerConfiguracion(indicador, "Tiempos");
		
		setHashCode(hashCode());
		cabeceralog = "Indicador " + ind.getNombre() + "|" + this.getHashCode() + ":";
	}

	
	/**
	 * Constructor de clase IndicadorProxy, en base al esquema de ejecucion definido en el objeto de base de datos Indicador. 
	 * Este constructor es utilizado para crear interpretes autogenerados, que no seran visibles por el resto de entidades Indicador 
	 * Inicializa el estado de ejecucion a NOEJECUTADO, obtiene los parametros de configuracion, define su hashcode e inicializa el resto de indicadores
	 * @param ind 
	 * @param autoGenerado
	 * @param nombreIndicadorPadre
	 */
	public IndicadorProxy(Indicador ind, boolean autoGenerado, String nombreIndicadorPadre)
	{
		setEstado(ESTADO_NOEJEUCTADO);
		
		indice = 0;
		indicador = ind;
		log.info(cabeceralog + "Cargando configuracion");
		obtenerConfiguracion(indicador, "Threads");
		obtenerConfiguracion(indicador, "Tiempos");
		
		this.autoGenerado = autoGenerado;
		this.nombreIndicadorPadre = nombreIndicadorPadre;

		setHashCode(hashCode());
		cabeceralog = "Indicador auto " + ind.getNombre() + "|" + this.getHashCode() + ":";
	}
	
	/**
	 * Metodo para recuperar el atributo de tipo Indicador, que contiene el esquema a ejecutar
	 */
	public Indicador getIndicador() {
		return indicador;
	}

	/**
	 * Metodo para definir el atributo de tipo Indicador, que contiene el esquema a ejecutar
	 * @param indicador
	 */
	public void setIndicador(Indicador indicador) {
		this.indicador = indicador;
	}
	
	/**
	 * Metodo para definir el atributo indice que es exclusivo de indicadores tipo bucle y define para un indicador hijo (B) con que posicion del indicador padre (A) está relacionado
	 * @param indice 
	 */
	public void setIndice(int indice) {
		this.indice = indice;
	}
	
	/**
	 * Metodo para recuperar el atributo indice que es exclusivo de indicadores tipo bucle y define para un indicador hijo (B) con que posicion del indicador padre (A) está relacionado
	 * @return 
	 */
	public int getIndice() {
		return this.indice;
	}
	
	/**
	 * Metodo para recuperar el atributo autogenerado que es exclusivo de indicadores tipo bucle y define si un objeto IndicadorProxy ha sido autogenerado
	 * @return
	 */
	public boolean isAutoGenerado() {
		return autoGenerado;
	}
	
	/**
	 * Metodo para definir el atributo autogenerado que es exlusivo de indicadores tipo bucle y define si un objeto IndicadorProxy es autogenerado
	 * @param autoGenerado
	 */
	public void setAutoGenerado(boolean autoGenerado) {
		this.autoGenerado = autoGenerado;
	}
	
	/**
	 * Metodo para recuperar el atributo indicadorPadre que es exclusivo de indicadores tipo bucle y define el nombre del indicador donde queremos almacenar el resultado de este indicador
	 * @return
	 */
	public String getNombreIndicadorPadre() {
		return nombreIndicadorPadre;
	}

	/**
	 * Metodo para definir el atributo indicadorPadre que es exclusivo de indicadores tipo bucle y define el nombre del indicador donde queremos almacenar el resultado de este indicador
	 * @return
	 */
	public void setNombreIndicadorPadre(String nombreIndicadorPadre) {
		this.nombreIndicadorPadre = nombreIndicadorPadre;
	}
	
	/**
	 * Metodo para recuperar el atributo countEjecutado que es exclusivo de indicadores tipo bucle y define el numero de indicadores hijos ejecutados
	 * @return
	 */
	public synchronized int getCountEjecutado() {
		return this.countEjecutado;
	}

	/**
	 * Metodo para incrementar en uno el atributo countEjecutado que es exclusivo de indicadores tipo bucle y define el numero de indicadores hijos ejecutados
	 */
	public synchronized void setCountEjecutado() {
		this.countEjecutado++;
	}
	
	/**
	 * Metodo para recuperar el atributo countRecorrido que es exclusivo de indicadores tipo bucle y define el numero de filas recorridas del indicador padre (A)
	 * @return
	 */
	public int getCountRecorridos() {
		return countRecorridos;
	}

	/**
	 * Metodo para incrementar en uno el atributo countRecorridos que es exclusivo de indicadores tipo bucle y define el numero de filas recorridas del indicador padre (A)
	 */
	public void setCountRecorridos() {
		this.countRecorridos++;
	}
	
	/**
	 * Metodo para recuperar el atributo resultadoEjecucionTemp utilizado como almacen temporal para aquellos indicadores que particionan sus resultados
	 * @return
	 */
	public Vector<Object[]> getResultadoEjecucionTemp() {
		return resultadoEjecucionTemp;
	}

	/**
	 * Metodo para definir el atributo resultadoEjecucionTemp utilizado como almacen temporal para aquellos indicadores que particionan sus resultados
	 * @param resultadoEjecucionTemp
	 */
	public void setResultadoEjecucionTemp(Vector<Object[]> resultadoEjecucionTemp) {
		this.resultadoEjecucionTemp = resultadoEjecucionTemp;
	}
	
	/**
	 * Metodo para recuperar el atributo countResultado utilizado para definir el numero total de filas obtenidas como resultado
	 * @return
	 */
	public int getCountResultados() {
		return countResultados;
	}

	/**
	 * Metodo para definir el atributo countResultado utilizado para definir el numero total de filas obtenidas como resultado
	 * @param incremento
	 */
	public void setCountResultados(int incremento) {
		this.countResultados = countResultados + incremento;
	}
	
	/**
	 * Metodo para recuperar el atributo countParticion utilizado para definir el numero total de particiones del resultado
	 * @return
	 */
	public int getCountParticion() {
		return countParticion;
	}

	/**
	 * Metodo para incrementar en uno el atributo countParticion utilizado para definir el numero total de particiones del resultado
	 */
	public void setCountParticion() {
		this.countParticion++;
	}
	
	/**
	 * Metodo para definir el hash del analisis al que pertenece el indicador
	 * @param analisis
	 */
	public void setAnalisis(long analisis) {
		this.analisis = analisis;
	}
	
	/**
	 * Metodo para recuperar el hash del analisis al que pertenece el indicador
	 * @return
	 */
	public long getAnalisis() {
		return this.analisis;
	}
	
	/**
	 * Metodo para obtener resultado de un indicador. El almacenamiento del resultado de ejecucion puede ser varios tipos: memoria,redis,fichero.
	 * @param desde define la particion o la linea desde la que queremos obtener resultado. Si tiene valor 0, se recupera la totalidad del resultado del indicador
	 * @return
	 */
	public Vector<Object[]> getResultadoEjecucion(int desde) {
		
		Vector<Object[]> vTemporal = null; 
		
		int numFilasParaVolcar = 500;
		String origen = this.getIndicador().getDestino()==null?"memoria":this.getIndicador().getDestino();
		
		if (origen.equals("memoria"))
			return super.getResultadoEjecucion();
		else if (origen.equals("redis"))
		{
			String redisClave = "RESULTADOS:"+getHashCode();
			try 
			{
				byte[] particion = Ejecutable.getRedis().lindex(redisClave.getBytes(),desde);
				//byte[] particion = Ejecutable.getRedis().lpop(redisClave.getBytes());//si no es persistente
				ByteArrayInputStream bInStream = new ByteArrayInputStream(particion);
			    ObjectInput in = new ObjectInputStream(bInStream);
			    vTemporal = (Vector<Object[]>)in.readObject();
				
			    bInStream.close();
				this.getResultadoEjecucionTemp().clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.setEstado(ESTADO_EJECUTADO);
				this.setDescripcionEstado(FIN_KO_VOLCADO);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				e.printStackTrace();
				this.setEstado(ESTADO_EJECUTADO);
				this.setDescripcionEstado(FIN_KO_VOLCADO);
			}
		}
		else if (origen.equals("fichero"))
		{
			String nombreFichero="C:\\Users\\0015814\\Desktop\\RESULTADOS:"+getHashCode();
			FileReader fichero = null;
			String sLinea;
			int nContador = 0;
			int nFila = 0;
			
			try {
				
				vTemporal = new Vector<Object[]>();
				fichero =  new FileReader(new File(nombreFichero));
				
				BufferedReader bReader = new BufferedReader(fichero);
				sLinea = bReader.readLine();
				
				Object[] arLinea = null;
				
				while (sLinea!=null && (nContador<numFilasParaVolcar))
				{
					if (nFila >= desde)
					{
						arLinea = sLinea.split("\\|");
						vTemporal.add(arLinea);
						sLinea = bReader.readLine();
						nContador++;
					}
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.setEstado(ESTADO_EJECUTADO);
				this.setDescripcionEstado(FIN_KO_VOLCADO);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.setEstado(ESTADO_EJECUTADO);
				this.setDescripcionEstado(FIN_KO_VOLCADO);
			}
		}
		
		return vTemporal;
		
	}
	
	/**
	 * Metodo para definir el resultado de un indicador. El almacenamiento del resultado de ejecucion puede ser varios tipos: memoria,redis,fichero.
	 * @param fila	contiene la informacion a volcar en el almacen de resultados
	 * @param bSinControlVolcado define si el volcado al almacen se hace de la totalidad true o de forma particionada false
	 */
	public synchronized void setResultadoEjecucion(Object[] fila, boolean bSinControlVolcado) {
		
		int numFilasParaVolcar = 500;
		String destino = this.getIndicador().getDestino()==null?"memoria":this.getIndicador().getDestino();
		
		if (this.getResultadoEjecucionTemp()==null)
			this.setResultadoEjecucionTemp(new Vector<Object[]>());
		
		if (fila!=null && fila.length>0)
			this.getResultadoEjecucionTemp().add(fila);
		
		if (this.getResultadoEjecucionTemp().size()>0)
		{
			if (bSinControlVolcado || (this.getResultadoEjecucionTemp().size()> 0 && this.getResultadoEjecucionTemp().size()%numFilasParaVolcar==0))
			{
				if (destino.equals("memoria"))
				{
					//Volcamos las filas que teniamos en el resultado temporal en el resultado definitivo
					while(this.getResultadoEjecucionTemp().size()>0)
					{
						this.getResultadoEjecucion().add(this.getResultadoEjecucionTemp().elementAt(0));
						this.getResultadoEjecucionTemp().remove(0);
						this.setCountResultados(1);
					}
				}
				else if (destino.equals("redis"))
				{
					this.setCountParticion();
					String redisClave = "RESULTADOS:"+getHashCode()+":"+this.getCountParticion();
					ByteArrayOutputStream bOutStream = new ByteArrayOutputStream();
				    ObjectOutputStream oOutStream;
					try {
						oOutStream = new ObjectOutputStream(bOutStream);
						oOutStream.writeObject(this.getResultadoEjecucionTemp());
					    oOutStream.close();
					    
					    Ejecutable.getRedis().rpush(redisClave.getBytes(), bOutStream.toByteArray());
						
						bOutStream.close();
						this.setCountResultados(this.getResultadoEjecucionTemp().size());
						this.getResultadoEjecucionTemp().clear();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						this.setEstado(ESTADO_EJECUTADO);
						this.setDescripcionEstado(FIN_KO_VOLCADO);
					}
				}
				else if (destino.equals("fichero"))
				{
					String nombreFichero = "";
					PrintWriter fichero = null; 
					
					this.setCountParticion();
					//nombreFichero="c://RESULTADOS:"+getHashCode();
					nombreFichero="C:\\Users\\0015814\\Desktop\\RESULTADOS:"+getHashCode()+":"+this.getCountParticion();
					try {
						
						fichero =  new PrintWriter(new File(nombreFichero));
						
						StringBuffer sfila = null;
						for (int i=0;i<this.getResultadoEjecucionTemp().size();i++)
						{
							Object[] filaTemp = this.getResultadoEjecucionTemp().elementAt(i);
							sfila = new StringBuffer();
							for (int j=0;j<filaTemp.length;j++)
							{
								sfila.append(filaTemp[j]);
								sfila.append("|");
							}
							fichero.println(sfila);
						}
						fichero.flush();
						this.setCountResultados(this.getResultadoEjecucionTemp().size());
						this.getResultadoEjecucionTemp().clear();
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						this.setEstado(ESTADO_EJECUTADO);
						this.setDescripcionEstado(FIN_KO_VOLCADO);
					}
					
				}
			}
			this.setDescripcionEstado(FIN_OK);
		}
		else
			this.setDescripcionEstado(FIN_OK_SIN_RESULTADO);
	}
	
	/**
	 * Metodo utilizado por el interfaz runnable
	 */
	public void run()
	{
		ejecutar();
	}
	
	/**
	 * Metodo encargado de interpretar el esquema definido por el objeto Indicador
	 */
	public boolean ejecutar() {
		
		if (this.noejecutado())
		{
			this.setEstado(ESTADO_EJECUTANDO);
			setCrono(Calendar.getInstance().getTimeInMillis());
			log.info(cabeceralog + "Ejecutando indicador...");
			log.info(cabeceralog + this.getIndicador().getDescripcion());
		
			//Mientras no haya finalizado su ejecucion debo controlar su interrupcion
			while (!this.ejecutado())
			{
				if (this.getIndicador().getTipo().equals(IIndicadorProxyType.tipo_bucle)
					|| this.getIndicador().getTipo().equals(IIndicadorProxyType.tipo_ws)
					|| this.getIndicador().getTipo().equals(IIndicadorProxyType.tipo_query))
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
				else if (this.getIndicador().getTipo().equals(IIndicadorProxyType.tipo_volcado))
				{
					String comando = this.getIndicador().getComando();
					String[] palabras = comando.split(" ");
					
					String nombreInd = palabras[1].substring(1);
					IndicadorProxy ind = super.getIndicadorNombre(this.getAnalisis(), nombreInd);					
//					IndicadorProxy ind = indicadores.get(this.getHashCode());	
					
					ind.volcadoResultado("trazas");
					this.setEstado(ESTADO_EJECUTADO);
					this.setDescripcionEstado(FIN_OK);
				}

				
				//Si se trata de un indice autogenerado, por un bucle, tenemos que incorporar a cada uno de los resultados obtenidos del indicador_a el resultado del indicador_b
				if (this.autoGenerado && this.getIndice()>-1)
				{
					Object[] linea = null;
//					Object[] linea_a = getIndicadores().get(this.getNombreIndicadorPadre()).getResultadoEjecucion().elementAt(this.getIndice());
					Object[] linea_a = super.getIndicadorNombre(this.getAnalisis(), this.getNombreIndicadorPadre()).getResultadoEjecucion().elementAt(this.getIndice());
					
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
					{
						//AGUJERO se deberia crear un objeto con la longitud de campos correspondiente al numero de elementos que el atributo resultado del indicador_b
						linea = new Object[linea_a.length+this.getIndicador().getResultado().length];
						for (int i=0;i<this.getIndicador().getResultado().length;i++)
							linea[linea_a.length+i] = "N/A";
					}
					
					log.info("Indicador auto finalizado: " + this.getIndicador().getNombre());
//					getIndicadores().get(this.getNombreIndicadorPadre()).getResultadoEjecucion().setElementAt(linea, this.getIndice());
//					getIndicadores().get(this.getNombreIndicadorPadre()).setCountEjecutado();
					//super.getIndicadorNombre(this.getAnalisis(), this.getNombreIndicadorPadre()).getResultadoEjecucion().setElementAt(linea, this.getIndice());
					super.getIndicadorNombre(this.getAnalisis(), this.getNombreIndicadorPadre()).setResultadoEjecucion(linea, true);
					super.getIndicadorNombre(this.getAnalisis(), this.getNombreIndicadorPadre()).setCountEjecutado();
					this.setDescripcionEstado(FIN_OK);
					//TAREA revisar si en este punto cuando damos por finalizado el hilo, tenemos que eliminar el indicador autogenerado o por el contrario se libera con la muerte del thread.
				}
				else
				{
					
				}
			}
			this.setEstado(ESTADO_EJECUTADO);
		}
		
		return true;

	}
	
	/**
	 * Metodo encargado de liberar los recursos asociados a un indicador
	 */
	public void detener()
	{
		super.detener();
		
		String destino = this.getIndicador().getDestino()==null?"memoria":this.getIndicador().getDestino();
		
		if (destino.equals("redis"))
		{
			String redisClave = "RESULTADOS:"+getHashCode()+":";
			for(int i=0;i<this.getCountParticion();i++)
			{
				super.getRedis().del((redisClave+i).getBytes());
			}
			
		}
		else if (destino.equals("fichero"))
		{
			String fichero = "RESULTADOS:"+getHashCode()+":";
			for(int i=0;i<this.getCountParticion();i++)
			{
				File file = new File(fichero+i);
				if (file.exists())
					file.delete();
			}
		}
			
		if (this.listaIndicadoresHijos!=null && this.listaIndicadoresHijos.size()>0)
		{
			for(int i=0;i<this.listaIndicadoresHijos.size();i++)
			{
				IndicadorProxy indHijo = this.listaIndicadoresHijos.get(i);
				//Buscamos los indicadores autogenerados hijos 
				indHijo.detener();
				log.info(cabeceralog+"Parado el indicador hijo:" +indHijo.getIndice()+ " por sobrepasar el tiempo maximo de ejecucion "+ tiempo_max);
			}
		}
	}
	
	/**
	 * Metodo encargado de asignar valor a los parametros de entrada de un indcador
	 */
	public void parametrosIndicador() {
		
		for (int p = 0; p < this.getIndicador().getParametros().size(); p++) {			
			if (this.getIndicador().getParametros().get(p).getValor().startsWith(Comunes.tpMarcaIndicador)) {
				
				String[] tramos = new String [] {};
				tramos = this.getIndicador().getParametros().get(p).getValor().split(Comunes.tpSeparador);
				String nombreIndicador = tramos[0].substring(1);
				
				IndicadorProxy indProxy = super.getIndicadorNombre(this.getAnalisis(), nombreIndicador);
				if (indProxy!=null) 
				{
//					if (getIndicadores().get(nombreIndicador).ejecutar() && (getIndicadores().get(nombreIndicador).getResultadoEjecucion()!=null) && getIndicadores().get(nombreIndicador).getResultadoEjecucion().size()>0)
					if (indProxy.ejecutar() && indProxy.getCountResultados() > 0) 
					{					
						String columna = tramos[1];
						Object valParam = new Object();
//						Object[] linea = getIndicadores().get(nombreIndicador).getResultadoEjecucion().elementAt(0);
						Vector<Object[]> vlinea = indProxy.getResultadoEjecucion(0);
						Object[] linea = vlinea.elementAt(0);
						int c = 0;
//						while (c < getIndicadores().get(nombreIndicador).getIndicador().getResultado().length ) {
//							if (columna.equals(getIndicadores().get(nombreIndicador).getIndicador().getResultado()[c])) {
						while (c < indProxy.getIndicador().getResultado().length ) {
							if (columna.equals(indProxy.getIndicador().getResultado()[c])) {
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
	
	/**
	 * Metodod encargado de identificar si un indicador depende de otro indicador o puede ser interpretado
	 * @return
	 */
	public boolean esDependiente()
	{
		boolean esDependiente = false;
		
		//Recorro los parametros del indicador para averiguar si todos su parametros estan interpretados o no
		for (int p = 0; p < this.getIndicador().getParametros().size(); p++) 
		{			
			//Identifico aquellos parametros tipo Indicador, comienzan por #
			if (this.getIndicador().getParametros().get(p).getValor().startsWith(Comunes.tpMarcaIndicador)) 
			{
				String[] tramos = new String [] {};
				tramos = this.getIndicador().getParametros().get(p).getValor().split("\\.");
				String nombreIndicador = tramos[0].substring(1);
				
				//Busco en la lista de indicadores
				IndicadorProxy indProxy = super.getIndicadorNombre(this.getAnalisis(), nombreIndicador);
				
//				if (getIndicadores().containsKey(nombreIndicador))
				if (indProxy!=null)
				{
//					if (getIndicadores().get(nombreIndicador).noejecutado() || getIndicadores().get(nombreIndicador).ejecutando())
					if (super.getIndicadorNombre(this.getAnalisis(), nombreIndicador).noejecutado() || super.getIndicadorNombre(this.getAnalisis(), nombreIndicador).ejecutando())
					{
						esDependiente = true;
						break;
					}
				}
			}
		}
		
		return esDependiente;
	}
	
	/**
	 * Metodo encargado de recopilar en una Lista todos los indicadores de los que depende
	 * @param listaInd
	 * @return
	 */
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
				if ((nombreIndicador.equals("CURSOR") || getIndicadorNombre(this.getAnalisis(), nombreIndicador)!=null) && !listaInd.contains(nombreIndicador)) 
				{
					listaInd.add(nombreIndicador);
					log.info(cabeceralog+" Añadimos a la lista de indicadores dependientes:"+nombreIndicador);
					//Llamamos recursivamente a este metodo hasta llegar al nivel de profundidad necesario y almacenando en un lista todos aquellos indicadores que esten asociados al indicador inicial
//					getIndicadores().get(nombreIndicador).ObtenerIndicadorAsociado(listaInd);
					super.getIndicadorNombre(this.getAnalisis(), nombreIndicador).ObtenerIndicadorAsociado(listaInd);
				}
			}
		}
		
		return listaInd;
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
