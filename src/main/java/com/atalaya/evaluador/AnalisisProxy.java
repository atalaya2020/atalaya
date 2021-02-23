package com.atalaya.evaluador;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import org.slf4j.LoggerFactory;

//import org.slf4j.LoggerFactory;

import com.modelodatos.Analisis;
import com.modelodatos.Parametro;

/** 
 * Esta clase representa a un componente Analisis ejecutable, contiene la definicion del analisis y los atributos y metodos necesarios para 
 * su interpretacion. 
 */
public class AnalisisProxy extends Ejecutable implements Runnable {

	private Analisis analisis;									//Almacena la definicion del analisis recuperado de bbdd
	
	private ArrayList<CriterioProxy> criterios;					//Almacena los criterios de un analisis
	private ArrayList<EventoProxy> eventos;						//Almacena los eventos de un analisis
	private ArrayList<Parametro> parametros;					//Almacena los parametros a utilizar para interpretar un analisis
	
	String cabeceralog;
	
	public ArrayList<CriterioProxy> getCriterios() {
		return criterios;
	}
	
	public void setCriterios(ArrayList<CriterioProxy> criterios) {
		this.criterios = criterios;
	}	
	
	public ArrayList<EventoProxy> getEventos() {
		return eventos;
	}
	
	public void setEventos(ArrayList<EventoProxy> eventos) {
		this.eventos = eventos;
	}	
	
	public ArrayList<Parametro> getParametros() {
		return parametros;
	}
	public void setParametros(ArrayList<Parametro> parametros) {
		this.parametros = parametros;
	}
	
	public Analisis getAnalisis() {
		return analisis;
	}
	
	public AnalisisProxy (Analisis analisis) 
	{			
		this.parametros = new ArrayList<Parametro>();
		cargarAnalisisProxy(analisis);
		
		setEstado(ESTADO_NOEJEUCTADO);
		
		hilos = new Thread[numHilos];
		setHashCode(hashCode());
		log = LoggerFactory.getLogger(AnalisisProxy.class);
		
		cabeceralog = "Analisis " +this.getAnalisis().getNombre() + "|" + this.getHashCode() + ":";
	}
	
	public AnalisisProxy (Analisis analisis, ArrayList<Parametro> parametros) 
	{
		this.parametros = parametros;
		this.analisis = analisis;
		
		setEstado(ESTADO_NOEJEUCTADO);
		
		cargarAnalisisProxy(analisis);
		hilos = new Thread[numHilos];
		setHashCode(hashCode());
		log = LoggerFactory.getLogger(AnalisisProxy.class);
		
		cabeceralog = "Analisis "+ this.getAnalisis().getNombre() + "|" + this.getHashCode() + ":";
	}
	
	//Este metodo tendria mas sentido si le metemos la validacion de cada uno de los elementos del analisis
	private void cargarAnalisisProxy(Analisis analisis) {
		
		this.criterios = new ArrayList<CriterioProxy>();	
		for (int i = 0; i < analisis.getCriterios().size(); i++) {
			CriterioProxy criProxy = new CriterioProxy(analisis.getCriterios().get(i));
			this.criterios.add(i, criProxy);
		}
		
		setIndicadores(new Hashtable<String,IndicadorProxy>());
		for (int i = 0; i < analisis.getIndicadores().size(); i++) {
			IndicadorProxy indProxy = new IndicadorProxy(analisis.getIndicadores().get(i));
			
			//Recorro los parametros del indicador para identificar aquellos que hacen referencia a un parametro de entrada #PARAM.NOMBRE_PARAMETRO
			ArrayList<Parametro> parametros = indProxy.getIndicador().getParametros();
			for(int j=0;j<parametros.size();j++)
			{
				//Si el parametro es del tipo #PARAM debo darle el valor recibido como parametro
				if (parametros.get(j).getValor().startsWith("#PARAM"))
				{
					String[] arValor = parametros.get(j).getValor().split("\\.");
					if (this.parametros!=null)
					{
						for (int k=0;k<this.parametros.size();k++)
						{
							if (this.parametros.get(k).getNombre().equals(arValor[1]))
								parametros.get(j).setValor(this.parametros.get(k).getValor());
						}
					}
				}
			}
			
			super.getIndicadores().put(analisis.getIndicadores().get(i).getNombre(), indProxy);
		}
		
		this.eventos = new ArrayList<EventoProxy>();
		for (int i = 0; i < analisis.getEventos().size(); i++) {
			EventoProxy eveProxy = new EventoProxy(analisis.getEventos().get(i));
			this.eventos.add(i, eveProxy);
		}
		
	}
	
	public void run()
	{
		ejecutar();
	}
	
