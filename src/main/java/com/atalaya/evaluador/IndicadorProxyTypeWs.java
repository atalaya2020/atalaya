package com.atalaya.evaluador;

import java.util.Vector;
import org.springframework.web.client.RestTemplate;

public class IndicadorProxyTypeWs implements IIndicadorProxyType {
	
	private static IndicadorProxyTypeWs instance;
	
	/** 
	 * Constructor.
	 */
	protected IndicadorProxyTypeWs(){}
	
	/** 
	 * Este metodo asegura que exista únicamente una instancia de esta clase. Devuelve el valor del atributo instance en caso de tener valor y si no es
	 * así genera una instancia de la clase y la devuelve como resultado.
	 * @return AliasTypeLoop Instancia única de la clase AliasTypeLoop
	 */
	public static IndicadorProxyTypeWs getInstance()
	{
		//Si el atributo instance no tiene valor significa que no existe ninguna instancia de la clase
		if (instance==null)
		{
			//Da valor al atributo instance, creando una instancia de la clase
			instance = new IndicadorProxyTypeWs();
		}
		//Devuelve la instancia unica de la clase AliasTypeLoop
		return instance;
	}
	
	public boolean ejecutar(IndicadorProxy indicador) {
		
		boolean exec = false;
		
		try
		{			
			String sComando = indicador.getIndicador().getComando();
			
			for (int i=0;i<indicador.getIndicador().getParametros().size();i++)
			{
				sComando = sComando.replaceFirst("-param-", indicador.getIndicador().getParametros().get(i).getValor());
			}
			
			//String sUrl = "http://wsValoracion:8080/"+sComando;
			String sResultado = "";
			
			try
			{
				sResultado =  new RestTemplate().getForObject(sComando, String.class);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			Vector<Object[]> vResultado = new Vector<Object[]>();
			vResultado.add(new Object[] {sResultado});
			indicador.setResultadoEjecucion(vResultado);
			
			try
			{
				indicador.setResultadoEjecucion(vResultado);
				exec = true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			indicador.setEstado(IIndicadorProxy.ESTADO_EJECUTADO);
		}
		catch (Exception e)
		{
			indicador.setEstado(IIndicadorProxy.ESTADO_EJECUTADO);
		}
		
		return exec;
	}

}
