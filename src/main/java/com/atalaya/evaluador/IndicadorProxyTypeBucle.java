package com.atalaya.evaluador;

import java.util.Vector;

import com.modelodatos.Indicador;

public class IndicadorProxyTypeBucle extends IndicadorProxyType implements IIndicadorProxyType  {
	
	private static IndicadorProxyTypeBucle instance;
	private int num_threads_max = 5;
	/** 
	 * Constructor.
	 */
	protected IndicadorProxyTypeBucle(){}
	
	/** 
	 * Este metodo asegura que exista únicamente una instancia de esta clase. Devuelve el valor del atributo instance en caso de tener valor y si no es
	 * así genera una instancia de la clase y la devuelve como resultado.
	 * @return AliasTypeLoop Instancia única de la clase AliasTypeLoop
	 */
	public static IndicadorProxyTypeBucle getInstance()
	{
		//Si el atributo instance no tiene valor significa que no existe ninguna instancia de la clase
		if (instance==null)
		{
			//Da valor al atributo instance, creando una instancia de la clase
			instance = new IndicadorProxyTypeBucle();
		}
		//Devuelve la instancia unica de la clase AliasTypeLoop
		return instance;
	}
	
	public boolean ejecutar(IndicadorProxy indicador) {
		
		boolean exec = false;
		
		String comando = indicador.getIndicador().getComando();
		String[] arComando = comando.split(" ");
		
		Vector<Object[]> vResultado = new Vector<Object[]>();
		
		try
		{			
			//Si el bucle es del tipo Bucle, debo recorrer todos los elementos del indicador A 
			if (arComando[0].equals(IIndicadorProxyType.comando_bucle))
			{
				//Recupero las caracterisiticas del indicador A
				IndicadorProxy indicador_a = IndicadorProxy.getIndicadoresProxy().get(arComando[1].substring(1));
				//Ejecuto el alias_A
				indicador_a.ejecutar();
				
				//Valido el resultado de ejecutar el indicador A
				//Si hay resultados que recorrer
				if (indicador_a.ejecutado() && 
					indicador_a.getResultadoEjecucion()!=null) 
				{
					//Recupero las caracterisiticas del indicador B
					IndicadorProxy indicador_b = IndicadorProxy.getIndicadoresProxy().get(arComando[3].substring(1));
					
					//Recupero el resultado del indicador A
					Vector<Object[]> resultado_indicador_a = (Vector<Object[]>)indicador_a.getResultadoEjecucion();
					
					int numLanzados = 0;
					IndicadorProxy indicador_auto = null;
					
					//List<Thread> threads = new ArrayList<Thread>();
					Thread[] threads = new Thread[num_threads_max];
					
					//Recuperamos el indicador y lo clonamos para utilizarlo como semilla en el bucle
					Indicador ind_b = super.copiaIndicador(indicador_b.getIndicador());
							
					while (numLanzados<resultado_indicador_a.size())
					{
						Indicador copia_ind_b = super.copiaIndicador(ind_b);
						//Obtenemos los hilos libres
						int nThreadsOcupados = 0;
						int nPosicionThreadLibre = 0;
						
						if (threads!=null && threads.length>0) 
						{
							nThreadsOcupados = 0;
							for(int j=0; j<threads.length;j++)
							{
								if (threads[j]!=null && threads[j].isAlive())
									nThreadsOcupados++;
								else
									nPosicionThreadLibre = j;
							}
						}
						else
							nThreadsOcupados = 0;
						
						//Si existen hilos libres 
						if (num_threads_max - nThreadsOcupados>0)
						{	
							//creo un indicador hijo con las mismas caracteristicas que el padre
							indicador_auto = new IndicadorProxy(copia_ind_b,true,indicador_a.getIndicador().getNombre());
							
							//Damos valor a los parametros del indicador_b, que estaran basados en los elementos del indicador_a
							for (int p = 0; p < indicador_auto.getIndicador().getParametros().size(); p++) 
							{
								if (indicador_auto.getIndicador().getParametros().get(p).getValor().startsWith(Comunes.tpMarcaIndicador)) {
									
									String[] tramos = new String [] {};
									tramos = indicador_auto.getIndicador().getParametros().get(p).getValor().split("\\.");
									String nombreIndicador = tramos[0].substring(1);
									
									if (nombreIndicador.equals("CURSOR")) 
									{
										String columna = tramos[1];
										Object valParam = new Object();
										Object[] linea = indicador_a.getResultadoEjecucion().elementAt(numLanzados);
										int c = 0;
										while (c < indicador_a.getIndicador().getResultado().length) {
											if (columna.equals(indicador_a.getIndicador().getResultado()[c])) {
												valParam = linea[c];
												break;
											}
											c++;
										}
										indicador_auto.getIndicador().getParametros().get(p).setValor(valParam.toString());
									}
								}
								//AGUJERO ESTO DEBER EVITARSE EN LA FASE DE CHEQUEO DEL ANALISIS Y EVITAR INDICADORES MAL FORMADOS
							}
							
							//Relacionamos el indicador hijo (su padre es el indicador_b) con el elemento correspondiente del indicador_a 
							indicador_auto.setIndice(numLanzados);
							//Ejecuto el indicador
							if (num_threads_max<=1)
								indicador_auto.ejecutar();
							else
							{
								Thread th = new Thread(indicador_auto);
								th.start();
								threads[nPosicionThreadLibre] = th;
								
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							System.out.println("Arranco nuevo hilo para el indicador " +indicador.getIndicador().getNombre()+" | " + indicador.getEstado() + " | " + indicador.getClass() + " | en la posicion " + nPosicionThreadLibre);
							
							numLanzados++;
						}
						else
						{
							Thread.sleep(5000);
							//AGUJERO SI NO HAY HILOS NO PODEMOS ESTAR ESPERANDO ETERNAMENTE, HAY QUE CONFIGURAR UN TIEMPO MAXIMO DE ESPERA PARA EVITAR ABRAZO MORTAL
						}
					}
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//AGUJERO SI NO HAY CASOS HAY QUE CONTROLARLO Y FINALIZAR EL INDICADOR
					indicador.setResultadoEjecucion(indicador_a.getResultadoEjecucion());
					indicador.setEstado(IIndicadorProxy.ESTADO_EJECUTADO);
					
					exec=true;											
				}
			}			
		}
		catch (Exception e)
		{
			indicador.setEstado(IIndicadorProxy.ESTADO_EJECUTADO);
		}
		
		return exec;		
	}

}