	public boolean ejecutar() {
		
		if (this.noejecutado())
		{
			this.setEstado(ESTADO_EJECUTANDO);
			setCrono(Calendar.getInstance().getTimeInMillis());
			log.info(cabeceralog + "Ejecutando analizador...");
			log.info(cabeceralog + this.getAnalisis().getDescripcion());
			
			//Bucle para el control del ciclo de vida del hilo
			while(!this.ejecutado())
			{	
				//Validar si se han superado los tiempos maximos de espera en los indicadores
				Enumeration<String> enumIndicadores = this.getIndicadores().keys();
				long ahora = Calendar.getInstance().getTimeInMillis();
				//Recorremos todos los indicadores lanzados para este Analisis
				while(enumIndicadores.hasMoreElements())
				{
					String nombreIndicador = (String)enumIndicadores.nextElement();
					//Paramos aquellos indicadores que han superado el tiempo maximo de ejecucion
					if (this.getIndicadores().get(nombreIndicador).ejecutando())
					{
						IndicadorProxy ind = this.getIndicadores().get(nombreIndicador);
						long time = ind.getCrono();
						if ((ahora - this.getIndicadores().get(nombreIndicador).getCrono()) > tiempo_max) //TAREA añadir nuevo parametro de ejecucion a nivel de indicador TIEMPO MAX DE EJECUCION
						{
							//Forzamos su parada por haber supero del tiempo maximo de ejeucion
							this.getIndicadores().get(nombreIndicador).detener();
							log.info(cabeceralog+"Parado el indicador:" +nombreIndicador+ " por sobrepasar el tiempo maximo de ejecucion "+ tiempo_max);
						}
					}
				}
					
				int numCriEjecutados = 0;
				int numCriEjecutadosOk = 0;
				int numCriEjecutadosFinForzado = 0;

				//Para interpretar un analisis el punto de partida son los criterios donde se definen las condiciones de evalucion para finalmente lanzar o no los eventos correspondientes
				//Recorro los criterios para ejecutarlos				
				for (int c = 0; c < analisis.getCriterios().size(); c++) 
				{	
					CriterioProxy criterio = this.getCriterios().get(c);
					
					//Recorro todos los criterios para validar si todos han sido ejecutados
					if (criterio.ejecutado())
						numCriEjecutados++;
					if (criterio.getDescripcionEstado()==FIN_OK)
					{
						numCriEjecutadosOk++;
						
						eventosCriterio(criterio);
						
					}
					else if (criterio.getDescripcionEstado()==FIN_FORZADO)
						numCriEjecutadosFinForzado++;
					
					//Si se han ejecutado todos los criterios damos por ejecutado el analisis
					if (numCriEjecutados==this.getCriterios().size())
					{
						this.setEstado(ESTADO_EJECUTADO);
						if (numCriEjecutadosOk==this.getCriterios().size())
							this.setDescripcionEstado(FIN_OK);
						else if (numCriEjecutadosFinForzado>0)
							this.setDescripcionEstado(FIN_FORZADO);
						else
							this.setDescripcionEstado(FIN_OK_SIN_RESULTADO);
						
						break;
					}
					
					//Paramos aquellos criterios que han superado el tiempo maximo de ejecucion
					if ((ahora - criterio.getCrono()) > tiempo_max) //TAREA añadir nuevo parametro de ejecucion a nivel de indicador TIEMPO MAX DE EJECUCION
					{
						//Forzamos su parada por haber superado el tiempo maximo de ejeucion
						if (criterio.ejecutando())
						{
							criterio.detener();
							log.info(cabeceralog+"Parado el criterio:" +criterio.getCriterio().getNombre()+ " por sobrepasar el tiempo maximo de ejecucion "+ tiempo_max);
							continue;
						}
					}
					
					if (criterio.noejecutado())
					{
						//Ejecuto el indicador
						if (super.numHilos<=minHilos)
							criterio.ejecutar();
						else
							super.nuevoHilo(criterio);
					}
				}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
			
			log.info(cabeceralog + "Finalizada la ejeucion del analisis.");
		}
		
		return true;
	}
	
	public String volcadoResultado(String modo)
	{
		String volcado = "";
		
		volcado = volcado + "Nombre: " + this.getAnalisis().getNombre() + "\n";
		volcado = volcado + "Estado: " + this.getEstado() + "\n";
		volcado = volcado + "Descripcion del estado: " + this.getDescripcionEstado() + "\n";
		volcado = volcado + super.volcadoResultado(modo);
		
		return volcado;
	}
	
	public void eventosCriterio (CriterioProxy criProxy) {
		// Recorre la lista de nombres de eventos del criterio para identificar y generar los eventos definidos en el analisis.
			for (int e = 0; e < this.getEventos().size(); e++) {
				for (int ec = 0; ec < criProxy.getCriterio().getEventos().length; ec++) {
					if ( this.getEventos().get(e).getEvento().getNombre().equalsIgnoreCase(criProxy.getCriterio().getEventos()[ec])) {
						if (this.getEventos().get(e).getEvento().getTipo().equals("VolcadoIndicador"))
						{
							String[] tramos = this.getEventos().get(e).getEvento().getComando().split(" ");
							this.getEventos().get(e).setResultadoEjecucion(this.getIndicadores().get(tramos[1].substring(1)).volcadoResultado("plano"));
						}
						else
						{
							parametrosEvento(this.getEventos().get(e));
							this.getEventos().get(e).generarEvento();
						}
					}
				}
			}
		}
		
		private void parametrosEvento(EventoProxy eventoPrx) {
			
			for (int p = 0; p < eventoPrx.getEvento().getParametros().size(); p++) {			
				if (eventoPrx.getEvento().getParametros().get(p).getValor().startsWith(Comunes.tpMarcaIndicador)) {
					
					String[] tramos = new String [] {};
					tramos = eventoPrx.getEvento().getParametros().get(p).getValor().split("\\.");
					String nombreIndicador = tramos[0].substring(1);
					
					if (this.getIndicadores().containsKey(nombreIndicador))
					{
						if ((this.getIndicadores().get(nombreIndicador).getResultadoEjecucion()!=null) && this.getIndicadores().get(nombreIndicador).getResultadoEjecucion().size()>0) 
						{					
							String columna = tramos[1];
							Object valParam = new Object();
							Object[] linea = this.getIndicadores().get(nombreIndicador).getResultadoEjecucion().elementAt(0);
							int c = 0;
							while (c < this.getIndicadores().get(nombreIndicador).getIndicador().getResultado().length ) {
								if (columna.equals(this.getIndicadores().get(nombreIndicador).getIndicador().getResultado()[c])) {
									valParam = linea[c];
									break;
								}
								c++;							
							}
							eventoPrx.getEvento().getParametros().get(p).setValor(valParam.toString());					
						}
					}
				}
			}
		}
}
