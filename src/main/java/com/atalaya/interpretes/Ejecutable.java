package com.atalaya.interpretes;

import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;


/** 
 * Esta clase abstracta que define los atributos y metodos comunes de los ejecutables que componen un analisis (analisis,criterio,indicador y evento) 
 * Atributos del ejecutable. Son comunes a todos los ejecutables que pueden componer un analisis
 */
public abstract class Ejecutable {
	
	public final static String ESTADO_NOEJEUCTADO = "NOEJECUTADO";		//Estado inical del ciclo de vida de un ejecutable
	public final static String ESTADO_EJECUTANDO = "EJECUTANDO";		//Estado de paso del ciclo de vida de un ejecutable
	public final static String ESTADO_VALIDANDO = "VALIDANDO";
	public final static String ESTADO_EJECUTADO = "EJECUTADO";			//Estado final del ciclo de vida de un ejecutable
	
	public final static String FIN_OK = "FIN OK";								//Esta descripción indica una ejecución satisfactoria sin error ni parada forzada
	public final static String FIN_OK_SIN_RESULTADO = "FIN OK SIN RESULTADOS";	//Esta descripción indica una ejecución satisfactoria sin error ni parada forzada
	public final static String FIN_FORZADO = "FIN FORZADO";						//Esta descripción indica una ejecución satisfactoria sin error ni parada forzada
	
	public final static int minHilos = 2;
	
	public final static String VOLCADO_HTML = "HTML";
		
	private volatile String estado;								//Almacena el estado en el que se encuentra el analisis
	private volatile String descripcion_estado;					//Almacena la descripcion del estado en el que se encuentra el analisis
	private long crono;											//Almacena el momento en el que comienza la interpretacion un ejecutable

	protected static int numHilos = 5;								//Define el numero de hilos definido para la ejecucion
	protected static int tiempo_max = 600000;							//Define el tiempo maximo de ejecucion de un ejecutable
	
	protected static Logger log;								//log
	private static long count = 0;								//Almacena un contador de instancias para la generacion del codigo hash del objeto
	private long hashCode;										//Almacena el codigo hash creado para la instancia del objeto

	protected static Thread[] hilos;												//Almacena los hilos lanzados por los analisis (se trata de un atributo de clase)
	protected static Hashtable<Long,Hashtable<String,IndicadorProxy>> indicadores;	//Almacena los indicadores de un alias, los ejecutables manejan la informacion obtenida en los indicadores
	private Vector<Object[]> resultadoEjecucion;									//Almacena el resultado
	
	
	public int hashCode()
	{
		count++;
		int hash = (count + this.getClass().getSimpleName()).hashCode(); 
		return  hash;
	}
	
	public Hashtable<Long,Hashtable<String,IndicadorProxy>> getIndicadores() {
		if (indicadores == null ) {
			indicadores = new Hashtable<Long,Hashtable<String,IndicadorProxy>>();
		}
		return indicadores;
	}
	
	public synchronized String getEstado() {
		return this.estado;
	}

	public synchronized void setEstado(String estado) {
		this.estado = estado;
	}
	
	public boolean noejecutado() {
		boolean noejecutado = false;
		if (this.getEstado().equals(ESTADO_NOEJEUCTADO))
			noejecutado=true;
		
		return noejecutado;
	}
	
	public boolean ejecutado() {
		boolean ejecutado = false;
		if (this.getEstado().equals(ESTADO_EJECUTADO))
			ejecutado=true;
		
		return ejecutado;
	}
	
	public boolean ejecutando() {
		boolean ejecutando = false;
		if (this.getEstado().equals(ESTADO_EJECUTANDO))
			ejecutando=true;
		
		return ejecutando;
	}
	
	public synchronized String getDescripcionEstado() {
		
		if (this.descripcion_estado!=null)
			return this.descripcion_estado;
		else
			return "SIN INFORMAR";
	}

	public synchronized void setDescripcionEstado(String desc_estado) {
		this.descripcion_estado = desc_estado;
	}
	
	public long getCrono() {
		return crono;
	}

	public void setCrono(long crono) {
		this.crono = crono;
	}
	
	public long getHashCode() {
		return hashCode;
	}
	
	public void setHashCode(long hash) {
		this.hashCode = hash;
	}
	
	public Vector<Object[]> getResultadoEjecucion() {
		return resultadoEjecucion;
	}
	
	public void setIndicadores(Hashtable<Long,Hashtable<String,IndicadorProxy>> indicadores) {
		Ejecutable.indicadores = indicadores;
	}

	public void setResultadoEjecucion(Vector<Object[]> resultadoEjecucion) {
		this.resultadoEjecucion = resultadoEjecucion;
	}
	
	public abstract boolean ejecutar();

	public void detener()
	{
		this.setEstado(ESTADO_EJECUTADO);
		this.setDescripcionEstado(FIN_FORZADO);
		
		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public String volcadoResultado (String modo)
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
						volcado.append("\n");
				}
			}
		}
		//else 
		//	volcado = volcado.append("Sin Resultados\n");
		
		log.info("Volcado:");
		log.info(volcado.toString());
		
		return volcado.toString();
	}
	
	public synchronized int infoHilos(String opcion)
	{
		int nThreadsOcupados = 0;
		int nPosicionThreadLibre = 0;
		
		if (Ejecutable.hilos!=null && Ejecutable.hilos.length>0)
		{
			nThreadsOcupados = 0;
			for(int j=0; j<Ejecutable.hilos.length;j++)
			{
				if (Ejecutable.hilos[j]!=null && Ejecutable.hilos[j].isAlive())
				{
					nThreadsOcupados++;
				}
				else
					nPosicionThreadLibre = j;
			}
		}
		else
			nThreadsOcupados = 0;
		
		if (opcion==null || opcion.equals("posicionlibre"))
			return nPosicionThreadLibre;
		else
			return nThreadsOcupados;
	}
	
	public synchronized boolean nuevoHilo (Runnable ejecutable)
	{
		boolean nuevoHilo = false;
		
		//Obtenemos los hilos libres
		int nThreadsOcupados = 0;
		
		nThreadsOcupados = infoHilos("ocupados");
		
		//Si existen hilos libres 
		if (numHilos - nThreadsOcupados>0)
		{
			Thread th = new Thread(ejecutable);
			Ejecutable.hilos[infoHilos("posicionlibre")] = th;
			log.info("Arranco nuevo hilo. Quedan:"+(numHilos-nThreadsOcupados));
			th.start();
			
			nuevoHilo = true;
		}
		
		return nuevoHilo;
	}
	
	public IndicadorProxy getIndicadorNombre(long analisis, String nombre) {
		IndicadorProxy indProxy = null;
		
		if (indicadores!=null && indicadores.size()>0)
		{	
			Hashtable<String,IndicadorProxy> lista =indicadores.get(analisis);
			if (lista!=null && lista.size()>0)	
				indProxy = lista.get(nombre);
		}
		
		return indProxy;
	}
}
